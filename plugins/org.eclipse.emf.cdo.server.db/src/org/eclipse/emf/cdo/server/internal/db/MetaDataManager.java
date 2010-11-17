/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 271444: [DB] Multiple refactorings
 *    Kai Schlamp - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 282976: [DB] Influence Mappings through EAnnotations
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class MetaDataManager extends Lifecycle implements IMetaDataManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MetaDataManager.class);

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private IDBStore store;

  public MetaDataManager(IDBStore store)
  {
    this.store = store;
  }

  public long getMetaID(EModelElement modelElement)
  {
    CDOID cdoid = getPackageRegistry().getMetaInstanceMapper().lookupMetaInstanceID((InternalEObject)modelElement);
    return CDOIDUtil.getLong(cdoid);
  }

  public EModelElement getMetaInstance(long id)
  {
    CDOIDMeta cdoid = CDOIDUtil.createMeta(id);
    InternalEObject metaInstance = getPackageRegistry().getMetaInstanceMapper().lookupMetaInstance(cdoid);
    return (EModelElement)metaInstance;
  }

  public final EPackage[] loadPackageUnit(Connection connection, InternalCDOPackageUnit packageUnit)
  {
    String where = CDODBSchema.PACKAGE_UNITS_ID.getName() + "='" + packageUnit.getID() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
    Object[] values = DBUtil.select(connection, where, CDODBSchema.PACKAGE_UNITS_PACKAGE_DATA);
    byte[] bytes = (byte[])values[0];
    EPackage ePackage = createEPackage(packageUnit, bytes);
    return EMFUtil.getAllPackages(ePackage);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection)
  {
    return readPackageUnits(connection, DBStore.UNSPECIFIED_DATE, DBStore.UNSPECIFIED_DATE, new Monitor());
  }

  public final void writePackageUnits(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    try
    {
      monitor.begin(2);
      fillSystemTables(connection, packageUnits, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    // Export package units
    String where = " WHERE p_u." + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelUtil.CORE_PACKAGE_URI + //
        "' AND p_u." + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelUtil.RESOURCE_PACKAGE_URI + //
        "' AND p_u." + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelUtil.TYPES_PACKAGE_URI + //
        "' AND p_u." + CDODBSchema.PACKAGE_UNITS_TIME_STAMP + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, CDODBSchema.PACKAGE_UNITS, "p_u", where);

    // Export package infos
    String join = ", " + CDODBSchema.PACKAGE_UNITS + " p_u" + where + " AND p_i." + CDODBSchema.PACKAGE_INFOS_UNIT
        + "=p_u." + CDODBSchema.PACKAGE_UNITS_ID;
    DBUtil.serializeTable(out, connection, CDODBSchema.PACKAGE_INFOS, "p_i", join);
  }

  public Collection<InternalCDOPackageUnit> rawImport(Connection connection, CDODataInput in, long fromCommitTime,
      long toCommitTime, OMMonitor monitor) throws IOException
  {
    monitor.begin(3);

    try
    {
      DBUtil.deserializeTable(in, connection, CDODBSchema.PACKAGE_UNITS, monitor.fork());
      DBUtil.deserializeTable(in, connection, CDODBSchema.PACKAGE_INFOS, monitor.fork());
      return readPackageUnits(connection, fromCommitTime, toCommitTime, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  protected IDBStore getStore()
  {
    return store;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    checkState(store != null, "Store is not set"); //$NON-NLS-1$
  }

  protected InternalCDOPackageInfo createPackageInfo()
  {
    return (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
  }

  protected InternalCDOPackageUnit createPackageUnit()
  {
    return (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
  }

  private InternalCDOPackageRegistry getPackageRegistry()
  {
    return (InternalCDOPackageRegistry)store.getRepository().getPackageRegistry();
  }

  private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(getPackageRegistry());
    return EMFUtil.createEPackage(packageUnit.getID(), bytes, ZIP_PACKAGE_BYTES, resourceSet, false);
  }

  private byte[] getEPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    CDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();
    return EMFUtil.getEPackageBytes(ePackage, ZIP_PACKAGE_BYTES, packageRegistry);
  }

  private void fillSystemTables(Connection connection, InternalCDOPackageUnit packageUnit, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing package unit: {0}", packageUnit); //$NON-NLS-1$
    }

    InternalCDOPackageInfo[] packageInfos = packageUnit.getPackageInfos();
    Async async = null;
    monitor.begin(1 + packageInfos.length);

    try
    {
      String sql = "INSERT INTO " + CDODBSchema.PACKAGE_UNITS + " VALUES (?, ?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$
      DBUtil.trace(sql);
      PreparedStatement pstmt = null;

      try
      {
        async = monitor.forkAsync();
        pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, packageUnit.getID());
        pstmt.setInt(2, packageUnit.getOriginalType().ordinal());
        pstmt.setLong(3, packageUnit.getTimeStamp());
        pstmt.setBytes(4, getEPackageBytes(packageUnit));

        if (pstmt.execute())
        {
          throw new DBException("No result set expected"); //$NON-NLS-1$
        }

        if (pstmt.getUpdateCount() == 0)
        {
          throw new DBException("No row inserted into table " + CDODBSchema.PACKAGE_UNITS); //$NON-NLS-1$
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        DBUtil.close(pstmt);
        if (async != null)
        {
          async.stop();
        }
      }

      for (InternalCDOPackageInfo packageInfo : packageInfos)
      {
        fillSystemTables(connection, packageInfo, monitor); // Don't fork monitor
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void fillSystemTables(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    try
    {
      monitor.begin(packageUnits.length);
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        fillSystemTables(connection, packageUnit, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void fillSystemTables(Connection connection, InternalCDOPackageInfo packageInfo, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing package info: {0}", packageInfo); //$NON-NLS-1$
    }

    String packageURI = packageInfo.getPackageURI();
    String parentURI = packageInfo.getParentURI();
    String unitID = packageInfo.getPackageUnit().getID();
    CDOIDMetaRange metaIDRange = packageInfo.getMetaIDRange();
    long metaLB = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getLowerBound()).getLongValue();
    long metaUB = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getUpperBound()).getLongValue();

    String sql = "INSERT INTO " + CDODBSchema.PACKAGE_INFOS + " VALUES (?, ?, ?, ?, ?)"; //$NON-NLS-1$ //$NON-NLS-2$
    DBUtil.trace(sql);
    PreparedStatement pstmt = null;
    Async async = monitor.forkAsync();

    try
    {
      pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, packageURI);
      pstmt.setString(2, parentURI);
      pstmt.setString(3, unitID);
      pstmt.setLong(4, metaLB);
      pstmt.setLong(5, metaUB);

      if (pstmt.execute())
      {
        throw new DBException("No result set expected"); //$NON-NLS-1$
      }

      if (pstmt.getUpdateCount() == 0)
      {
        throw new DBException("No row inserted into table " + CDODBSchema.PACKAGE_INFOS); //$NON-NLS-1$
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(pstmt);
      if (async != null)
      {
        async.stop();
      }
    }
  }

  private Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection, long fromCommitTime,
      long toCommitTime, OMMonitor monitor)
  {
    final Map<String, InternalCDOPackageUnit> packageUnits = new HashMap<String, InternalCDOPackageUnit>();
    IDBRowHandler unitRowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        InternalCDOPackageUnit packageUnit = createPackageUnit();
        packageUnit.setOriginalType(CDOPackageUnit.Type.values()[(Integer)values[1]]);
        packageUnit.setTimeStamp((Long)values[2]);
        packageUnits.put((String)values[0], packageUnit);
        return true;
      }
    };

    String where = null;
    if (fromCommitTime != DBStore.UNSPECIFIED_DATE)
    {
      where = CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelUtil.CORE_PACKAGE_URI + "' AND "
          + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelUtil.RESOURCE_PACKAGE_URI + "' AND "
          + CDODBSchema.PACKAGE_UNITS_ID + "<>'" + CDOModelUtil.TYPES_PACKAGE_URI + "' AND "
          + CDODBSchema.PACKAGE_UNITS_TIME_STAMP + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    }

    DBUtil.select(connection, unitRowHandler, where, CDODBSchema.PACKAGE_UNITS_ID,
        CDODBSchema.PACKAGE_UNITS_ORIGINAL_TYPE, CDODBSchema.PACKAGE_UNITS_TIME_STAMP);

    final Map<String, List<InternalCDOPackageInfo>> packageInfos = new HashMap<String, List<InternalCDOPackageInfo>>();
    IDBRowHandler infoRowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        long metaLB = (Long)values[3];
        long metaUB = (Long)values[4];
        CDOIDMetaRange metaIDRange = metaLB == 0 ? null : CDOIDUtil.createMetaRange(CDOIDUtil.createMeta(metaLB),
            (int)(metaUB - metaLB) + 1);

        InternalCDOPackageInfo packageInfo = createPackageInfo();
        packageInfo.setPackageURI((String)values[1]);
        packageInfo.setParentURI((String)values[2]);
        packageInfo.setMetaIDRange(metaIDRange);

        String unit = (String)values[0];
        List<InternalCDOPackageInfo> list = packageInfos.get(unit);
        if (list == null)
        {
          list = new ArrayList<InternalCDOPackageInfo>();
          packageInfos.put(unit, list);
        }

        list.add(packageInfo);
        return true;
      }
    };

    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      DBUtil.select(connection, infoRowHandler, CDODBSchema.PACKAGE_INFOS_UNIT, CDODBSchema.PACKAGE_INFOS_URI,
          CDODBSchema.PACKAGE_INFOS_PARENT, CDODBSchema.PACKAGE_INFOS_META_LB, CDODBSchema.PACKAGE_INFOS_META_UB);
    }
    finally
    {
      async.stop();
      monitor.done();
    }

    for (Entry<String, InternalCDOPackageUnit> entry : packageUnits.entrySet())
    {
      String id = entry.getKey();
      InternalCDOPackageUnit packageUnit = entry.getValue();

      List<InternalCDOPackageInfo> list = packageInfos.get(id);
      InternalCDOPackageInfo[] array = list.toArray(new InternalCDOPackageInfo[list.size()]);
      packageUnit.setPackageInfos(array);
    }

    return packageUnits.values();
  }
}
