/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
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
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.LoadPackageRequest;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOSessionPackageManagerImpl extends CDOPackageManagerImpl implements CDOSessionPackageManager
{
  private CDOSessionImpl session;

  /**
   * For optimization only. Instead of doing 3 lookups we are doing only one.
   * <p>
   * We could apply the same strategy for CDOClass and CDOPackage, because this is an optimization it will be good to do
   * it only if it proof to make a difference. CDOPackage doesn't need to do it since we will do one lookup anyway...
   * otherwise we need to proof it is more efficient.
   * <p>
   * TODO Should we have a cache for CDOClass(to save 1 lookup), CDOPackage (doesn'T save any lookup) ? TODO A reverse
   * lookup cache is it worth it ?
   */
  private Map<EStructuralFeature, CDOFeature> featureCache = new ConcurrentHashMap<EStructuralFeature, CDOFeature>();

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

  /**
   * TODO Simon: If we enhance all these convert methods like getCDOFeature I suggest to do it directly in convert and
   * remove the corresdonding static methods from ModelUtil. Then always call through the interface.
   * 
   * @since 2.0
   */
  public CDOFeature getCDOFeature(EStructuralFeature eFeature)
  {
    // Do not synchronized since we don't mind putting the same CDOFeeature twice in the Map.
    CDOFeature feature = featureCache.get(eFeature);
    if (feature == null)
    {
      feature = ModelUtil.getCDOFeature(eFeature, this);
      featureCache.put(eFeature, feature);
    }

    return feature;
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

  /**
   * @since 2.0
   */
  public void loadPackage(CDOPackage cdoPackage)
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
      new LoadPackageRequest(session.getProtocol(), cdoPackage, false).send();
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

  /**
   * @since 2.0
   */
  public void loadPackageEcore(CDOPackage cdoPackage)
  {
    try
    {
      String ecore = new LoadPackageRequest(session.getProtocol(), cdoPackage, true).send();
      ((InternalCDOPackage)cdoPackage).setEcore(ecore);
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
}
