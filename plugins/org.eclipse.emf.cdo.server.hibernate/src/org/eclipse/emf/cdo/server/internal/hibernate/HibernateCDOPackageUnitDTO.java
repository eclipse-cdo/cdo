/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

/**
 * A DTO for convenience of storing a CDOPackageUnit in the db.
 * 
 * @author Martin Taal
 */
public class HibernateCDOPackageUnitDTO
{
  private CDOPackageUnit.Type originalType;

  private long timeStamp;

  private InternalCDOPackageInfo[] packageInfos;

  private byte[] ePackageBlob;

  public HibernateCDOPackageUnitDTO()
  {
  }

  public HibernateCDOPackageUnitDTO(CDOPackageUnit cdoPackageUnit)
  {
    setPackageInfos((InternalCDOPackageInfo[])cdoPackageUnit.getPackageInfos());
    setOriginalType(cdoPackageUnit.getOriginalType());
    setTimeStamp(cdoPackageUnit.getTimeStamp());
  }

  public CDOPackageUnit.Type getOriginalType()
  {
    return originalType;
  }

  public void setOriginalType(CDOPackageUnit.Type originalType)
  {
    this.originalType = originalType;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public InternalCDOPackageInfo[] getPackageInfos()
  {
    return packageInfos;
  }

  public void setPackageInfos(InternalCDOPackageInfo[] packageInfos)
  {
    this.packageInfos = packageInfos;
  }

  public byte[] getEPackageBlob()
  {
    return ePackageBlob;
  }

  public void setEPackageBlob(byte[] ePackageBlob)
  {
    this.ePackageBlob = ePackageBlob;
  }

  public InternalCDOPackageUnit createCDOPackageUnit(InternalCDOPackageRegistry packageRegistry)
  {
    InternalCDOPackageUnit packageUnit = (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
    packageUnit.setOriginalType(originalType);
    packageUnit.setTimeStamp(timeStamp);
    packageUnit.setPackageRegistry(packageRegistry);
    packageUnit.setPackageInfos(packageInfos);
    for (CDOPackageInfo packageInfo : packageInfos)
    {
      ((InternalCDOPackageInfo)packageInfo).setPackageUnit(packageUnit);
    }

    return packageUnit;
  }

  public String getNsUri()
  {
    return getPackageInfos()[0].getPackageURI();
  }

  public void setNsUri(String nsUri)
  {
    // ignore
  }
}
