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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IObjectTypeCache;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.AbstractMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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
   * The associated object type cache.
   */
  private IObjectTypeCache objectTypeCache;

  public CDOClassifierRef readObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    return objectTypeCache.getObjectType(accessor, id);
  }

  public void putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    objectTypeCache.putObjectType(accessor, timeStamp, id, type);
  }

  public long[] repairAfterCrash(IDBAdapter dbAdapter, Connection connection)
  {
    long minLocalID = getMinLocalID(connection);
    long maxID = objectTypeCache.getMaxID(connection);
    long[] maxTimes = getMaxTimes(connection);

    long[] result = { minLocalID, maxID, maxTimes[0], maxTimes[1] };
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

    for (IClassMapping classMapping : getClassMappings(true).values())
    {
      out.writeBoolean(true);
      EClass eClass = classMapping.getEClass();
      out.writeCDOClassifierRef(eClass);

      IDBTable table = classMapping.getDBTables().get(0);
      DBUtil.serializeTable(out, connection, table, "a_t", attrSuffix);

      for (IListMapping listMapping : classMapping.getListMappings())
      {
        rawExportList(out, connection, listMapping, table, attrSuffix);
      }
    }

    out.writeBoolean(false);
    objectTypeCache.rawExport(connection, out, fromCommitTime, toCommitTime);
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

  public void rawImport(IDBStoreAccessor accessor, CDODataInput in) throws IOException
  {
    Connection connection = accessor.getConnection();

    while (in.readBoolean())
    {
      EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
      IClassMapping classMapping = getClassMapping(eClass);

      IDBTable table = classMapping.getDBTables().get(0);
      DBUtil.deserializeTable(in, connection, table);
      rawImportReviseOldRevisions(connection, table);

      for (IListMapping listMapping : classMapping.getListMappings())
      {
        rawImportList(in, connection, listMapping);
      }
    }

    objectTypeCache.rawImport(connection, in);
  }

  protected void rawImportReviseOldRevisions(Connection connection, IDBTable table)
  {
    throw new UnsupportedOperationException("Must be overridden");
  }

  protected void rawImportList(CDODataInput in, Connection connection, IListMapping listMapping) throws IOException
  {
    for (IDBTable table : listMapping.getDBTables())
    {
      DBUtil.deserializeTable(in, connection, table);
    }
  }

  protected String getListJoin(String attrTable, String listTable)
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
    if (objectTypeCache == null)
    {
      objectTypeCache = createObjectTypeCache();
      LifecycleUtil.activate(objectTypeCache);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(objectTypeCache);
    objectTypeCache = null;
    super.doDeactivate();
  }

  private IObjectTypeCache createObjectTypeCache()
  {
    ObjectTypeCache cache = new ObjectTypeCache();
    cache.setMappingStrategy(this);
    return cache;
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
    if (getStore().getRepository().isSupportingBranches())
    {
      for (IClassMapping classMapping : getClassMappings().values())
      {
        IDBTable table = classMapping.getDBTables().get(0);
        IDBField field = table.getField(CDODBSchema.ATTRIBUTES_ID);
        long id = DBUtil.selectMinimumLong(connection, field, "0>" + CDODBSchema.ATTRIBUTES_BRANCH); //$NON-NLS-1$
        if (id < min)
        {
          min = id;
        }
      }
    }

    return min;
  }

  private long[] getMaxTimes(Connection connection)
  {
    long max = CDORevision.UNSPECIFIED_DATE;
    long maxNonLocal = CDORevision.UNSPECIFIED_DATE;
    final String where = CDODBSchema.ATTRIBUTES_BRANCH + ">=" + CDOBranch.MAIN_BRANCH_ID;

    for (IClassMapping classMapping : getClassMappings().values())
    {
      IDBTable table = classMapping.getDBTables().get(0);
      IDBField field = table.getField(CDODBSchema.ATTRIBUTES_CREATED);
      long timeStamp = DBUtil.selectMaximumLong(connection, field);
      if (timeStamp > max)
      {
        max = timeStamp;
      }

      timeStamp = DBUtil.selectMaximumLong(connection, field, where);
      if (timeStamp > maxNonLocal)
      {
        maxNonLocal = timeStamp;
      }
    }

    return new long[] { max, maxNonLocal };
  }
}
