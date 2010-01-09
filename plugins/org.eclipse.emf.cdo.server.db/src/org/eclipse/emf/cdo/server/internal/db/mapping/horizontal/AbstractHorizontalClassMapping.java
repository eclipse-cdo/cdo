/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IExternalReferenceManager;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractHorizontalClassMapping implements IClassMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractHorizontalClassMapping.class);

  private EClass eClass;

  private IDBTable table;

  private AbstractHorizontalMappingStrategy mappingStrategy;

  private List<ITypeMapping> valueMappings;

  private List<IListMapping> listMappings;

  public AbstractHorizontalClassMapping(AbstractHorizontalMappingStrategy mappingStrategy, EClass eClass)
  {
    this.mappingStrategy = mappingStrategy;
    this.eClass = eClass;

    initTable();
    initFeatures();
  }

  private void initTable()
  {
    String name = getMappingStrategy().getTableName(eClass);
    table = getMappingStrategy().getStore().getDBSchema().addTable(name);

    IDBField idField = table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT, true);
    IDBField versionField = table.addField(CDODBSchema.ATTRIBUTES_VERSION, DBType.INTEGER, true);
    table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.BIGINT, true);
    table.addField(CDODBSchema.ATTRIBUTES_CREATED, DBType.BIGINT, true);
    IDBField revisedField = table.addField(CDODBSchema.ATTRIBUTES_REVISED, DBType.BIGINT, true);
    table.addField(CDODBSchema.ATTRIBUTES_RESOURCE, DBType.BIGINT, true);
    table.addField(CDODBSchema.ATTRIBUTES_CONTAINER, DBType.BIGINT, true);
    table.addField(CDODBSchema.ATTRIBUTES_FEATURE, DBType.INTEGER, true);

    table.addIndex(IDBIndex.Type.UNIQUE, idField, versionField);
    table.addIndex(IDBIndex.Type.NON_UNIQUE, idField, revisedField);
  }

  private void initFeatures()
  {
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(eClass);

    if (features == null)
    {
      valueMappings = Collections.emptyList();
      listMappings = Collections.emptyList();
    }
    else
    {
      valueMappings = createValueMappings(features);
      listMappings = createListMappings(features);
    }
  }

  private List<ITypeMapping> createValueMappings(EStructuralFeature[] features)
  {
    List<ITypeMapping> mappings = new ArrayList<ITypeMapping>();
    for (EStructuralFeature feature : features)
    {
      if (!feature.isMany())
      {
        ITypeMapping mapping = mappingStrategy.createValueMapping(feature);
        mapping.createDBField(getTable());
        mappings.add(mapping);
      }
    }

    return mappings;
  }

  private List<IListMapping> createListMappings(EStructuralFeature[] features)
  {
    List<IListMapping> listMappings = new ArrayList<IListMapping>();
    for (EStructuralFeature feature : features)
    {
      if (feature.isMany())
      {
        if (FeatureMapUtil.isFeatureMap(feature))
        {
          listMappings.add(mappingStrategy.createFeatureMapMapping(eClass, feature));
        }
        else
        {
          listMappings.add(mappingStrategy.createListMapping(eClass, feature));
        }
      }
    }

    return listMappings;
  }

  /**
   * Read the revision's values from the DB.
   * 
   * @return <code>true</code> if the revision has been read successfully.<br>
   *         <code>false</code> if the revision does not exist in the DB.
   */
  protected final boolean readValuesFromStatement(PreparedStatement pstmt, InternalCDORevision revision,
      IDBStoreAccessor accessor)
  {
    ResultSet resultSet = null;

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Executing Query: {0}", pstmt.toString()); //$NON-NLS-1$
      }

      pstmt.setMaxRows(1); // Optimization: only 1 row

      resultSet = pstmt.executeQuery();
      if (!resultSet.next())
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Resultset was empty"); //$NON-NLS-1$
        }

        return false;
      }

      revision.setVersion(resultSet.getInt(CDODBSchema.ATTRIBUTES_VERSION));
      revision.setCreated(resultSet.getLong(CDODBSchema.ATTRIBUTES_CREATED));
      revision.setRevised(resultSet.getLong(CDODBSchema.ATTRIBUTES_REVISED));
      revision.setResourceID(CDODBUtil.convertLongToCDOID(getExternalReferenceManager(), accessor, resultSet
          .getLong(CDODBSchema.ATTRIBUTES_RESOURCE)));
      revision.setContainerID(CDODBUtil.convertLongToCDOID(getExternalReferenceManager(), accessor, resultSet
          .getLong(CDODBSchema.ATTRIBUTES_CONTAINER)));
      revision.setContainingFeatureID(resultSet.getInt(CDODBSchema.ATTRIBUTES_FEATURE));

      for (ITypeMapping mapping : valueMappings)
      {
        mapping.readValueToRevision(resultSet, revision);
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

  protected final void readLists(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
  {
    for (IListMapping listMapping : listMappings)
    {
      listMapping.readValues(accessor, revision, listChunk);
    }
  }

  public final void detachObject(IDBStoreAccessor accessor, CDOID id, long revised, OMMonitor monitor)
  {
    Async async = null;
    try
    {
      monitor.begin(getListMappings().size() + 1);
      async = monitor.forkAsync();
      reviseObject(accessor, id, revised);
      async.stop();
      async = monitor.forkAsync(getListMappings().size());
      for (IListMapping mapping : getListMappings())
      {
        mapping.objectRevised(accessor, id, revised);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  protected final IMetaDataManager getMetaDataManager()
  {
    return getMappingStrategy().getStore().getMetaDataManager();
  }

  protected final IExternalReferenceManager getExternalReferenceManager()
  {
    return mappingStrategy.getStore().getExternalReferenceManager();
  }

  protected final IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  protected final EClass getEClass()
  {
    return eClass;
  }

  public final List<ITypeMapping> getValueMappings()
  {
    return valueMappings;
  }

  public final ITypeMapping getValueMapping(EStructuralFeature feature)
  {
    for (ITypeMapping mapping : valueMappings)
    {
      if (mapping.getFeature() == feature)
      {
        return mapping;
      }
    }

    return null;
  }

  public final List<IListMapping> getListMappings()
  {
    return listMappings;
  }

  public final IListMapping getListMapping(EStructuralFeature feature)
  {
    for (IListMapping mapping : listMappings)
    {
      if (mapping.getFeature() == feature)
      {
        return mapping;
      }
    }

    throw new IllegalArgumentException("List mapping for feature " + feature + " does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected final IDBTable getTable()
  {
    return table;
  }

  public Collection<IDBTable> getDBTables()
  {
    ArrayList<IDBTable> tables = new ArrayList<IDBTable>();
    tables.add(table);

    for (IListMapping listMapping : listMappings)
    {
      tables.addAll(listMapping.getDBTables());
    }

    return tables;
  }

  private void checkDuplicateResources(IDBStoreAccessor accessor, CDORevision revision) throws IllegalStateException
  {
    CDOID folderID = (CDOID)revision.data().getContainerID();
    String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    CDOID existingID = accessor.readResourceID(folderID, name, CDORevision.UNSPECIFIED_DATE);
    if (existingID != null && !existingID.equals(revision.getID()))
    {
      throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  protected void writeLists(IDBStoreAccessor accessor, InternalCDORevision revision)
  {
    for (IListMapping listMapping : listMappings)
    {
      listMapping.writeValues(accessor, revision);
    }
  }

  public void writeRevision(IDBStoreAccessor accessor, InternalCDORevision revision, OMMonitor monitor)
  {
    Async async = null;
    try
    {
      monitor.begin(10);
      async = monitor.forkAsync();

      CDOID id = revision.getID();
      if (revision.getVersion() == 1)
      {
        mappingStrategy.putObjectType(accessor, id, eClass);
      }
      else
      {
        long revised = revision.getCreated() - 1;
        reviseObject(accessor, id, revised);
        for (IListMapping mapping : getListMappings())
        {
          mapping.objectRevised(accessor, id, revised);
        }
      }

      async.stop();
      async = monitor.forkAsync();

      if (revision.isResourceFolder() || revision.isResource())
      {
        checkDuplicateResources(accessor, revision);
      }

      async.stop();
      async = monitor.forkAsync();

      // Write attribute table always (even without modeled attributes!)
      writeValues(accessor, revision);

      async.stop();
      async = monitor.forkAsync(7);

      // Write list tables only if they exist
      if (listMappings != null)
      {
        writeLists(accessor, revision);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  protected abstract void writeValues(IDBStoreAccessor accessor, InternalCDORevision revision);

  protected abstract void reviseObject(IDBStoreAccessor accessor, CDOID id, long revised);
}
