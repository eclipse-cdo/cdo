/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.db4o;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
public class PrimitivePackageUnit
{
  private String id;

  private Integer ordinalType;

  private Long timeStamp;

  private List<Byte> ePackageBytes;

  public PrimitivePackageUnit(String id, Integer ordinalType, Long timeStamp, List<Byte> ePackageBytes)
  {
    setId(id);
    setOrdinalType(ordinalType);
    setTimeStamp(timeStamp);
    setePackageBytes(ePackageBytes);
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

  public void setePackageBytes(List<Byte> ePackageBytes)
  {
    this.ePackageBytes = ePackageBytes;
  }

  public List<Byte> getePackageBytes()
  {
    return ePackageBytes;
  }

  public static PrimitivePackageUnit getPrimitivePackageUnit(IStore store, InternalCDOPackageUnit packageUnit)
  {
    return new PrimitivePackageUnit(new String(packageUnit.getID()), new Integer(packageUnit.getOriginalType()
        .ordinal()), new Long(packageUnit.getTimeStamp()), getEPackageBytes(store, packageUnit));
  }

  public static InternalCDOPackageUnit getPackageUnit(InternalCDOPackageRegistry packageRegistry,
      PrimitivePackageUnit packageUnit)
  {
    InternalCDOPackageUnit cdoPackageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
    CDOPackageUnit.Type type = CDOPackageUnit.Type.values()[packageUnit.getOrdinalType()];
    cdoPackageUnit.setOriginalType(type);
    cdoPackageUnit.setTimeStamp(packageUnit.getTimeStamp());
    EPackage ePackage = getEPackageFromBytes(packageUnit.getePackageBytes());
    cdoPackageUnit.setPackageRegistry(packageRegistry);
    cdoPackageUnit.init(ePackage);
    return cdoPackageUnit;
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
}
