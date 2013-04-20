/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OPackageUnit
{
  private String id;

  private Integer ordinalType;

  private Long timeStamp;

  private List<Byte> ePackageBytes;

  private List<Pair<String, String>> packageInfos;

  public DB4OPackageUnit(String id, Integer ordinalType, Long timeStamp, List<Byte> ePackageBytes,
      List<Pair<String, String>> packageInfos)
  {
    setId(id);
    setOrdinalType(ordinalType);
    setTimeStamp(timeStamp);
    setEPackageBytes(ePackageBytes);
    setPackageInfos(packageInfos);
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }

  public void setOrdinalType(Integer ordinalType)
  {
    this.ordinalType = ordinalType;
  }

  public Integer getOrdinalType()
  {
    return ordinalType;
  }

  public void setTimeStamp(Long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public Long getTimeStamp()
  {
    return timeStamp;
  }

  public void setEPackageBytes(List<Byte> ePackageBytes)
  {
    this.ePackageBytes = ePackageBytes;
  }

  public List<Byte> getEPackageBytes()
  {
    return ePackageBytes;
  }

  public static DB4OPackageUnit getPrimitivePackageUnit(IStore store, InternalCDOPackageUnit packageUnit)
  {
    return new DB4OPackageUnit(new String(packageUnit.getID()), new Integer(packageUnit.getOriginalType().ordinal()),
        new Long(packageUnit.getTimeStamp()), getEPackageBytes(store, packageUnit),
        getPackageInfosAsPair(packageUnit.getPackageInfos()));
  }

  public static InternalCDOPackageUnit getPackageUnit(DB4OPackageUnit packageUnit)
  {
    InternalCDOPackageUnit cdoPackageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
    CDOPackageUnit.Type type = CDOPackageUnit.Type.values()[packageUnit.getOrdinalType()];
    cdoPackageUnit.setOriginalType(type);
    cdoPackageUnit.setTimeStamp(packageUnit.getTimeStamp());
    cdoPackageUnit.setPackageInfos(getPackageInfos(packageUnit));
    return cdoPackageUnit;
  }

  private static InternalCDOPackageInfo[] getPackageInfos(DB4OPackageUnit packageUnit)
  {
    List<InternalCDOPackageInfo> list = new ArrayList<InternalCDOPackageInfo>();
    for (Pair<String, String> infoPair : packageUnit.getPackageInfos())
    {
      InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
      packageInfo.setParentURI(infoPair.getElement1());
      packageInfo.setPackageURI(infoPair.getElement2());
      list.add(packageInfo);
    }
    return list.toArray(new InternalCDOPackageInfo[list.size()]);
  }

  private static List<Byte> getEPackageBytes(IStore store, InternalCDOPackageUnit packageUnit)
  {
    EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
    CDOPackageRegistry packageRegistry = store.getRepository().getPackageRegistry();
    byte[] bytes = EMFUtil.getEPackageBytes(ePackage, true, packageRegistry);
    List<Byte> bytesObject = new ArrayList<Byte>();
    for (byte bt : bytes)
    {
      bytesObject.add(new Byte(bt));
    }

    return bytesObject;
  }

  private static EPackage getEPackageFromBytes(List<Byte> ePackageBytesList)
  {
    ResourceSet rSet = new ResourceSetImpl();
    byte[] packageBytes = new byte[ePackageBytesList.size()];
    for (int i = 0; i < packageBytes.length; i++)
    {
      packageBytes[i] = ePackageBytesList.get(i);
    }

    EPackage ePackage = EMFUtil.createEPackage("", packageBytes, true, rSet, false);
    return ePackage;
  }

  public EPackage getEPackage()
  {
    return getEPackageFromBytes(getEPackageBytes());
  }

  private static List<Pair<String, String>> getPackageInfosAsPair(InternalCDOPackageInfo[] packageInfos)
  {
    List<Pair<String, String>> infos = new ArrayList<Pair<String, String>>();
    for (InternalCDOPackageInfo info : packageInfos)
    {
      Pair<String, String> pair = Pair.create(info.getParentURI(), info.getPackageURI());
      infos.add(pair);
    }

    return infos;
  }

  public void setPackageInfos(List<Pair<String, String>> packageInfos)
  {
    this.packageInfos = packageInfos;
  }

  public List<Pair<String, String>> getPackageInfos()
  {
    return packageInfos;
  }
}
