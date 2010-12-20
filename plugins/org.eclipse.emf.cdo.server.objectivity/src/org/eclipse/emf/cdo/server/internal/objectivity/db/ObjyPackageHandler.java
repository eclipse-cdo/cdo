/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.db;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyPackageInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyPackageUnit;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.SmartLock;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;

import com.objy.db.app.Session;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ObjyPackageHandler
{

  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjyPackageHandler.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjyPackageHandler.class);

  protected ooId packageMapId;

  private boolean zipped = true;

  public ObjyPackageHandler(String repositoryName)
  {
    // this.store = store;
    packageMapId = ObjyDb.getOrCreatePackageMap(repositoryName);
  }

  /***
   * Factory method to create the PackageMap, which is an ooMap
   */
  public static ooId create(ooId scopeContOid)
  {
    ooMap map = new ooMap();
    ooObj clusterObject = ooObj.create_ooObj(scopeContOid);
    clusterObject.cluster(map);
    return map.getOid();
  }

  public void writePackages(CDOPackageRegistry packageRegistry, InternalCDOPackageUnit packageUnit, OMMonitor monitor)
  {
    try
    {
      ooMap packageMap = getMap();

      SmartLock.lock(packageMap.getContainer());
      InternalCDOPackageInfo[] packageInfos = packageUnit.getPackageInfos();
      monitor.begin(1 + packageInfos.length);

      if (TRACER_INFO.isEnabled())
      {
        TRACER_INFO.format("Writing package unit: {0}", packageUnit); //$NON-NLS-1$
      }

      byte[] ePackageAsBytes = getEPackageBytes(packageRegistry, packageUnit);

      ObjyPackageUnit objyPackageUnit = new ObjyPackageUnit(ePackageAsBytes.length);
      packageMap.cluster(objyPackageUnit);

      objyPackageUnit.setId(packageUnit.getID());
      objyPackageUnit.setOrdinal(packageUnit.getOriginalType().ordinal());
      objyPackageUnit.setTimeStamp(packageUnit.getTimeStamp());
      objyPackageUnit.setPackageAsBytes(ePackageAsBytes);

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("... writing ObjyPackageUnit.getId(): " + objyPackageUnit.getId());
      }

      ObjyPackageInfo objyPackageInfo;
      for (InternalCDOPackageInfo packageInfo : packageInfos)
      {
        objyPackageInfo = createPackageInfo(packageInfo, monitor); // Don't fork monitor
        objyPackageUnit.addPackageInfo(objyPackageInfo);
        // make sure we have the mapping between the Package name an the nsURI
        // set mapping between package name and the nsURI
        // getStore().addPackageMapping(packageInfo.getPackageURI(), ooPackageInfo.getPackageName());
        // getStore().addPackageMapping(ooPackageInfo.getPackageName(), packageInfo.getPackageURI());
        String objyPackageName = ObjySchema.getObjyPackageName(packageInfo.getPackageURI());
        ObjySchema.setPackageNameMapping(packageInfo.getPackageURI(), objyPackageName);
        ObjySchema.setPackageNameMapping(objyPackageName, packageInfo.getPackageURI());

        // we might as well create the schema in Objy, although I`m not sure if we needed for the ecore pacakge.
        EPackage ePackage = packageInfo.getEPackage();
        ObjySchema.registerEPackage(ePackage);
      }

      // add the package unit to the map.
      packageMap.add(objyPackageUnit, objyPackageUnit.getId());
    }
    finally
    {
      monitor.done();
    }
  }

  private ObjyPackageInfo createPackageInfo(InternalCDOPackageInfo packageInfo, OMMonitor monitor)
  {
    if (TRACER_INFO.isEnabled())
    {
      TRACER_INFO.format("Writing package info: {0}", packageInfo); //$NON-NLS-1$
    }

    ObjyPackageInfo ooPackageInfo = new ObjyPackageInfo();
    ooPackageInfo.setPackageURI(packageInfo.getPackageURI());
    ooPackageInfo.setParentURI(packageInfo.getParentURI());
    ooPackageInfo.setUnitID(packageInfo.getPackageUnit().getID());
    ooPackageInfo.setPackageName(packageInfo.getEPackage().getName());

    return ooPackageInfo;
  }

  private byte[] getEPackageBytes(CDOPackageRegistry packageRegistry, InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    return EMFUtil.getEPackageBytes(ePackage, zipped, packageRegistry);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    final Map<ObjyPackageUnit, InternalCDOPackageUnit> packageUnitsMap = new HashMap<ObjyPackageUnit, InternalCDOPackageUnit>();

    ooMap packageMap = getMap();

    Iterator<?> itr = packageMap.elements();
    while (itr.hasNext())
    {
      ObjyPackageUnit objyPackageUnit = (ObjyPackageUnit)itr.next();
      InternalCDOPackageUnit packageUnit = createPackageUnit();
      packageUnit.setOriginalType(CDOPackageUnit.Type.values()[objyPackageUnit.getOrdinal()]);
      packageUnit.setTimeStamp(objyPackageUnit.getTimeStamp());
      packageUnitsMap.put(objyPackageUnit, packageUnit);
      if (TRACER_INFO.isEnabled())
      {
        TRACER_INFO.format("Read package unit: {0}", packageUnit); //$NON-NLS-1$
      }
    }

    // create the package infos from the units.
    for (Entry<ObjyPackageUnit, InternalCDOPackageUnit> entry : packageUnitsMap.entrySet())
    {
      // scan the relationship.
      List<ObjyPackageInfo> objyPackageInfoList = entry.getKey().getPackageInfos();
      List<InternalCDOPackageInfo> packageInfoList = new ArrayList<InternalCDOPackageInfo>();
      // create the package infos.
      for (ObjyPackageInfo objyPackageInfo : objyPackageInfoList)
      {
        InternalCDOPackageInfo packageInfo = createPackageInfo(objyPackageInfo);
        packageInfoList.add(packageInfo);
        // set mapping between package URI and the package name used in Objy Schema.
        // getStore().addPackageMapping(packageInfo.getPackageURI(), ooPackageInfo.getPackageName());
        // getStore().addPackageMapping(ooPackageInfo.getPackageName(), packageInfo.getPackageURI());
        String objyPackageName = ObjySchema.getObjyPackageName(packageInfo.getPackageURI());
        ObjySchema.setPackageNameMapping(packageInfo.getPackageURI(), objyPackageName);
        ObjySchema.setPackageNameMapping(objyPackageName, packageInfo.getPackageURI());
      }
      // add the package infos to the unit.
      InternalCDOPackageInfo[] array = packageInfoList.toArray(new InternalCDOPackageInfo[packageInfoList.size()]);
      entry.getValue().setPackageInfos(array);
    }

    return packageUnitsMap.values();
  }

  protected InternalCDOPackageUnit createPackageUnit()
  {
    return (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
  }

  protected InternalCDOPackageInfo createPackageInfo()
  {
    return (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
  }

  private InternalCDOPackageInfo createPackageInfo(ObjyPackageInfo ooPackageInfo)
  {
    if (TRACER_INFO.isEnabled())
    {
      TRACER_INFO.format("create package info: {0}", ooPackageInfo); //$NON-NLS-1$
    }

    InternalCDOPackageInfo packageInfo = createPackageInfo();

    packageInfo.setPackageURI(ooPackageInfo.getPackageURI());
    packageInfo.setParentURI(ooPackageInfo.getParentURI());
    // TODO - do we need this!!!! setUnitID(packageInfo.getPackageUnit().getID());

    return packageInfo;
  }

  public byte[] readPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    byte[] bytes = null;

    ooMap map = getMap();
    String packageUnitId = packageUnit.getID();
    if (TRACER_INFO.isEnabled())
    {
      TRACER_INFO.format("Looking for package unit with ID: {0}", packageUnitId); //$NON-NLS-1$
    }
    if (map.isMember(packageUnitId))
    {
      if (TRACER_INFO.isEnabled())
      {
        TRACER_INFO.format("Reading package unit with ID: {0}", packageUnitId); //$NON-NLS-1$
      }
      ObjyPackageUnit objyPackageUnit = (ObjyPackageUnit)map.lookup(packageUnitId);
      // this is our package...
      bytes = objyPackageUnit.getPackageAsBytes();
    }
    return bytes;
  }

  /***
   * This function assume we are in an Objy transaction.
   */
  private ooMap getMap()
  {
    ooMap map = null;
    map = (ooMap)Session.getCurrent().getFD().objectFrom(packageMapId);
    return map;
  }

}
