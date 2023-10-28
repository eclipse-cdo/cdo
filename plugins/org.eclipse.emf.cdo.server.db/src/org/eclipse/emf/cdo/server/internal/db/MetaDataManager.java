/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageInfosTable;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTables.PackageUnitsTable;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class MetaDataManager extends Lifecycle implements IMetaDataManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MetaDataManager.class);

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private DBStore store;

  private Map<EModelElement, CDOID> modelElementToMetaID = new HashMap<>();

  private Map<CDOID, EModelElement> metaIDToModelElement = CDOIDUtil.createMap();

  public MetaDataManager(IDBStore store)
  {
    this.store = (DBStore)store;
  }

  @Override
  public synchronized CDOID getMetaID(EModelElement modelElement, long commitTime)
  {
    CDOID metaID = modelElementToMetaID.get(modelElement);
    if (metaID != null)
    {
      return metaID;
    }

    IDBStoreAccessor accessor = (IDBStoreAccessor)StoreThreadLocal.getAccessor();
    String uri = EcoreUtil.getURI(modelElement).toString();
    metaID = store.getIDHandler().mapURI(accessor, uri, commitTime);
    cacheMetaIDMapping(modelElement, metaID);

    return metaID;
  }

  @Override
  public synchronized EModelElement getMetaInstance(CDOID id)
  {
    EModelElement modelElement = metaIDToModelElement.get(id);
    if (modelElement != null)
    {
      return modelElement;
    }

    IDBStoreAccessor accessor = (IDBStoreAccessor)StoreThreadLocal.getAccessor();
    String uri = store.getIDHandler().unmapURI(accessor, id);

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.setPackageRegistry(getStore().getRepository().getPackageRegistry());

    return (EModelElement)resourceSet.getEObject(URI.createURI(uri), true);
  }

  @Override
  public synchronized void clearMetaIDMappings()
  {
    modelElementToMetaID.clear();
    metaIDToModelElement.clear();
  }

  @Override
  public final EPackage[] loadPackageUnit(Connection connection, InternalCDOPackageUnit packageUnit)
  {
    byte[] bytes = store.tables().packageUnits().loadPackageUnitBytes(connection, packageUnit);
    EPackage ePackage = createEPackage(packageUnit, bytes);
    return EMFUtil.getAllPackages(ePackage);
  }

  @Override
  public Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection)
  {
    return readPackageUnits(connection, CDOBranchPoint.UNSPECIFIED_DATE, CDOBranchPoint.UNSPECIFIED_DATE, new Monitor());
  }

  @Override
  public final void writePackageUnits(Connection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    try
    {
      monitor.begin(2);
      fillSystemTables((IDBConnection)connection, packageUnits, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    // Export package units
    PackageUnitsTable packageUnits = store.tables().packageUnits();
    String where = " WHERE p_u." + packageUnits.id() + "<>'" + CDOModelConstants.CORE_PACKAGE_URI + //
        "' AND p_u." + packageUnits.id() + "<>'" + CDOModelConstants.RESOURCE_PACKAGE_URI + //
        "' AND p_u." + packageUnits.id() + "<>'" + CDOModelConstants.TYPES_PACKAGE_URI + //
        "' AND p_u." + packageUnits.timeStamp() + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, packageUnits.table(), "p_u", where);

    // Export package infos
    PackageInfosTable packageInfos = store.tables().packageInfos();
    String join = ", " + packageUnits.table() + " p_u" + where + " AND p_i." + packageInfos.unit() + "=p_u." + packageUnits.id();
    DBUtil.serializeTable(out, connection, packageInfos.table(), "p_i", join);
  }

  @Override
  public Collection<InternalCDOPackageUnit> rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor)
      throws IOException
  {
    monitor.begin(3);

    try
    {
      PackageUnitsTable packageUnits = store.tables().packageUnits();
      DBUtil.deserializeTable(in, connection, packageUnits.table(), monitor.fork());

      PackageInfosTable packageInfos = store.tables().packageInfos();
      DBUtil.deserializeTable(in, connection, packageInfos.table(), monitor.fork());

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
    checkState(store, "Store is not set"); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    clearMetaIDMappings();
    super.doDeactivate();
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
    return store.getRepository().getPackageRegistry();
  }

  private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(getPackageRegistry());
    return EMFUtil.createEPackage(packageUnit.getID(), bytes, ZIP_PACKAGE_BYTES, resourceSet, false);
  }

  private byte[] getEPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    return EMFUtil.getEPackageBytes(ePackage, ZIP_PACKAGE_BYTES, getPackageRegistry());
  }

  private void fillSystemTables(IDBConnection connection, InternalCDOPackageUnit packageUnit, OMMonitor monitor)
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
      PackageUnitsTable packageUnits = store.tables().packageUnits();
      String sql = "INSERT INTO " + packageUnits + " (" + packageUnits.id() + ", " //$NON-NLS-1$ //$NON-NLS-2$
          + packageUnits.originalType() + ", " + packageUnits.timeStamp() + ", " + packageUnits.packageData() + ") VALUES (?, ?, ?, ?)";
      DBUtil.trace(sql);

      IDBPreparedStatement stmt = connection.prepareStatement(sql, ReuseProbability.MEDIUM);

      try
      {
        async = monitor.forkAsync();
        stmt.setString(1, packageUnit.getID());
        stmt.setInt(2, packageUnit.getOriginalType().ordinal());
        stmt.setLong(3, packageUnit.getTimeStamp());
        stmt.setBytes(4, getEPackageBytes(packageUnit));

        if (stmt.execute())
        {
          throw new DBException("No result set expected"); //$NON-NLS-1$
        }

        if (stmt.getUpdateCount() == 0)
        {
          throw new DBException("No row inserted into table " + packageUnits.table().getName()); //$NON-NLS-1$
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
      finally
      {
        DBUtil.close(stmt);
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

  private void fillSystemTables(IDBConnection connection, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
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

  private void fillSystemTables(IDBConnection connection, InternalCDOPackageInfo packageInfo, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing package info: {0}", packageInfo); //$NON-NLS-1$
    }

    String packageURI = packageInfo.getPackageURI();
    String parentURI = packageInfo.getParentURI();
    String unitID = packageInfo.getPackageUnit().getID();

    PackageInfosTable packageInfos = store.tables().packageInfos();
    String sql = "INSERT INTO " + packageInfos + " (" + packageInfos.uri() + ", " //$NON-NLS-1$ //$NON-NLS-2$
        + packageInfos.parent() + ", " + packageInfos.unit() + ") VALUES (?, ?, ?)";
    DBUtil.trace(sql);

    IDBPreparedStatement stmt = connection.prepareStatement(sql, ReuseProbability.MEDIUM);
    Async async = monitor.forkAsync();

    try
    {
      stmt.setString(1, packageURI);
      stmt.setString(2, parentURI);
      stmt.setString(3, unitID);

      if (stmt.execute())
      {
        throw new DBException("No result set expected"); //$NON-NLS-1$
      }

      if (stmt.getUpdateCount() == 0)
      {
        throw new DBException("No row inserted into table " + packageInfos.table().getName()); //$NON-NLS-1$
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
      if (async != null)
      {
        async.stop();
      }
    }
  }

  private Collection<InternalCDOPackageUnit> readPackageUnits(Connection connection, long fromCommitTime, long toCommitTime, OMMonitor monitor)
  {
    final Map<String, InternalCDOPackageUnit> packageUnits = new HashMap<>();
    IDBRowHandler unitRowHandler = new IDBRowHandler()
    {
      @Override
      public boolean handle(int row, final Object... values)
      {
        int index = DBUtil.asInt(values[1]);
        long timestamp = DBUtil.asLong(values[2]);

        InternalCDOPackageUnit packageUnit = createPackageUnit();
        packageUnit.setOriginalType(CDOPackageUnit.Type.values()[index]);
        packageUnit.setTimeStamp(timestamp);

        packageUnits.put((String)values[0], packageUnit);
        return true;
      }
    };

    PackageUnitsTable packageUnitsTable = store.tables().packageUnits();

    String where = null;
    if (fromCommitTime != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      where = packageUnitsTable.id() + "<>'" + CDOModelConstants.CORE_PACKAGE_URI + "' AND " + packageUnitsTable.id() + "<>'"
          + CDOModelConstants.RESOURCE_PACKAGE_URI + "' AND " + packageUnitsTable.id() + "<>'" + CDOModelConstants.TYPES_PACKAGE_URI + "' AND "
          + packageUnitsTable.timeStamp() + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    }

    DBUtil.select(connection, unitRowHandler, where, packageUnitsTable.id(), packageUnitsTable.originalType(), packageUnitsTable.timeStamp());

    final Map<String, List<InternalCDOPackageInfo>> packageInfos = new HashMap<>();
    IDBRowHandler infoRowHandler = new IDBRowHandler()
    {
      @Override
      public boolean handle(int row, final Object... values)
      {
        InternalCDOPackageInfo packageInfo = createPackageInfo();
        packageInfo.setPackageURI((String)values[1]);
        packageInfo.setParentURI((String)values[2]);

        String unit = (String)values[0];
        List<InternalCDOPackageInfo> list = packageInfos.get(unit);
        if (list == null)
        {
          list = new ArrayList<>();
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
      PackageInfosTable packageInfosTable = store.tables().packageInfos();
      DBUtil.select(connection, infoRowHandler, packageInfosTable.unit(), packageInfosTable.uri(), packageInfosTable.parent());
    }
    finally
    {
      async.stop();
      monitor.done();
    }

    for (Map.Entry<String, InternalCDOPackageUnit> entry : packageUnits.entrySet())
    {
      String id = entry.getKey();
      InternalCDOPackageUnit packageUnit = entry.getValue();

      List<InternalCDOPackageInfo> list = packageInfos.get(id);
      InternalCDOPackageInfo[] array = list.toArray(new InternalCDOPackageInfo[list.size()]);
      packageUnit.setPackageInfos(array);
    }

    return packageUnits.values();
  }

  private void cacheMetaIDMapping(EModelElement modelElement, CDOID metaID)
  {
    modelElementToMetaID.put(modelElement, metaID);
    metaIDToModelElement.put(metaID, modelElement);
  }
}
