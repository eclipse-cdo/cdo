/*
 * Copyright (c) 2009-2013, 2015-2020, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.evolution.phased.Context;
import org.eclipse.emf.cdo.server.db.evolution.phased.ISchemaMigration;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IFeatureMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.IObjectTypeMapper;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;

import org.eclipse.net4j.db.BatchingIDChanger;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.DeserializeRowHandler;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.StatementBatcher;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.IDChanger;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.Holder;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.PropertiesEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * * This abstract base class refines {@link AbstractMappingStrategy} by implementing aspects common to horizontal
 * mapping strategies -- namely:
 * <ul>
 * <li>object type cache (table cdo_objects)
 * <li>resource query handling
 * </ul>
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractHorizontalMappingStrategy extends AbstractMappingStrategy implements ISchemaMigration
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractHorizontalMappingStrategy.class);

  private static final EAttribute[] NO_PERSISTENT_LOB_ATTRIBUTES = {};

  private ObjectTypeTable objects;

  /**
   * The associated object type mapper.
   */
  private IObjectTypeMapper objectTypeMapper;

  private IClassMapping resourceFolderMapping;

  private IClassMapping modelResourceMapping;

  private IClassMapping textResourceMapping;

  private IClassMapping binaryResourceMapping;

  public AbstractHorizontalMappingStrategy()
  {
  }

  public final ObjectTypeTable objects()
  {
    return objects;
  }

  public IObjectTypeMapper getObjectTypeMapper()
  {
    return objectTypeMapper;
  }

  @Override
  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    EClass type = readObjectType2(accessor, id);
    if (type == null)
    {
      return null;
    }

    return new CDOClassifierRef(type);
  }

  @Override
  public EClass readObjectType2(IDBStoreAccessor accessor, CDOID id)
  {
    return objectTypeMapper.getObjectType(accessor, id);
  }

  public boolean putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    return objectTypeMapper.putObjectType(accessor, timeStamp, id, type);
  }

  public boolean removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return objectTypeMapper.removeObjectType(accessor, id);
  }

  @Override
  public void repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    IDBStore store = getStore();
    IRepository repository = store.getRepository();
    if (repository.getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      IIDHandler idHandler = store.getIDHandler();

      if (repository.isSupportingBranches())
      {
        CDOID minLocalID = getMinLocalID(connection);
        idHandler.setNextLocalObjectID(minLocalID);
      }

      CDOID maxID = objectTypeMapper.getMaxID(connection, idHandler);
      idHandler.setLastObjectID(maxID);
    }
  }

  @Override
  public void queryResources(IDBStoreAccessor accessor, QueryResourcesContext context)
  {
    // Only support timestamp in audit mode.
    if (context.getTimeStamp() != CDORevision.UNSPECIFIED_DATE && !hasAuditSupport())
    {
      throw new IllegalArgumentException("Mapping Strategy does not support audits"); //$NON-NLS-1$
    }

    if (resourceFolderMapping == null)
    {
      resourceFolderMapping = getClassMapping(EresourcePackage.eINSTANCE.getCDOResourceFolder());
      modelResourceMapping = getClassMapping(EresourcePackage.eINSTANCE.getCDOResource());
      textResourceMapping = getClassMapping(EresourcePackage.eINSTANCE.getCDOTextResource());
      binaryResourceMapping = getClassMapping(EresourcePackage.eINSTANCE.getCDOBinaryResource());
    }

    boolean shallContinue = true;

    // First query folders.
    if (shallContinue)
    {
      shallContinue = queryResources(accessor, resourceFolderMapping, context);
    }

    // Not enough results? -> Query resources.
    if (shallContinue)
    {
      shallContinue = queryResources(accessor, modelResourceMapping, context);
    }

    // Not enough results? -> Query text resources.
    if (shallContinue)
    {
      shallContinue = queryResources(accessor, textResourceMapping, context);
    }

    // Not enough results? -> Query binary resources.
    if (shallContinue)
    {
      shallContinue = queryResources(accessor, binaryResourceMapping, context);
    }
  }

  @Override
  public void queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context)
  {
    IIDHandler idHandler = getStore().getIDHandler();
    StringBuilder builder = null;

    // create a string containing "(id1,id2,...)"
    // NOTE: this might not scale infinitely, because of dbms-dependent
    // max size for SQL strings. But for now, it's the easiest way...
    for (CDOID targetID : context.getTargetObjects().keySet())
    {
      // NOTE: currently no support for external references!
      if (builder == null)
      {
        builder = new StringBuilder("(");
      }
      else
      {
        builder.append(",");
      }

      idHandler.appendCDOID(builder, targetID);
    }

    builder.append(")");
    String idString = builder.toString();

    for (EClass eClass : context.getSourceCandidates().keySet())
    {
      IClassMapping classMapping = getClassMapping(eClass);
      boolean more = classMapping.queryXRefs(accessor, context, idString);
      if (!more)
      {
        // cancel query (max results reached or user canceled)
        return;
      }
    }
  }

  @Override
  public void migrateSchema(Context context, IDBStoreAccessor accessor) throws SQLException
  {
    ModelEvolutionHelper helper = new ModelEvolutionHelper(context);
    helper.evolveSchema();
    helper.evolveUsedClasses(accessor);
  }

  @Override
  public void rawExport(IDBStoreAccessor accessor, CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    StringBuilder builder = new StringBuilder();
    builder.append(" WHERE a_t."); //$NON-NLS-1$
    builder.append(DBUtil.quoted(MappingNames.ATTRIBUTES_CREATED));
    builder.append(" BETWEEN "); //$NON-NLS-1$
    builder.append(fromCommitTime);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(toCommitTime);

    String attrSuffix = builder.toString();
    IDBConnection connection = accessor.getDBConnection();

    Collection<IClassMapping> classMappings = getClassMappings(true).values();
    out.writeXInt(classMappings.size());

    for (IClassMapping classMapping : classMappings)
    {
      EClass eClass = classMapping.getEClass();
      out.writeCDOClassifierRef(eClass);

      IDBTable table = classMapping.getTable();
      DBUtil.serializeTable(out, connection, table, "a_t", attrSuffix);

      for (IListMapping listMapping : classMapping.getListMappings())
      {
        rawExportList(out, connection, listMapping, table, attrSuffix);
      }
    }

    objectTypeMapper.rawExport(connection, out, fromCommitTime, toCommitTime);
  }

  protected void rawExportList(CDODataOutput out, IDBConnection connection, IListMapping listMapping, IDBTable attrTable, String attrSuffix) throws IOException
  {
    for (IDBTable table : listMapping.getDBTables())
    {
      String listSuffix = ", " + attrTable + " a_t" + attrSuffix;
      String listJoin = getListJoinForRawExport("a_t", "l_t");
      if (listJoin != null)
      {
        listSuffix += listJoin;
      }

      DBUtil.serializeTable(out, connection, table, "l_t", listSuffix);
    }
  }

  protected String getListJoinForRawExport(String attrTable, String listTable)
  {
    return getListJoin(attrTable, listTable);
  }

  @Override
  public void rawImport(IDBStoreAccessor accessor, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    int size = in.readXInt();
    if (size == 0)
    {
      return;
    }

    int objectTypeMapperWork = 10;
    monitor.begin(3 * size + objectTypeMapperWork);

    try
    {
      IDBConnection connection = accessor.getDBConnection();

      for (int i = 0; i < size; i++)
      {
        EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
        IClassMapping classMapping = getClassMapping(eClass);

        // IClassMapping classMapping;
        // IDBSchemaTransaction schemaTransaction = null;
        //
        // try
        // {
        // schemaTransaction = accessor.getStore().getDatabase().openSchemaTransaction();
        // classMapping = getClassMapping(eClass);
        // schemaTransaction.commit();
        // }
        // catch (RuntimeException ex)
        // {
        // throw ex;
        // }
        // catch (Error ex)
        // {
        // throw ex;
        // }
        // finally
        // {
        // if (schemaTransaction != null)
        // {
        // schemaTransaction.close();
        // }
        // }

        IDBTable table = classMapping.getTable();
        DBUtil.deserializeTable(in, connection, table, monitor.fork());
        rawImportReviseOldRevisions(connection, table, monitor.fork());
        rawImportUnreviseNewRevisions(connection, table, fromCommitTime, toCommitTime, monitor.fork());

        List<IListMapping> listMappings = classMapping.getListMappings();
        int listSize = listMappings.size();
        if (listSize == 0)
        {
          monitor.worked();
        }
        else
        {
          OMMonitor listMonitor = monitor.fork();
          listMonitor.begin(listSize);

          try
          {
            for (IListMapping listMapping : listMappings)
            {
              rawImportList(in, connection, listMapping, listMonitor.fork());
            }
          }
          finally
          {
            listMonitor.done();
          }
        }
      }

      objectTypeMapper.rawImport(connection, in, monitor.fork(objectTypeMapperWork));
    }
    finally
    {
      monitor.done();
    }
  }

  protected void rawImportUnreviseNewRevisions(IDBConnection connection, IDBTable table, long fromCommitTime, long toCommitTime, OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Must be overridden");
  }

  protected void rawImportReviseOldRevisions(IDBConnection connection, IDBTable table, OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Must be overridden");
  }

  protected void rawImportList(CDODataInput in, IDBConnection connection, IListMapping listMapping, OMMonitor monitor) throws IOException
  {
    Collection<IDBTable> tables = listMapping.getDBTables();
    int size = tables.size();
    if (size == 0)
    {
      return;
    }

    monitor.begin(size);

    try
    {
      for (IDBTable table : tables)
      {
        DBUtil.deserializeTable(in, connection, table, monitor.fork(), getImportListHandler());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected DeserializeRowHandler getImportListHandler()
  {
    // Only needed with ranges
    return null;
  }

  @Override
  public String getListJoin(String attrTable, String listTable)
  {
    return " AND " + attrTable + "." + DBUtil.quoted(MappingNames.ATTRIBUTES_ID) + "=" + listTable + "." + DBUtil.quoted(MappingNames.LIST_REVISION_ID);
  }

  @Override
  protected boolean isMapped(EClass eClass)
  {
    return !eClass.isAbstract() && !eClass.isInterface();
  }

  @Override
  protected Collection<EClass> getClassesWithObjectInfo()
  {
    return getClassMappings().keySet();
  }

  @Override
  protected EAttribute[] getPersistentLobAttributes(EClass eClass)
  {
    if (!eClass.isAbstract() && !eClass.isInterface())
    {
      return CDOModelUtil.getClassInfo(eClass).getAllPersistentLobAttributes();
    }

    return NO_PERSISTENT_LOB_ATTRIBUTES;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (objectTypeMapper == null)
    {
      objectTypeMapper = createObjectTypeMapper();
    }

    LifecycleUtil.activate(objectTypeMapper);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(objectTypeMapper);

    // They will be deactivated in super.deactivateClassMappings();
    resourceFolderMapping = null;
    modelResourceMapping = null;
    textResourceMapping = null;
    binaryResourceMapping = null;

    super.doDeactivate();
  }

  private IObjectTypeMapper createObjectTypeMapper()
  {
    objects = new ObjectTypeTable(getStore());

    int cacheSize = getObjectTypeCacheSize();
    if (cacheSize == 0)
    {
      return objects;
    }

    ObjectTypeCache cache = new ObjectTypeCache(cacheSize);
    cache.setMappingStrategy(this);
    cache.setDelegate(objects);
    return cache;
  }

  private int getObjectTypeCacheSize()
  {
    int objectTypeCacheSize = ObjectTypeCache.DEFAULT_CACHE_CAPACITY;

    Object value = getProperties().get(Props.OBJECT_TYPE_CACHE_SIZE);
    if (value != null)
    {
      try
      {
        int intValue = Integer.parseInt((String)value);
        objectTypeCacheSize = intValue;
      }
      catch (NumberFormatException e)
      {
        OM.LOG.warn("Malformed configuration option for object type cache size. Using default.");
      }
    }

    return objectTypeCacheSize;
  }

  /**
   * This is an intermediate implementation. It should be changed after class mappings support a general way to implement
   * queries ...
   *
   * @param accessor
   *          the accessor to use.
   * @param classMapping
   *          the class mapping of a class instanceof {@link CDOResourceNode} which should be queried.
   * @param context
   *          the query context containing the parameters and the result.
   * @return <code>true</code> if result context is not yet full and query should continue false, if result context is
   *         full and query should stop.
   */
  private boolean queryResources(IDBStoreAccessor accessor, IClassMapping classMapping, QueryResourcesContext context)
  {
    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    IIDHandler idHandler = getStore().getIDHandler();
    PreparedStatement stmt = classMapping.createResourceQueryStatement(accessor, folderID, name, exactMatch, context);
    if (stmt == null)
    {
      return true;
    }

    ResultSet resultSet = null;

    try
    {
      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Resource query returned ID " + id); //$NON-NLS-1$
        }

        if (!context.addResource(id))
        {
          // No more results allowed
          return false; // don't continue
        }
      }

      return true; // continue with other results
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  private CDOID getMinLocalID(Connection connection)
  {
    final IIDHandler idHandler = getStore().getIDHandler();
    final CDOID[] min = { idHandler.getMaxCDOID() };

    String id = DBUtil.quoted(MappingNames.ATTRIBUTES_ID);
    String branch = DBUtil.quoted(MappingNames.ATTRIBUTES_BRANCH);
    String created = DBUtil.quoted(MappingNames.ATTRIBUTES_CREATED);

    String prefix = "SELECT MIN(t." + id + ") FROM " + objects + " o, ";
    String suffix = " t WHERE t." + branch + "<0 AND t." + id + "=o." + id + " AND t." + created + "=o." + created;

    getStore().visitAllTables(connection, (c, name) -> {
      Statement stmt = null;
      ResultSet resultSet = null;

      try
      {
        stmt = connection.createStatement();
        resultSet = stmt.executeQuery(prefix + name + suffix);

        if (resultSet.next())
        {
          CDOID id1 = idHandler.getCDOID(resultSet, 1);
          if (id1 != null && idHandler.compare(id1, min[0]) < 0)
          {
            min[0] = id1;
          }
        }
      }
      finally
      {
        DBUtil.close(resultSet);
        DBUtil.close(stmt);
      }
    });

    return min[0];
  }

  /**
   * @author Eike Stepper
   */
  private final class ModelEvolutionHelper
  {
    private final Context context;

    private final PropertiesEvent event = new PropertiesEvent(AbstractHorizontalMappingStrategy.this)
    {
      @Override
      public PropertiesEvent fire()
      {
        if (!EventUtil.fireEvent(context.getSupport(), this))
        {
          fireEvent(this);
        }

        return this;
      }

      @Override
      protected String formatEventName()
      {
        return "SchemaMigrationEvent";
      }
    };

    public ModelEvolutionHelper(Context context)
    {
      this.context = context;

    }

    public void evolveSchema() throws SQLException
    {
      event.setType("StartingSchemaMigration").fire();

      Holder<IDBSchemaTransaction> schemaTransactionHolder = new Holder<>(() -> {
        event.setType("OpeningSchemaTransaction").fire();
        IDBSchemaTransaction schemaTransaction = context.getSupport().getStore().getDatabase().openSchemaTransaction();
        event.setType("OpenedSchemaTransaction").addProperty("schemaTransaction", schemaTransaction).fire();
        return schemaTransaction;
      });

      Supplier<IDBSchema> workingCopySupplier = () -> schemaTransactionHolder.get().getWorkingCopy();

      try
      {
        context.getOldPackages().values().forEach(oldPackage -> {
          for (EClassifier oldClassifier : oldPackage.getEClassifiers())
          {
            if (oldClassifier instanceof EClass)
            {
              EClass oldClass = (EClass)oldClassifier;

              if (!isMapped(oldClass))
              {
                // Only concrete classes can have tables in horizontal mapping.
                continue;
              }

              EClass newClass = context.getNewElement(oldClass);
              if (newClass == null || !isMapped(newClass))
              {
                // Class was removed or is no longer mapped.
                continue;
              }

              EStructuralFeature[] addedFeatures = getAddedFeatures(oldClass, newClass);
              if (addedFeatures.length == 0)
              {
                // No new features.
                continue;
              }

              IClassMapping classMapping = doCreateClassMapping(oldClass);
              String tableName = classMapping.getTable().getName();
              IDBTable table = workingCopySupplier.get().getTable(tableName);

              context.log("Creating new feature mappings for class " + EMFUtil.getFullyQualifiedName(oldClass) + " in table " + table + ": "
                  + Arrays.stream(addedFeatures).map(EStructuralFeature::getName).collect(Collectors.joining(", ")));

              AbstractHorizontalClassMapping.createFeatureMappings(AbstractHorizontalMappingStrategy.this, newClass, table, addedFeatures);
            }
          }
        });

        schemaTransactionHolder.handleIfSet(schemaTransaction -> {
          event.setType("CommittingSchemaTransaction").fire();
          schemaTransaction.commit();
          event.setType("CommittedSchemaTransaction").fire().removeProperty("schemaTransaction");
        });
      }
      finally
      {
        schemaTransactionHolder.handleIfSet(IDBSchemaTransaction::close);
      }
    }

    public void evolveUsedClasses(IDBStoreAccessor accessor) throws SQLException
    {
      Map<EClass, String> usedClasses = new HashMap<>();
      Map<String, EObject> fromObjectsByURI = context.getElementMappings().getFromObjectsByURI();

      // Determine used classes and their URIs.
      objects.handleObjectTypes(accessor, //
          (raw, metaID, uri) -> ObjectUtil.ifNotNull((EClass)fromObjectsByURI.get(uri), //
              usedClass -> usedClasses.put(usedClass, raw)));

      event.setType("EvolvingUsedClasses").addProperty("usedClasses", usedClasses).fire();

      try (StatementBatcher batcher = createBatcher(accessor))
      {
        usedClasses.keySet().forEach(eClass -> evolveUsedClass(batcher, usedClasses, eClass));
      }

      event.setType("EvolvedUsedClasses").fire();
    }

    private void evolveUsedClass(StatementBatcher batcher, Map<EClass, String> usedClasses, EClass usedClass)
    {
      event.setType("EvolvingUsedClass").addProperty("usedClass", usedClass).fire();

      Holder<IClassMapping> classMappingHolder = new Holder<>(() -> Objects.requireNonNull(getClassMapping(usedClass)));

      // Change container IDs
      Map<Integer, Integer> containerChanges = new HashMap<>();
      context.handleFeatureIDChanges(usedClass, this::getContainerReferences, //
          (oldFeatureID, newFeatureID) -> containerChanges.put(oldFeatureID, newFeatureID));

      if (!containerChanges.isEmpty())
      {
        context.log("Adjusting container reference IDs for class " + EMFUtil.getFullyQualifiedName(usedClass));
        IDChanger.changeIDs(containerChanges, new ContainerIDChanger(batcher, classMappingHolder.get()));
      }

      // Change opposite container IDs (containments with no navigable opposite).
      handleContainmentCandidates(usedClass, usedClasses.keySet(), (containerClass, containments) -> {
        Map<Integer, Integer> containmentChanges = new HashMap<>();
        context.handleFeatureIDChanges(containerClass, ec -> containments, (oldFeatureID, newFeatureID) -> {
          int oldID = InternalEObject.EOPPOSITE_FEATURE_BASE - oldFeatureID;
          int newID = InternalEObject.EOPPOSITE_FEATURE_BASE - newFeatureID;
          containmentChanges.put(oldID, newID);
        });

        if (!containmentChanges.isEmpty())
        {
          context.log("Adjusting containment reference IDs for class " + EMFUtil.getFullyQualifiedName(usedClass));
          String rawContainerTypeID = usedClasses.get(containerClass);
          IDChanger.changeIDs(containmentChanges, new ContainmentIDChanger(batcher, classMappingHolder.get(), objects, rawContainerTypeID));
        }
      });

      // Accommodate enum literal changes.
      for (EAttribute attribute : usedClass.getEAllAttributes())
      {
        EClassifier type = attribute.getEType();
        if (type instanceof EEnum)
        {
          EEnum oldEnum = (EEnum)type;

          Map<Integer, Integer> changes = context.getEnumLiteralChanges(oldEnum);
          if (!ObjectUtil.isEmpty(changes))
          {
            context.log("Adjusting enum literal IDs for attribute " + EMFUtil.getFullyQualifiedName(usedClass) + "." + attribute.getName());
            IClassMapping classMapping = classMappingHolder.get();
            IFeatureMapping featureMapping = classMapping.getFeatureMapping(attribute);
            IDBField valueField = featureMapping.getField();
            IDChanger.changeIDs(changes, new BatchingIDChanger(batcher, valueField));
          }
        }
      }

      event.setType("EvolvedUsedClass").fire().removeProperty("usedClass");
    }

    private EStructuralFeature[] getAddedFeatures(EClass oldClass, EClass newClass)
    {
      Set<EStructuralFeature> existingNewFeatures = new HashSet<>();

      for (EStructuralFeature oldFeature : CDOModelUtil.getClassInfo(oldClass).getAllPersistentFeatures())
      {
        EStructuralFeature newFeature = context.getNewElement(oldFeature);
        if (newFeature != null)
        {
          existingNewFeatures.add(newFeature);
        }
      }

      List<EStructuralFeature> addedNewFeatures = new ArrayList<>();

      for (EStructuralFeature newFeature : CDOModelUtil.getClassInfo(newClass).getAllPersistentFeatures())
      {
        if (!existingNewFeatures.contains(newFeature))
        {
          addedNewFeatures.add(newFeature);
        }
      }

      return addedNewFeatures.toArray(new EStructuralFeature[0]);
    }

    private List<EStructuralFeature> getContainerReferences(EClass eClass)
    {
      return eClass.getEAllReferences().stream().filter(EReference::isContainer).collect(Collectors.toList());
    }

    private void handleContainmentCandidates(EClass eClass, Collection<EClass> usedClasses, BiConsumer<EClass, List<EReference>> handler)
    {
      usedClasses.forEach(containerClass -> {
        List<EReference> containments = containerClass.getEAllContainments().stream() //
            .filter(ref -> EMFUtil.isPersistent(ref) && ref.getEReferenceType().isSuperTypeOf(eClass)) //
            .collect(Collectors.toList());
        if (!containments.isEmpty())
        {
          handler.accept(containerClass, containments);
        }
      });
    }

    private StatementBatcher createBatcher(IDBStoreAccessor accessor) throws SQLException
    {
      StatementBatcher batcher = context.createStatementBatcher(accessor.getConnection());
      event.setType("CreatedStatementBatcher").addProperty("batcher", batcher).fire();
      return batcher;
    }

    /**
     * @author Eike Stepper
     */
    private class ContainerIDChanger extends BatchingIDChanger
    {
      public ContainerIDChanger(StatementBatcher batcher, IClassMapping classMapping)
      {
        super(batcher, classMapping.getTable().getField(MappingNames.ATTRIBUTES_FEATURE));
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ContainmentIDChanger extends ContainerIDChanger
    {
      private final ObjectTypeTable objects;

      private final String rawContainerTypeID;

      private final IDBField containerField;

      public ContainmentIDChanger(StatementBatcher batcher, IClassMapping classMapping, ObjectTypeTable objects, String rawContainerTypeID)
      {
        super(batcher, classMapping);
        this.objects = objects;
        this.rawContainerTypeID = rawContainerTypeID;
        containerField = Objects.requireNonNull(table.getField(MappingNames.ATTRIBUTES_CONTAINER));
      }

      @Override
      protected String sql(Object newValue, String condition)
      {
        return super.sql(newValue, condition) //
            + " AND (SELECT " + objects.clazz() //
            + " FROM " + objects //
            + " WHERE " + objects.id() + "=" + containerField + ")=" + rawContainerTypeID;
      }
    }
  }
}
