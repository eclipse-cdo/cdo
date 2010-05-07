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

import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStore;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyPackageInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyPackageUnit;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.SmartLock;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;

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

  private static final ContextTracer TRACER_ERROR = new ContextTracer(OM.ERROR, ObjyPackageHandler.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjyPackageHandler.class);

  private ObjyScope objyScope = null;

  private ObjectivityStore store = null;

  private boolean zipped = false;

  public ObjyPackageHandler(ObjectivityStore store, ObjyScope objyScope, boolean zipped)
  {
    this.store = store;
    this.objyScope = objyScope;
    this.zipped = zipped;
  }

  public void writePackages(InternalCDOPackageUnit packageUnit, OMMonitor monitor)
  {
    try
    {
      SmartLock.lock(objyScope.getContainerObj());
      InternalCDOPackageInfo[] packageInfos = packageUnit.getPackageInfos();
      monitor.begin(1 + packageInfos.length);

      if (TRACER_INFO.isEnabled())
      {
        TRACER_INFO.format("Writing package unit: {0}", packageUnit); //$NON-NLS-1$
      }

      byte[] ePackageAsBytes = getEPackageBytes(packageUnit);

      ObjyPackageUnit ooPackageUnit = new ObjyPackageUnit(ePackageAsBytes.length);
      objyScope.getContainerObj().cluster(ooPackageUnit);

      ooPackageUnit.setId(packageUnit.getID());
      ooPackageUnit.setOrdinal(packageUnit.getOriginalType().ordinal());
      ooPackageUnit.setTimeStamp(packageUnit.getTimeStamp());
      ooPackageUnit.setPackageAsBytes(ePackageAsBytes);

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("... writing OoPackageUnit.getId(): " + ooPackageUnit.getId());
      }

      ObjyPackageInfo ooPackageInfo;
      for (InternalCDOPackageInfo packageInfo : packageInfos)
      {
        ooPackageInfo = createPackageInfo(packageInfo, monitor); // Don't fork monitor
        ooPackageUnit.addPackageInfo(ooPackageInfo);
        // make sure we have the mapping between the Package name an the nsURI
        // set mapping between package name and the nsURI
        // getStore().addPackageMapping(packageInfo.getPackageURI(), ooPackageInfo.getPackageName());
        // getStore().addPackageMapping(ooPackageInfo.getPackageName(), packageInfo.getPackageURI());
        getStore().addPackageMapping(packageInfo.getPackageURI(), ooPackageInfo.getPackageUniqueName());
        getStore().addPackageMapping(ooPackageInfo.getPackageUniqueName(), packageInfo.getPackageURI());

        // we might as well create the schema in Objy.
        EPackage ePackage = packageInfo.getEPackage();
        ObjySchema.registerEPackage(ePackage);
      }
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

    CDOIDMetaRange metaIDRange = packageInfo.getMetaIDRange();
    long metaLB = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getLowerBound()).getLongValue();
    long metaUB = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getUpperBound()).getLongValue();
    ooPackageInfo.setMetaLB(metaLB);
    ooPackageInfo.setMetaUB(metaUB);

    return ooPackageInfo;
  }

  private byte[] getEPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    return EMFUtil.getEPackageBytes(ePackage, zipped, getPackageRegistry());
  }

  private CDOPackageRegistry getPackageRegistry()
  {
    return getStore().getRepository().getPackageRegistry();
  }

  private ObjectivityStore getStore()
  {
    return store;
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    final Map<ObjyPackageUnit, InternalCDOPackageUnit> packageUnitsMap = new HashMap<ObjyPackageUnit, InternalCDOPackageUnit>();

    Iterator<?> itr = objyScope.getDatabaseObj().scan(ObjyPackageUnit.class.getName());
    while (itr.hasNext())
    {
      ObjyPackageUnit ooPackageUnit = (ObjyPackageUnit)itr.next();
      InternalCDOPackageUnit packageUnit = createPackageUnit();
      packageUnit.setOriginalType(CDOPackageUnit.Type.values()[ooPackageUnit.getOrdinal()]);
      packageUnit.setTimeStamp(ooPackageUnit.getTimeStamp());
      packageUnitsMap.put(ooPackageUnit, packageUnit);
      if (TRACER_INFO.isEnabled())
      {
        TRACER_INFO.format("Read package unit: {0}", packageUnit); //$NON-NLS-1$
      }
    }

    // create the package infos from the units.
    for (Entry<ObjyPackageUnit, InternalCDOPackageUnit> entry : packageUnitsMap.entrySet())
    {
      // scan the relationship.
      List<ObjyPackageInfo> ooPackageInfoList = entry.getKey().getPackageInfos();
      List<InternalCDOPackageInfo> packageInfoList = new ArrayList<InternalCDOPackageInfo>();
      // create the package infos.
      for (ObjyPackageInfo ooPackageInfo : ooPackageInfoList)
      {
        InternalCDOPackageInfo packageInfo = createPackageInfo(ooPackageInfo);
        packageInfoList.add(packageInfo);
        // set mapping between package name and the nsURI
        // getStore().addPackageMapping(packageInfo.getPackageURI(), ooPackageInfo.getPackageName());
        // getStore().addPackageMapping(ooPackageInfo.getPackageName(), packageInfo.getPackageURI());
        getStore().addPackageMapping(packageInfo.getPackageURI(), ooPackageInfo.getPackageUniqueName());
        getStore().addPackageMapping(ooPackageInfo.getPackageUniqueName(), packageInfo.getPackageURI());
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
    long metaLB = ooPackageInfo.getMetaLB();
    long metaUB = ooPackageInfo.getMetaUB();
    CDOIDMetaRange metaIDRange = metaLB == 0 ? null : OBJYCDOIDUtil.createMetaRange(OBJYCDOIDUtil.createMeta(metaLB),
        (int)(metaUB - metaLB) + 1);
    packageInfo.setMetaIDRange(metaIDRange);

    return packageInfo;
  }

  public byte[] readPackageBytes(InternalCDOPackageUnit packageUnit)
  {
    byte[] bytes = null;

    // TODO - we should use the predicate query.
    Iterator<?> itr = objyScope.getDatabaseObj().scan(ObjyPackageUnit.class.getName());
    System.out.println("Looking for packageUnit.ID(): " + packageUnit.getID());
    while (itr.hasNext())
    {
      ObjyPackageUnit ooPackageUnit = (ObjyPackageUnit)itr.next();
      System.out.println("... found OoPackageUnit.getId(): " + ooPackageUnit.getId());
      if (ooPackageUnit.getId().equals(packageUnit.getID()))
      {
        if (TRACER_INFO.isEnabled())
        {
          TRACER_INFO.format("Read package unit with ID: {0}", packageUnit.getID()); //$NON-NLS-1$
        }
        // this is our package...
        bytes = ooPackageUnit.getPackageAsBytes();
        break;
      }
    }
    return bytes;
  }

}
