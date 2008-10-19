/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689    
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.server.IStoreReader.QueryResourcesContext;
import org.eclipse.emf.cdo.server.IStoreReader.QueryResourcesContext.ExactMatch;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy extends Lifecycle implements IMappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MappingStrategy.class);

  private IDBStore store;

  private Map<String, String> properties;

  private Map<Object, IDBTable> referenceTables = new HashMap<Object, IDBTable>();

  private Map<Integer, CDOClassRef> classRefs = new HashMap<Integer, CDOClassRef>();

  public MappingStrategy()
  {
  }

  public IDBStore getStore()
  {
    return store;
  }

  public void setStore(IDBStore store)
  {
    this.store = store;
  }

  public synchronized Map<String, String> getProperties()
  {
    if (properties == null)
    {
      properties = new HashMap<String, String>();
    }

    return properties;
  }

  public synchronized void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public boolean isQualifiedNames()
  {
    String value = getProperties().get(PROP_QUALIFIED_NAMES);
    return value == null ? false : Boolean.valueOf(value);
  }

  public ToMany getToMany()
  {
    String value = getProperties().get(PROP_TO_MANY_REFERENCE_MAPPING);
    return value == null ? ToMany.PER_REFERENCE : ToMany.valueOf(value);
  }

  public ToOne getToOne()
  {
    String value = getProperties().get(PROP_TO_ONE_REFERENCE_MAPPING);
    return value == null ? ToOne.LIKE_ATTRIBUTES : ToOne.valueOf(value);
  }

  public Map<Object, IDBTable> getReferenceTables()
  {
    return referenceTables;
  }

  public CDOClassRef getClassRef(IDBStoreReader storeReader, int classID)
  {
    CDOClassRef classRef = classRefs.get(classID);
    if (classRef == null)
    {
      switch (classID)
      {
      case ClassServerInfo.CDO_RESOURCE_NODE_CLASS_DBID:
      {
        CDOResourcePackage resourcePackage = getStore().getRepository().getPackageManager().getCDOResourcePackage();
        classRef = resourcePackage.getCDOResourceNodeClass().createClassRef();
        break;
      }

      case ClassServerInfo.CDO_RESOURCE_FOLDER_CLASS_DBID:
      {
        CDOResourcePackage resourcePackage = getStore().getRepository().getPackageManager().getCDOResourcePackage();
        classRef = resourcePackage.getCDOResourceFolderClass().createClassRef();
        break;
      }

      case ClassServerInfo.CDO_RESOURCE_CLASS_DBID:
      {
        CDOResourcePackage resourcePackage = getStore().getRepository().getPackageManager().getCDOResourcePackage();
        classRef = resourcePackage.getCDOResourceClass().createClassRef();
        break;
      }

      default:
        classRef = storeReader.readClassRef(classID);
      }

      classRefs.put(classID, classRef);
    }

    return classRef;
  }

  public IClassMapping getClassMapping(CDOClass cdoClass)
  {
    IClassMapping mapping = ClassServerInfo.getClassMapping(cdoClass);
    if (mapping == NoClassMapping.INSTANCE)
    {
      return null;
    }

    if (mapping == null)
    {
      mapping = createClassMapping(cdoClass);
      ClassServerInfo.setClassMapping(cdoClass, mapping == null ? NoClassMapping.INSTANCE : mapping);
    }

    return mapping;
  }

  public String getTableName(CDOPackage cdoPackage)
  {
    if (isQualifiedNames())
    {
      return cdoPackage.getQualifiedName().replace('.', '_');
    }

    return cdoPackage.getName();
  }

  public String getTableName(CDOClass cdoClass)
  {
    if (isQualifiedNames())
    {
      return cdoClass.getQualifiedName().replace('.', '_');
    }

    return cdoClass.getName();
  }

  public String createWhereClause(long timeStamp)
  {
    StringBuilder builder = new StringBuilder();
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0");
    }
    else
    {
      builder.append("(");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append("=0 OR ");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(">=");
      builder.append(timeStamp);
      builder.append(") AND ");
      builder.append(timeStamp);
      builder.append(">=");
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    }

    return builder.toString();
  }

  public CloseableIterator<CDOID> readObjectIDs(final IDBStoreReader storeReader)
  {
    List<CDOClass> classes = getClassesWithObjectInfo();
    final Iterator<CDOClass> classIt = classes.iterator();
    return new ObjectIDIterator(this, storeReader)
    {
      @Override
      protected ResultSet getNextResultSet()
      {
        while (classIt.hasNext())
        {
          CDOClass cdoClass = classIt.next();
          IClassMapping mapping = getClassMapping(cdoClass);
          if (mapping != null)
          {
            IDBTable table = mapping.getTable();
            if (table != null)
            {
              StringBuilder builder = new StringBuilder();
              builder.append("SELECT DISTINCT ");
              builder.append(CDODBSchema.ATTRIBUTES_ID);
              builder.append(" FROM ");
              builder.append(table);
              String sql = builder.toString();

              try
              {
                return storeReader.getStatement().executeQuery(sql);
              }
              catch (SQLException ex)
              {
                throw new DBException(ex);
              }
            }
          }
        }

        return null;
      }
    };
  }

  public CDOID readResourceID(IDBStoreReader storeReader, CDOID folderID, String name, long timeStamp)
  {
    ExactMatch context = StoreUtil.createExactMatchContext(folderID, name, timeStamp);
    queryResources(storeReader, context);
    return context.getResourceID();
  }

  public void queryResources(IDBStoreReader storeReader, QueryResourcesContext context)
  {
    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();
    String where = createWhereClause(context.getTimeStamp());

    String[] queries = getResourceQueries(folderID, name, exactMatch);
    for (String query : queries)
    {
      StringBuilder builder = new StringBuilder();
      builder.append(query);
      builder.append(" AND (");
      builder.append(where);
      builder.append(")");

      String sql = builder.toString();
      if (TRACER.isEnabled())
      {
        TRACER.trace(sql);
      }

      ResultSet resultSet = null;

      try
      {
        resultSet = storeReader.getStatement().executeQuery(sql);
        while (resultSet.next())
        {
          long longID = resultSet.getLong(1);
          CDOID id = CDOIDUtil.createLong(longID);
          if (!context.addResource(id))
          {
            // No more results allowed
            return;
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
      }
    }
  }

  protected abstract String[] getResourceQueries(CDOID folderID, String name, boolean exactMatch);

  public void createResourceTables(IDBAdapter dbAdapter, Connection connection)
  {
    Set<IDBTable> tables = new HashSet<IDBTable>();
    CDOResourcePackage resourcePackage = getStore().getRepository().getPackageManager().getCDOResourcePackage();

    addResourceTables(resourcePackage.getCDOResourceNodeClass(), tables);
    addResourceTables(resourcePackage.getCDOResourceFolderClass(), tables);
    addResourceTables(resourcePackage.getCDOResourceClass(), tables);

    if (dbAdapter.createTables(tables, connection).size() != tables.size())
    {
      throw new DBException("Resource tables not completely created");
    }
  }

  private void addResourceTables(CDOClass cdoClass, Set<IDBTable> tables)
  {
    IClassMapping mapping = getClassMapping(cdoClass);
    if (mapping != null)
    {
      Set<IDBTable> affectedTables = mapping.getAffectedTables();
      tables.addAll(affectedTables);
    }
  }

  public long repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    long maxCDOID = 0L;
    for (CDOClass idClass : getClassesWithObjectInfo())
    {
      IClassMapping classMapping = getClassMapping(idClass);
      IDBTable table = classMapping.getTable();
      IDBField idField = table.getField(CDODBSchema.ATTRIBUTES_ID);
      long classMaxCDOID = DBUtil.selectMaximumLong(connection, idField);
      if (TRACER.isEnabled())
      {
        TRACER.format("Max CDOID of table {0}: {1}", table, classMaxCDOID);
      }

      maxCDOID = Math.max(maxCDOID, classMaxCDOID);
    }

    return maxCDOID + 2L;
  }

  @Override
  public String toString()
  {
    return getType();
  }

  /**
   * The implementation of this method must take care of creating a unique ids to prevent duplicate resource paths.
   */
  protected abstract IClassMapping createClassMapping(CDOClass cdoClass);

  protected abstract List<CDOClass> getClassesWithObjectInfo();

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(store, "store");
  }
}
