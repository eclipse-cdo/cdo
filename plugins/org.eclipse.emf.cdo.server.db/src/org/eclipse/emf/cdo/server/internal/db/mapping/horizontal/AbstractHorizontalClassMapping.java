/*
 * Copyright (c) 2009-2013, 2015, 2016, 2018-2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeatureType;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping3;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping4;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.DBIndexAnnotation;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractHorizontalClassMapping implements IClassMapping, IDeactivateable
{
  protected static final int UNSET_LIST = -1;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractHorizontalClassMapping.class);

  protected IDBTable table;

  protected IDBField idField;

  protected IDBField versionField;

  protected IDBField branchField;

  protected IDBField createdField;

  protected IDBField revisedField;

  protected IDBField resourceField;

  protected IDBField containerField;

  protected IDBField featureField;

  private EClass eClass;

  private AbstractHorizontalMappingStrategy mappingStrategy;

  private List<ITypeMapping> valueMappings;

  private List<IListMapping> listMappings;

  private Map<EStructuralFeature, IDBField> listSizeFields;

  private Map<EStructuralFeature, IDBField> unsettableFields;

  private String sqlSelectForHandle;

  private String sqlSelectForChangeSet;

  public AbstractHorizontalClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    this.mappingStrategy = mappingStrategy;
    this.eClass = eClass;

    IDBStoreAccessor accessor = null;
    if (AbstractHorizontalMappingStrategy.isEagerTableCreation(mappingStrategy))
    {
      accessor = (IDBStoreAccessor)StoreThreadLocal.getAccessor();
    }

    initTable(accessor);
  }

  protected final void initTable(IDBStoreAccessor accessor)
  {
    IDBStore store = mappingStrategy.getStore();
    IDBDatabase database = store.getDatabase();
    String tableName = mappingStrategy.getTableName(eClass);

    table = database.getSchema().getTable(tableName);
    if (table == null)
    {
      if (accessor == null)
      {
        // With lazy table creation, an accessor is not available here.
        // The table will be created later when needed.
        return;
      }

      IDBSchemaTransaction schemaTransaction = accessor.openSchemaTransaction();

      try
      {
        createTable(store, schemaTransaction, tableName);
        schemaTransaction.commit();
      }
      finally
      {
        schemaTransaction.close();
      }

      // Re-fetch the table from the real schema by recursive call.
      // An accessor is not needed anymore because the table now exists.
      initTable(null);

      accessor.tableCreated(table);
    }
    else
    {
      initExistingTable();
    }
  }

  private void initExistingTable()
  {
    idField = table.getField(MappingNames.ATTRIBUTES_ID);
    versionField = table.getField(MappingNames.ATTRIBUTES_VERSION);
    branchField = table.getField(MappingNames.ATTRIBUTES_BRANCH);
    createdField = table.getField(MappingNames.ATTRIBUTES_CREATED);
    revisedField = table.getField(MappingNames.ATTRIBUTES_REVISED);
    resourceField = table.getField(MappingNames.ATTRIBUTES_RESOURCE);
    containerField = table.getField(MappingNames.ATTRIBUTES_CONTAINER);
    featureField = table.getField(MappingNames.ATTRIBUTES_FEATURE);

    valueMappings = null;
    listMappings = null;
    listSizeFields = null;
    unsettableFields = null;

    EStructuralFeature[] allPersistentFeatures = CDOModelUtil.getClassInfo(eClass).getAllPersistentFeatures();
    List<EStructuralFeature> unsettableFeatures = new ArrayList<>();

    for (EStructuralFeature feature : allPersistentFeatures)
    {
      String fieldName = mappingStrategy.getFieldName(feature);
      if (feature.isMany())
      {
        IListMapping mapping = mappingStrategy.createListMapping(eClass, feature);
        if (mapping != null)
        {
          if (mapping instanceof IListMapping3)
          {
            ((IListMapping3)mapping).setClassMapping(this);
          }

          if (listMappings == null)
          {
            listMappings = new ArrayList<>();
          }

          listMappings.add(mapping);

          IDBField listSizeField = table.getField(fieldName);

          if (listSizeFields == null)
          {
            listSizeFields = new LinkedHashMap<>();
          }

          listSizeFields.put(feature, listSizeField);
        }
      }
      else
      {
        ITypeMapping mapping = mappingStrategy.createValueMapping(feature);
        mapping.setDBField(table, fieldName);

        if (valueMappings == null)
        {
          valueMappings = new ArrayList<>();
        }

        valueMappings.add(mapping);

        if (feature.isUnsettable())
        {
          unsettableFeatures.add(feature);
        }
      }
    }

    // Register unsettable fields.
    if (!unsettableFeatures.isEmpty())
    {
      unsettableFields = new LinkedHashMap<>();
      for (EStructuralFeature feature : unsettableFeatures)
      {
        String fieldName = mappingStrategy.getUnsettableFieldName(feature);
        IDBField field = table.getField(fieldName);
        unsettableFields.put(feature, field);
      }
    }

    if (valueMappings == null)
    {
      valueMappings = Collections.emptyList();
    }

    if (listMappings == null)
    {
      listMappings = Collections.emptyList();
    }

    initSQLStrings();
  }

  private void createTable(IDBStore store, IDBSchemaTransaction schemaTransaction, String tableName)
  {
    DBType idType = store.getIDHandler().getDBType();
    int idLength = store.getIDColumnLength();

    IDBSchema workingCopy = schemaTransaction.getWorkingCopy();
    IDBTable table = workingCopy.addTable(tableName);

    // Attention: The following fields are only temporarily assigned here.
    // They are only meaningful in this working copy context.
    // At the end of this block, initTable(null) is called recursively to assign the fields in the real table.
    idField = table.addField(MappingNames.ATTRIBUTES_ID, idType, idLength, true);
    versionField = table.addField(MappingNames.ATTRIBUTES_VERSION, DBType.INTEGER, true);
    branchField = addBranchField(table);
    createdField = table.addField(MappingNames.ATTRIBUTES_CREATED, DBType.BIGINT, true);
    revisedField = table.addField(MappingNames.ATTRIBUTES_REVISED, DBType.BIGINT, true);
    resourceField = table.addField(MappingNames.ATTRIBUTES_RESOURCE, idType, idLength, true);
    containerField = addContainerField(table, idType, idLength);
    featureField = table.addField(MappingNames.ATTRIBUTES_FEATURE, DBType.INTEGER, true);

    IDBIndex primaryKey = table.addIndex(IDBIndex.Type.PRIMARY_KEY, idField, versionField);
    if (branchField != null)
    {
      primaryKey.addIndexField(branchField);
    }

    table.addIndex(IDBIndex.Type.NON_UNIQUE, revisedField);

    EStructuralFeature[] features = CDOModelUtil.getClassInfo(eClass).getAllPersistentFeatures();
    createFeatureMappings(mappingStrategy, eClass, table, features);
  }

  protected void initSQLStrings()
  {
    // ----------- Select all revisions (for handleRevisions) ---
    StringBuilder builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(versionField);
    appendSelectForHandleFields(builder);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    sqlSelectForHandle = builder.toString();

    // ----------- Select all revisions (for readChangeSet) ---
    builder = new StringBuilder("SELECT DISTINCT "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table);
    builder.append(" WHERE "); //$NON-NLS-1$
    sqlSelectForChangeSet = builder.toString();
  }

  protected void appendSelectForHandleFields(StringBuilder builder)
  {
    // Do nothing.
  }

  protected String getSQLSelectForHandle()
  {
    return sqlSelectForHandle;
  }

  protected String getSQLSelectForChangeSet()
  {
    return sqlSelectForChangeSet;
  }

  protected IDBField addContainerField(IDBTable table, DBType idType, int idLength)
  {
    return table.addField(MappingNames.ATTRIBUTES_CONTAINER, idType, idLength, true);
  }

  protected IDBField addBranchField(IDBTable table)
  {
    return null;
  }

  /**
   * Read the revision's values from the DB.
   *
   * @return <code>true</code> if the revision has been read successfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  protected final boolean readValuesFromStatement(PreparedStatement stmt, InternalCDORevision revision, IDBStoreAccessor accessor)
  {
    ResultSet resultSet = null;

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Executing Query: {0}", stmt.toString()); //$NON-NLS-1$
      }

      stmt.setMaxRows(1); // Optimization: only 1 row
      resultSet = stmt.executeQuery();

      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      if (!readValuesFromResultSet(resultSet, idHandler, revision, false))
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Resultset was empty"); //$NON-NLS-1$
        }

        return false;
      }

      return true;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  /**
   * Read the revision's values from the DB.
   *
   * @return <code>true</code> if the revision has been read successfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  protected final boolean readValuesFromResultSet(ResultSet resultSet, IIDHandler idHandler, InternalCDORevision revision, boolean forUnit)
  {
    try
    {
      if (resultSet.next())
      {
        long timeStamp = resultSet.getLong(MappingNames.ATTRIBUTES_CREATED);
        CDOBranchPoint branchPoint = revision.getBranch().getPoint(timeStamp);

        if (forUnit)
        {
          revision.setID(idHandler.getCDOID(resultSet, MappingNames.ATTRIBUTES_ID));
        }

        revision.setBranchPoint(branchPoint);
        revision.setVersion(resultSet.getInt(MappingNames.ATTRIBUTES_VERSION));
        revision.setRevised(resultSet.getLong(MappingNames.ATTRIBUTES_REVISED));
        revision.setResourceID(idHandler.getCDOID(resultSet, MappingNames.ATTRIBUTES_RESOURCE));
        revision.setContainerID(idHandler.getCDOID(resultSet, MappingNames.ATTRIBUTES_CONTAINER));
        revision.setContainerFeatureID(resultSet.getInt(MappingNames.ATTRIBUTES_FEATURE));

        for (ITypeMapping mapping : valueMappings)
        {
          EStructuralFeature feature = mapping.getFeature();
          if (feature.isUnsettable())
          {
            IDBField field = unsettableFields.get(feature);
            if (!resultSet.getBoolean(field.getName()))
            {
              // isSet==false -- setValue: null
              revision.setValue(feature, null);
              continue;
            }
          }

          mapping.readValueToRevision(resultSet, revision);
        }

        if (listSizeFields != null)
        {
          for (Map.Entry<EStructuralFeature, IDBField> listSizeEntry : listSizeFields.entrySet())
          {
            EStructuralFeature feature = listSizeEntry.getKey();
            IDBField field = listSizeEntry.getValue();

            int size = resultSet.getInt(field.getName());
            if (size == UNSET_LIST)
            {
              // Leave the list slot in the revision null.
              continue;
            }

            // Ensure the list size.
            CDOList list = revision.getOrCreateList(feature, size);
            for (int i = 0; i < size; i++)
            {
              list.add(InternalCDOList.UNINITIALIZED);
            }
          }
        }

        return true;
      }

      return false;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  protected final void readLists(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    for (IListMapping listMapping : listMappings)
    {
      listMapping.readValues(accessor, revision, listChunk);
    }
  }

  protected final AbstractHorizontalMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  @Override
  public final EClass getEClass()
  {
    return eClass;
  }

  protected final CDOID getMetaID()
  {
    IMetaDataManager metaDataManager = mappingStrategy.getStore().getMetaDataManager();
    return metaDataManager.getMetaID(eClass, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  protected final String getMetaIDStr()
  {
    StringBuilder builder = new StringBuilder();
    getMappingStrategy().getStore().getIDHandler().appendCDOID(builder, getMetaID());
    return builder.toString();
  }

  protected final Map<EStructuralFeature, IDBField> getUnsettableFields()
  {
    return unsettableFields;
  }

  protected final Map<EStructuralFeature, IDBField> getListSizeFields()
  {
    return listSizeFields;
  }

  @Override
  public final List<ITypeMapping> getValueMappings()
  {
    return valueMappings;
  }

  @Override
  public final List<IListMapping> getListMappings()
  {
    return listMappings;
  }

  @Override
  public final IListMapping getListMapping(EStructuralFeature feature)
  {
    if (!isMapped())
    {
      return null;
    }

    for (IListMapping mapping : listMappings)
    {
      if (mapping.getFeature() == feature)
      {
        return mapping;
      }
    }

    throw new IllegalArgumentException("List mapping for feature " + feature + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public final IDBTable getTable()
  {
    return table;
  }

  @Override
  public List<IDBTable> getDBTables()
  {
    List<IDBTable> tables = new ArrayList<>();
    tables.add(table);

    if (listMappings != null)
    {
      for (IListMapping listMapping : listMappings)
      {
        tables.addAll(listMapping.getDBTables());
      }
    }

    return tables;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1} -> {2}]", getClass().getSimpleName(), eClass, table);
  }

  protected void checkDuplicateResources(IDBStoreAccessor accessor, CDORevision revision) throws IllegalStateException
  {
    CDOID folderID = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    CDOID existingID = accessor.readResourceID(folderID, name, revision.getBranch().getHead());
    if (existingID != null && !existingID.equals(revision.getID()))
    {
      throw new IllegalStateException("Duplicate resource node in folder " + folderID + ": " + name); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  protected void writeLists(IDBStoreAccessor accessor, InternalCDORevision revision, boolean firstRevision, boolean raw)
  {
    for (IListMapping listMapping : listMappings)
    {
      if (listMapping instanceof IListMapping4)
      {
        ((IListMapping4)listMapping).writeValues(accessor, revision, firstRevision, raw);
      }
      else
      {
        listMapping.writeValues(accessor, revision);
      }
    }
  }

  @Override
  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, boolean firstRevision, boolean revise, OMMonitor monitor)
  {
    if (table == null)
    {
      initTable(accessor);
    }

    CDOID id = revision.getID();
    InternalCDOBranch branch = revision.getBranch();
    long timeStamp = revision.getTimeStamp();

    // A DetachedCDORevision can only come from DBStoreAccessor.rawStore().
    if (revision instanceof DetachedCDORevision)
    {
      int version = revision.getVersion();
      detachAttributes(accessor, id, version, branch, timeStamp, monitor);

      long revised = revision.getRevised();
      if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        reviseOldRevision(accessor, id, branch, revised);
      }

      return;
    }

    // If the repository's root resource ID is not yet set, then this must be the initial initRootResource()
    // commit. The duplicate check is certainly not needed in this case, and it appears that Mysql has problems
    // with it (Table definition has changed, please retry transaction), see bug 482886.
    boolean duplicateResourcesCheckNeeded = revision.isResourceNode() && mappingStrategy.getStore().getRepository().getRootResourceID() != null;

    monitor.begin(duplicateResourcesCheckNeeded ? 10 : 9);
    Async async = null;

    try
    {
      try
      {
        async = monitor.forkAsync();
        if (firstRevision)
        {
          mappingStrategy.putObjectType(accessor, timeStamp, id, eClass);
        }
        else if (revise)
        {
          long revised = timeStamp - 1;
          reviseOldRevision(accessor, id, branch, revised);
          for (IListMapping mapping : getListMappings())
          {
            mapping.objectDetached(accessor, id, revised);
          }
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      if (duplicateResourcesCheckNeeded)
      {
        try
        {
          async = monitor.forkAsync();
          checkDuplicateResources(accessor, revision);
        }
        finally
        {
          if (async != null)
          {
            async.stop();
          }
        }
      }

      try
      {
        // Write attribute table always (even without modeled attributes!)
        async = monitor.forkAsync();
        writeValues(accessor, revision);
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      try
      {
        // Write list tables only if they exist
        if (listMappings != null)
        {
          async = monitor.forkAsync(7);
          writeLists(accessor, revision, firstRevision, !revise);
        }
        else
        {
          monitor.worked(7);
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void handleRevisions(IDBStoreAccessor accessor, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    if (table == null)
    {
      return;
    }

    // branch parameter is ignored, because either it is null or main branch.
    // this does not make any difference for non-branching store.
    // see #handleRevisions() implementation in HorizontalBranchingClassMapping
    // for branch handling.

    IRepository repository = accessor.getStore().getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDOBranchManager branchManager = repository.getBranchManager();
    CDOBranch mainBranch = branchManager.getMainBranch();

    // TODO: test for timeStamp == INVALID_TIME and encode revision.isValid() as WHERE instead of fetching all revisions
    // in order to increase performance

    StringBuilder builder = new StringBuilder(sqlSelectForHandle);

    int timeParameters = 0;
    if (timeStamp != CDOBranchPoint.INVALID_DATE)
    {
      if (exactTime)
      {
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          builder.append(" WHERE "); //$NON-NLS-1$
          builder.append(createdField);
          builder.append("=?"); //$NON-NLS-1$
          timeParameters = 1;
        }
      }
      else
      {
        builder.append(" WHERE "); //$NON-NLS-1$
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          builder.append(createdField);
          builder.append("<=?"); //$NON-NLS-1$
          builder.append(" AND ("); //$NON-NLS-1$
          builder.append(revisedField);
          builder.append(">=? OR "); //$NON-NLS-1$
          builder.append(revisedField);
          builder.append("=0)"); //$NON-NLS-1$
          timeParameters = 2;
        }
        else
        {
          builder.append(revisedField);
          builder.append("=0"); //$NON-NLS-1$
        }
      }
    }

    builder.append(" ORDER BY "); //$NON-NLS-1$
    builder.append(idField);
    builder.append(", "); //$NON-NLS-1$
    builder.append(versionField);

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(builder.toString(), ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      for (int i = 0; i < timeParameters; i++)
      {
        stmt.setLong(i + 1, timeStamp);
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        int version = resultSet.getInt(2);

        if (version >= CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion branchVersion = mainBranch.getVersion(version);
          InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevisionByVersion(id, branchVersion, CDORevision.UNCHUNKED, true);

          if (!handler.handleRevision(revision))
          {
            break;
          }
        }
        else
        {
          EClass eClass = getEClass();
          long created = resultSet.getLong(3);
          long revised = resultSet.getLong(4);

          // Tell handler about detached IDs
          InternalCDORevision revision = new DetachedCDORevision(eClass, id, mainBranch, -version, created, revised);
          if (!handler.handleRevision(revision))
          {
            break;
          }
        }
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public Set<CDOID> readChangeSet(IDBStoreAccessor accessor, CDOChangeSetSegment[] segments)
  {
    Set<CDOID> result = new HashSet<>();
    if (table == null)
    {
      return result;
    }

    StringBuilder builder = new StringBuilder(sqlSelectForChangeSet);
    boolean isFirst = true;

    for (int i = 0; i < segments.length; i++)
    {
      if (isFirst)
      {
        isFirst = false;
      }
      else
      {
        builder.append(" OR "); //$NON-NLS-1$
      }

      builder.append(createdField);
      builder.append(">=?"); //$NON-NLS-1$
      builder.append(" AND ("); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append("<=? OR "); //$NON-NLS-1$
      builder.append(revisedField);
      builder.append("=0)"); //$NON-NLS-1$
    }

    IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(builder.toString(), ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      int column = 1;
      for (CDOChangeSetSegment segment : segments)
      {
        stmt.setLong(column++, segment.getTimeStamp());
        stmt.setLong(column++, segment.getEndTime());
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        result.add(id);
      }

      return result;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  public void detachObject(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    if (table == null)
    {
      return;
    }

    Async async = null;
    monitor.begin(1 + listMappings.size());

    try
    {
      if (version >= CDOBranchVersion.FIRST_VERSION)
      {
        reviseOldRevision(accessor, id, branch, timeStamp - 1);
      }

      detachAttributes(accessor, id, version, branch, timeStamp, monitor.fork());

      // notify list mappings so they can clean up
      for (IListMapping mapping : getListMappings())
      {
        try
        {
          async = monitor.forkAsync();
          mapping.objectDetached(accessor, id, timeStamp);
        }
        finally
        {
          if (async != null)
          {
            async.stop();
          }
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public void rawDelete(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, OMMonitor monitor)
  {
    if (table == null)
    {
      return;
    }

    Async async = null;
    monitor.begin(1 + listMappings.size());

    try
    {
      rawDeleteAttributes(accessor, id, branch, version, monitor.fork());

      // notify list mappings so they can clean up
      for (IListMapping mapping : getListMappings())
      {
        if (mapping instanceof AbstractBasicListTableMapping)
        {
          try
          {
            async = monitor.forkAsync();

            AbstractBasicListTableMapping m = (AbstractBasicListTableMapping)mapping;
            m.rawDeleted(accessor, id, branch, version);
          }
          finally
          {
            if (async != null)
            {
              async.stop();
            }
          }
        }
        else
        {
          throw new UnsupportedOperationException("rawDeleted() is not supported by " + mapping.getClass().getName());
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected abstract void rawDeleteAttributes(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, int version, OMMonitor fork);

  @Override
  public final boolean queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context, String idString)
  {
    if (table == null)
    {
      return true;
    }

    List<EReference> refs = context.getSourceCandidates().get(eClass);
    List<EReference> scalarRefs = new ArrayList<>();

    for (EReference ref : refs)
    {
      if (ref.isMany())
      {
        IListMapping listMapping = getListMapping(ref);
        String where = getListXRefsWhere(context);

        boolean more = listMapping.queryXRefs(accessor, table.toString(), where, context, idString);
        if (!more)
        {
          return false;
        }
      }
      else
      {
        scalarRefs.add(ref);
      }
    }

    if (!scalarRefs.isEmpty())
    {
      boolean more = queryScalarXRefs(accessor, scalarRefs, context, idString);
      if (!more)
      {
        return false;
      }
    }

    return true;
  }

  protected final boolean queryScalarXRefs(IDBStoreAccessor accessor, List<EReference> scalarRefs, QueryXRefsContext context, String idString)
  {
    String where = getListXRefsWhere(context);

    for (EReference ref : scalarRefs)
    {
      ITypeMapping valueMapping = getValueMapping(ref);
      IDBField valueField = valueMapping.getField();

      StringBuilder builder = new StringBuilder();
      builder.append("SELECT ");
      builder.append(idField);
      builder.append(", ");
      builder.append(valueField);
      builder.append(" FROM ");
      builder.append(table);
      builder.append(" WHERE ");
      builder.append(versionField);
      builder.append(">0 AND ");
      builder.append(where);
      builder.append(" AND ");
      builder.append(valueField);
      builder.append(" IN ");
      builder.append(idString);
      String sql = builder.toString();
      if (TRACER.isEnabled())
      {
        TRACER.format("Query XRefs (attributes): {0}", sql);
      }

      IIDHandler idHandler = getMappingStrategy().getStore().getIDHandler();
      IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.MEDIUM);
      ResultSet resultSet = null;

      try
      {
        resultSet = stmt.executeQuery();
        while (resultSet.next())
        {
          CDOID sourceID = idHandler.getCDOID(resultSet, 1);
          CDOID targetID = idHandler.getCDOID(resultSet, 2);

          boolean more = context.addXRef(targetID, sourceID, ref, 0);
          if (TRACER.isEnabled())
          {
            TRACER.format("  add XRef to context: src={0}, tgt={1}, idx=0", sourceID, targetID);
          }

          if (!more)
          {
            if (TRACER.isEnabled())
            {
              TRACER.format("  result limit reached. Ignoring further results.");
            }

            return false;
          }
        }
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

    return true;
  }

  protected abstract String getListXRefsWhere(QueryXRefsContext context);

  protected abstract void detachAttributes(IDBStoreAccessor accessor, CDOID id, int version, CDOBranch branch, long timeStamp, OMMonitor fork);

  protected abstract void reviseOldRevision(IDBStoreAccessor accessor, CDOID id, CDOBranch branch, long timeStamp);

  protected abstract void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision);

  @Override
  public Exception deactivate()
  {
    return null;
  }

  protected static void appendTypeMappingNames(StringBuilder builder, Collection<ITypeMapping> typeMappings)
  {
    if (typeMappings != null)
    {
      for (ITypeMapping typeMapping : typeMappings)
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(typeMapping.getField());
      }
    }
  }

  protected static void appendFieldNames(StringBuilder builder, Map<EStructuralFeature, IDBField> fields)
  {
    if (fields != null)
    {
      for (IDBField field : fields.values())
      {
        builder.append(", "); //$NON-NLS-1$
        builder.append(field);
      }
    }
  }

  protected static void appendTypeMappingParameters(StringBuilder builder, Collection<ITypeMapping> typeMappings)
  {
    if (typeMappings != null)
    {
      for (int i = 0; i < typeMappings.size(); i++)
      {
        builder.append(", ?"); //$NON-NLS-1$
      }
    }
  }

  protected static void appendFieldParameters(StringBuilder builder, Map<EStructuralFeature, IDBField> fields)
  {
    if (fields != null)
    {
      for (int i = 0; i < fields.size(); i++)
      {
        builder.append(", ?"); //$NON-NLS-1$
      }
    }
  }

  public static void createFeatureMappings(AbstractMappingStrategy mappingStrategy, EClass eClass, IDBTable table, EStructuralFeature[] features)
  {
    if (ObjectUtil.isEmpty(features))
    {
      return;
    }

    Map<EStructuralFeature, ITypeMapping> valueMappings = new HashMap<>();
    List<EStructuralFeature> unsettableFeatures = new ArrayList<>();

    for (EStructuralFeature feature : features)
    {
      String fieldName = mappingStrategy.getFieldName(feature);
      if (feature.isMany())
      {
        IListMapping mapping = mappingStrategy.createListMapping(eClass, feature);
        if (mapping != null)
        {
          // Add field for list sizes.
          table.addField(fieldName, DBType.INTEGER);
        }
      }
      else
      {
        ITypeMapping valueMapping = mappingStrategy.createValueMapping(feature);
        valueMapping.createDBField(table, fieldName);
        valueMappings.put(feature, valueMapping);

        Set<CDOFeatureType> forceIndexes = AbstractMappingStrategy.getForceIndexes(mappingStrategy);
        if (CDOFeatureType.matchesCombination(feature, forceIndexes))
        {
          IDBField field = table.getField(fieldName);
          if (!table.hasIndexFor(field))
          {
            IDBIndex index = table.addIndex(IDBIndex.Type.NON_UNIQUE, field);
            DBUtil.setOptional(index, true); // Creation might fail for unsupported column type!
          }
        }

        if (feature.isUnsettable())
        {
          unsettableFeatures.add(feature);
        }
      }
    }

    // Add unsettable fields to end of table.
    for (EStructuralFeature feature : unsettableFeatures)
    {
      String fieldName = mappingStrategy.getUnsettableFieldName(feature);
      table.addField(fieldName, DBType.BOOLEAN);
    }

    // Create optional feature indices.
    for (List<EStructuralFeature> indexFeatures : DBIndexAnnotation.getIndices(eClass, features))
    {
      int size = indexFeatures.size();
      IDBField[] fields = new IDBField[size];

      for (int i = 0; i < size; i++)
      {
        EStructuralFeature indexFeature = indexFeatures.get(i);

        ITypeMapping valueMapping = valueMappings.get(indexFeature);
        IDBField field = valueMapping.getField();
        fields[i] = field;
      }

      if (!table.hasIndexFor(fields))
      {
        IDBIndex index = table.addIndex(IDBIndex.Type.NON_UNIQUE, fields);
        DBUtil.setOptional(index, true); // Creation might fail for unsupported column type!
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class AbstractFeatureDeltaWriter implements CDOFeatureDeltaVisitor
  {
    protected IDBStoreAccessor accessor;

    protected long created;

    protected CDOID id;

    public final void process(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created)
    {
      this.accessor = accessor;
      this.created = created;
      id = delta.getID();

      if (table == null)
      {
        initTable(accessor);
      }

      doProcess(delta);
    }

    protected abstract void doProcess(InternalCDORevisionDelta delta);

    @Override
    @Deprecated
    public final void visit(CDOAddFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    @Override
    @Deprecated
    public final void visit(CDORemoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    @Override
    @Deprecated
    public final void visit(CDOMoveFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }

    @Override
    @Deprecated
    public final void visit(CDOClearFeatureDelta delta)
    {
      throw new ImplementationError("Should not be called"); //$NON-NLS-1$
    }
  }
}
