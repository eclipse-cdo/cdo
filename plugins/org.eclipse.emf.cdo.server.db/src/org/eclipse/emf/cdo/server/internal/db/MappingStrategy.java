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
import org.eclipse.emf.cdo.common.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IStoreReader.QueryResourcesContext;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class MappingStrategy extends Lifecycle implements IMappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MappingStrategy.class);

  public static final String PROP_TO_MANY_REFERENCE_MAPPING = "toManyReferenceMapping";

  public static final String PROP_TO_ONE_REFERENCE_MAPPING = "toOneReferenceMapping";

  private IDBStore store;

  private Map<String, String> properties;

  private Map<Object, IDBTable> referenceTables = new HashMap<Object, IDBTable>();

  private Map<Integer, CDOClassRef> classRefs = new HashMap<Integer, CDOClassRef>();

  private IClassMapping resourceClassMapping;

  private IAttributeMapping resourcePathMapping;

  private IDBTable resourceTable;

  private IDBField resourceIDField;

  private IDBField resourcePathField;

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
      if (classID == ClassServerInfo.CDO_RESOURCE_CLASS_DBID)
      {
        IPackageManager packageManager = getStore().getRepository().getPackageManager();
        CDOResourceClass resourceClass = packageManager.getCDOResourcePackage().getCDOResourceClass();
        classRef = resourceClass.createClassRef();
      }
      else
      {
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

  public IClassMapping getResourceClassMapping()
  {
    if (resourceClassMapping == null)
    {
      initResourceInfos();
    }

    return resourceClassMapping;
  }

  public IAttributeMapping getResourcePathMapping()
  {
    if (resourcePathMapping == null)
    {
      initResourceInfos();
    }

    return resourcePathMapping;
  }

  public IDBTable getResourceTable()
  {
    if (resourceTable == null)
    {
      initResourceInfos();
    }

    return resourceTable;
  }

  public IDBField getResourceIDField()
  {
    if (resourceIDField == null)
    {
      initResourceInfos();
    }

    return resourceIDField;
  }

  public IDBField getResourcePathField()
  {
    if (resourcePathField == null)
    {
      initResourceInfos();
    }

    return resourcePathField;
  }

  protected void initResourceInfos()
  {
    IPackageManager packageManager = getStore().getRepository().getPackageManager();
    CDOResourceClass resourceClass = packageManager.getCDOResourcePackage().getCDOResourceClass();
    CDOPathFeature pathFeature = packageManager.getCDOResourcePackage().getCDOResourceClass().getCDOPathFeature();

    resourceClassMapping = getClassMapping(resourceClass);
    resourcePathMapping = resourceClassMapping.getAttributeMapping(pathFeature);

    resourceTable = resourceClassMapping.getTable();
    resourceIDField = resourceTable.getField(CDODBSchema.ATTRIBUTES_ID);
    resourcePathField = resourcePathMapping.getField();
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

  public void queryResources(IDBStoreReader storeReader, QueryResourcesContext context)
  {
    IClassMapping mapping = getResourceClassMapping();
    IDBTable resourceTable = mapping.getTable();
    IDBField pathField = getResourcePathField();
    String pathPrefix = context.getPathPrefix();

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" FROM ");
    builder.append(resourceTable);
    builder.append(" WHERE ");
    builder.append(pathField);
    builder.append(" LIKE \'");
    builder.append(pathPrefix);
    builder.append("%\'");
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
          break;
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

  public CDOID readResourceID(IDBStoreReader storeReader, String path)
  {
    IDBTable resourceTable = getResourceTable();
    IDBField selectField = getResourceIDField();
    IDBField whereField = getResourcePathField();
    return (CDOID)readResourceInfo(storeReader, resourceTable, selectField, whereField, path);
  }

  public String readResourcePath(IDBStoreReader storeReader, CDOID id)
  {
    IDBTable resourceTable = getResourceTable();
    IDBField selectField = getResourcePathField();
    IDBField whereField = getResourceIDField();
    Object whereValue = CDOIDUtil.getLong(id);
    return (String)readResourceInfo(storeReader, resourceTable, selectField, whereField, whereValue);
  }

  protected Object readResourceInfo(IDBStoreReader storeReader, IDBTable resourceTable, IDBField selectField,
      IDBField whereField, Object whereValue)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(selectField);
    builder.append(" FROM ");
    builder.append(resourceTable);
    builder.append(" WHERE ");
    builder.append(whereField);
    builder.append("=");
    getStore().getDBAdapter().appendValue(builder, whereField, whereValue);

    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    ResultSet resultSet = null;

    try
    {
      Statement statement = storeReader.getStatement();
      statement.setMaxRows(1); // Reset by DBUtil.close(resultSet)

      resultSet = statement.executeQuery(sql);
      if (!resultSet.next())
      {
        return null;
      }

      if (whereValue instanceof Long)
      {
        String path = resultSet.getString(1);
        return path;
      }

      long id = resultSet.getLong(1);
      return CDOIDUtil.createLong(id);
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

  public long repairAfterCrash(Connection connection)
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
