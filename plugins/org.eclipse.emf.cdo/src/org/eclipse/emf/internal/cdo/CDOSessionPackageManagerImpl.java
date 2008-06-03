/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSessionPackageManager;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.util.EMFUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.LoadPackageRequest;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CDOSessionPackageManagerImpl extends CDOPackageManagerImpl implements CDOSessionPackageManager
{
  private CDOSessionImpl session;

  public CDOSessionPackageManagerImpl(CDOSessionImpl session)
  {
    this.session = session;
    ModelUtil.addModelInfos(this);
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return session;
  }

  public CDOPackage convert(EPackage ePackage)
  {
    return ModelUtil.getCDOPackage(ePackage, this);
  }

  public CDOClass convert(EClass eClass)
  {
    return ModelUtil.getCDOClass(eClass, this);
  }

  public CDOFeature convert(EStructuralFeature eFeature)
  {
    return ModelUtil.getCDOFeature(eFeature, this);
  }

  public EPackage convert(CDOPackage cdoPackage)
  {
    return ModelUtil.getEPackage(cdoPackage, session.getPackageRegistry());
  }

  public EClass convert(CDOClass cdoClass)
  {
    return ModelUtil.getEClass(cdoClass, session.getPackageRegistry());
  }

  public EStructuralFeature convert(CDOFeature cdoFeature)
  {
    return ModelUtil.getEFeature(cdoFeature, session.getPackageRegistry());
  }

  public void addPackageProxies(Collection<CDOPackageInfo> packageInfos)
  {
    for (CDOPackageInfo info : packageInfos)
    {
      String packageURI = info.getPackageURI();
      boolean dynamic = info.isDynamic();
      CDOIDMetaRange metaIDRange = info.getMetaIDRange();
      String parentURI = info.getParentURI();

      CDOPackage proxy = CDOModelUtil.createProxyPackage(this, packageURI, dynamic, metaIDRange, parentURI);
      addPackage(proxy);
      session.getPackageRegistry().putPackageDescriptor(proxy);
    }
  }

  @Override
  protected void resolve(CDOPackage cdoPackage)
  {
    if (!cdoPackage.isDynamic())
    {
      String uri = cdoPackage.getPackageURI();
      EPackage ePackage = session.getPackageRegistry().getEPackage(uri);
      if (ePackage != null)
      {
        ModelUtil.initializeCDOPackage(ePackage, cdoPackage);
        return;
      }
    }

    try
    {
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      LoadPackageRequest request = new LoadPackageRequest(session.getChannel(), cdoPackage);
      failOverStrategy.send(request);

      if (!cdoPackage.isDynamic())
      {
        OM.LOG.info("Dynamic package created for " + cdoPackage.getPackageURI());
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  @Override
  protected String provideEcore(CDOPackage cdoPackage)
  {
    EPackage ePackage = ModelUtil.getEPackage(cdoPackage, session.getPackageRegistry());
    return EMFUtil.ePackageToString(ePackage, session.getPackageRegistry());
  }
}
