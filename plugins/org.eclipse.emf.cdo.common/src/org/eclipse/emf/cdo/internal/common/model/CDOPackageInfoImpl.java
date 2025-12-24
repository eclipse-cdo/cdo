/*
 * Copyright (c) 2009-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOPackageInfoImpl implements InternalCDOPackageInfo
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOPackageInfoImpl.class);

  private InternalCDOPackageUnit packageUnit;

  private String packageURI;

  private String parentURI;

  private EPackage ePackage;

  public CDOPackageInfoImpl()
  {
  }

  @Override
  public InternalCDOPackageUnit getPackageUnit()
  {
    return packageUnit;
  }

  @Override
  public void setPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    this.packageUnit = packageUnit;
  }

  @Override
  public String getPackageURI()
  {
    return packageURI;
  }

  @Override
  public void setPackageURI(String packageURI)
  {
    this.packageURI = packageURI;
  }

  @Override
  public String getParentURI()
  {
    return parentURI;
  }

  @Override
  public void setParentURI(String parentURI)
  {
    this.parentURI = parentURI;
  }

  @Override
  public void write(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0}", this); //$NON-NLS-1$
    }

    out.writeCDOPackageURI(packageURI);
    out.writeCDOPackageURI(parentURI);
  }

  @Override
  public void read(CDODataInput in) throws IOException
  {
    packageURI = in.readCDOPackageURI();
    parentURI = in.readCDOPackageURI();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read {0}", this); //$NON-NLS-1$
    }
  }

  @Override
  public InternalCDOPackageInfo copy()
  {
    InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
    packageInfo.setPackageURI(getPackageURI());
    packageInfo.setParentURI(getParentURI());
    packageInfo.setEPackage(getEPackage());
    return packageInfo;
  }

  @Override
  public EFactory getEFactory()
  {
    return getEPackage().getEFactoryInstance();
  }

  @Override
  public EPackage getEPackage()
  {
    return getEPackage(true);
  }

  @Override
  public EPackage getEPackage(boolean loadOnDemand)
  {
    if (ePackage == null && loadOnDemand)
    {
      packageUnit.load(true);
    }

    return ePackage;
  }

  @Override
  public void setEPackage(EPackage ePackage)
  {
    this.ePackage = ePackage;
  }

  @Override
  public boolean isCorePackage()
  {
    return CDOModelUtil.isCorePackage(getEPackage());
  }

  @Override
  public boolean isResourcePackage()
  {
    return CDOModelUtil.isResourcePackage(getEPackage());
  }

  @Override
  public boolean isTypePackage()
  {
    return CDOModelUtil.isTypesPackage(getEPackage());
  }

  @Override
  public boolean isSystemPackage()
  {
    return CDOModelUtil.isSystemPackage(getEPackage());
  }

  @Override
  public int compareTo(CDOPackageInfo o)
  {
    return getPackageURI().compareTo(o.getPackageURI());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOPackageInfo[packageURI={0}, parentURI={1}]", packageURI, parentURI); //$NON-NLS-1$
  }

  /**
   * @deprecated As of 4.2 CDOPackageInfos are no longer mapped through Adapters.
   * @see InternalCDOPackageRegistry#registerPackageInfo(EPackage, InternalCDOPackageInfo)
   */
  @Override
  @Deprecated
  public void notifyChanged(Notification notification)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.2 CDOPackageInfos are no longer mapped through Adapters.
   * @see InternalCDOPackageRegistry#registerPackageInfo(EPackage, InternalCDOPackageInfo)
   */
  @Override
  @Deprecated
  public Notifier getTarget()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.2 CDOPackageInfos are no longer mapped through Adapters.
   * @see InternalCDOPackageRegistry#registerPackageInfo(EPackage, InternalCDOPackageInfo)
   */
  @Override
  @Deprecated
  public void setTarget(Notifier newTarget)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.2 CDOPackageInfos are no longer mapped through Adapters.
   * @see InternalCDOPackageRegistry#registerPackageInfo(EPackage, InternalCDOPackageInfo)
   */
  @Override
  @Deprecated
  public void unsetTarget(Notifier oldTarget)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.2 CDOPackageInfos are no longer mapped through Adapters.
   * @see InternalCDOPackageRegistry#registerPackageInfo(EPackage, InternalCDOPackageInfo)
   */
  @Override
  @Deprecated
  public boolean isAdapterForType(Object type)
  {
    throw new UnsupportedOperationException();
  }
}
