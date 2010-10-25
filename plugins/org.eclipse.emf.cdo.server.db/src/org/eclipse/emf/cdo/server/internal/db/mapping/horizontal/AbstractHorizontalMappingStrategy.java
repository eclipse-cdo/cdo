/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IObjectTypeMapper;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

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
public abstract class AbstractHorizontalMappingStrategy extends AbstractMappingStrategy
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractHorizontalMappingStrategy.class);

  /**
   * The associated object type mapper.
   */
  private IObjectTypeMapper objectTypeMapper;

  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return objectTypeMapper.getObjectType(accessor, id);
  }

  public void putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    objectTypeMapper.putObjectType(accessor, timeStamp, id, type);
  }

  public long[] repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    long minLocalID = getMinLocalID(connection);
    long maxID = objectTypeMapper.getMaxID(connection);

    long[] result = { minLocalID, maxID };
    return result;
  }

  public void queryResources(IDBStoreAccessor accessor, QueryResourcesContext context)
  {
    // only support timestamp in audit mode
    if (context.getTimeStamp() != CDORevision.UNSPECIFIED_DATE && !hasAuditSupport())
    {
      throw new UnsupportedOperationException("Mapping Strategy does not support audits"); //$NON-NLS-1$
    }

    EresourcePackage resourcesPackage = EresourcePackage.eINSTANCE;

    // first query folders
    boolean shallContinue = queryResources(accessor, getClassMapping(resourcesPackage.getCDOResourceFolder()), context);

    // not enough results? -> query resources
    if (shallContinue)
    {
      queryResources(accessor, getClassMapping(resourcesPackage.getCDOResource()), context);
    }
  }

  public void queryXRefs(IDBStoreAccessor accessor, QueryXRefsContext context)
  {
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

      long id = CDOIDUtil.getLong(targetID);
      builder.append(id);
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

  public void rawExport(IDBStoreAccessor accessor, CDODataOutput out, int fromBranchID, int toBranchID,
      long fromCommitTime, long toCommitTime) throws IOException
  {
    StringBuilder builder = new StringBuilder();
    builder.append(" WHERE a_t."); //$NON-NLS-1$
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(" BETWEEN "); //$NON-NLS-1$
    builder.append(fromCommitTime);
    builder.append(" AND "); //$NON-NLS-1$
    builder.append(toCommitTime);

    String attrSuffix = builder.toString();
    Connection connection = accessor.getConnection();

    Collection<IClassMapping> classMappings = getClassMappings(true).values();
    out.writeInt(classMappings.size());

    for (IClassMapping classMapping : classMappings)
    {
      EClass eClass = classMapping.getEClass();
      out.writeCDOClassifierRef(eClass);

      IDBTable table = classMapping.getDBTables().get(0);
      DBUtil.serializeTable(out, connection, table, "a_t", attrSuffix);

      for (IListMapping listMapping : classMapping.getListMappings())
      {
        rawExportList(out, connection, listMapping, table, attrSuffix);
      }
    }

    objectTypeMapper.rawExport(connection, out, fromCommitTime, toCommitTime);
  }

  protected void rawExportList(CDODataOutput out, Connection connection, IListMapping listMapping, IDBTable attrTable,
      String attrSuffix) throws IOException
  {
    for (IDBTable table : listMapping.getDBTables())
    {
      String listSuffix = ", " + attrTable + " a_t" + attrSuffix;
      String listJoin = getListJoin("a_t", "l_t");
      if (listJoin != null)
      {
        listSuffix += listJoin;
      }

      DBUtil.serializeTable(out, connection, table, "l_t", listSuffix);
    }
  }

  public void rawImport(IDBStoreAccessor accessor, CDODataInput in, OMMonitor monitor) throws IOException
  {
    int size = in.readInt();
    if (size == 0)
    {
      return;
    }

    int objectTypeMapperWork = 10;
    monitor.begin(3 * size + objectTypeMapperWork);

    try
    {
      Connection connection = accessor.getConnection();
      for (int i = 0; i < size; i++)
      {
        EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
        IClassMapping classMapping = getClassMapping(eClass);

        IDBTable table = classMapping.getDBTables().get(0);
        DBUtil.deserializeTable(in, connection, table, monitor.fork());
        rawImportReviseOldRevisions(connection, table, monitor.fork());

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

  protected void rawImportReviseOldRevisions(Connection connection, IDBTable table, OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Must be overridden");
  }

  protected void rawImportList(CDODataInput in, Connection connection, IListMapping listMapping, OMMonitor monitor)
      throws IOException
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
        DBUtil.deserializeTable(in, connection, table, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public String getListJoin(String attrTable, String listTable)
  {
    return " AND " + attrTable + "." + CDODBSchema.ATTRIBUTES_ID + "=" + listTable + "." + CDODBSchema.LIST_REVISION_ID;
  }

  @Override
  protected Collection<EClass> getClassesWithObjectInfo()
  {
    return getClassMappings().keySet();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (objectTypeMapper == null)
    {
      objectTypeMapper = createObjectTypeMapper();
      LifecycleUtil.activate(objectTypeMapper);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(objectTypeMapper);
    objectTypeMapper = null;
    super.doDeactivate();
  }

  private IObjectTypeMapper createObjectTypeMapper()
  {
    ObjectTypeTable table = new ObjectTypeTable();
    table.setMappingStrategy(this);

    int cacheSize = getObjectTypeCacheSize();
    if (cacheSize == 0)
    {
      return table;
    }

    ObjectTypeCache cache = new ObjectTypeCache(cacheSize);
    cache.setMappingStrategy(this);
    cache.setDelegate(table);
    return cache;
  }

  private int getObjectTypeCacheSize()
  {
    int objectTypeCacheSize = ObjectTypeCache.DEFAULT_CACHE_CAPACITY;

    Object value = getProperties().get(PROP_OBJECT_TYPE_CACHE_SIZE);
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
   * This is an intermediate implementation. It should be changed after classmappings support a general way to implement
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
    PreparedStatement stmt = null;
    ResultSet rset = null;

    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    try
    {
      stmt = classMapping.createResourceQueryStatement(accessor, folderID, name, exactMatch, context);
      rset = stmt.executeQuery();

      while (rset.next())
      {
        long longID = rset.getLong(1);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Resource query returned ID " + longID); //$NON-NLS-1$
        }

        CDOID id = CDOIDUtil.createLong(longID);
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
      DBUtil.close(rset);
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  private long getMinLocalID(Connection connection)
  {
    long min = Long.MAX_VALUE;

    String dbName = getStore().getRepository().getName();
    List<String> names = DBUtil.getAllTableNames(connection, dbName);
    for (String name : names)
    {
      String sql = "SELECT MIN(" + CDODBSchema.ATTRIBUTES_ID + ") FROM " + dbName + "." + name + " WHERE 0>"
          + CDODBSchema.ATTRIBUTES_BRANCH;

      Statement stmt = null;
      ResultSet resultSet = null;

      try
      {
        stmt = connection.createStatement();
        resultSet = stmt.executeQuery(sql);

        if (resultSet.next())
        {
          long id = resultSet.getLong(1);
          if (id < min)
          {
            min = id;
          }
        }
      }
      catch (SQLException ex)
      {
        //$FALL-THROUGH$
      }
      finally
      {
        DBUtil.close(resultSet);
        DBUtil.close(stmt);
      }
    }

    return min;
  }
}
