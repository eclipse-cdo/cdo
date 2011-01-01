/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.hibernate.Hibernate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;

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

  private byte[] ePackageByteArray;

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

  public byte[] getEPackageByteArray()
  {
    return ePackageByteArray;
  }

  public void setEPackageByteArray(byte[] ePackageByteArray)
  {
    this.ePackageByteArray = ePackageByteArray;
  }

  public void setEPackageBlob(Blob ePackageBlob)
  {
    ePackageByteArray = toByteArray(ePackageBlob);
  }

  private byte[] toByteArray(Blob blob)
  {
    try
    {
      final InputStream is = blob.getBinaryStream();
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int dataSize;
      final byte[] buffer = new byte[4000];

      try
      {
        while ((dataSize = is.read(buffer)) != -1)
        {
          bos.write(buffer, 0, dataSize);
        }
      }
      finally
      {
        IOUtil.close(is);
      }

      return bos.toByteArray();
    }
    catch (Exception e)
    {
      throw WrappedException.wrap(e);
    }
  }

  public Blob getEPackageBlob()
  {
    return Hibernate.createBlob(getEPackageByteArray());
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
