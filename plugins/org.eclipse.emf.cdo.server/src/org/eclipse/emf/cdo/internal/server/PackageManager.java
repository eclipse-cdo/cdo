/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class PackageManager extends CDOPackageManagerImpl implements IPackageManager
{
  private IRepository repository;

  /**
   * @since 2.0
   */
  public PackageManager()
  {
  }

  /**
   * @since 2.0
   */
  public IRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 2.0
   */
  public void setRepository(IRepository repository)
  {
    this.repository = repository;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return repository.getStore().getCDOIDObjectFactory();
  }

  /**
   * @since 2.0
   */
  public void loadPackage(CDOPackage cdoPackage)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.readPackage(cdoPackage);
  }

  /**
   * @since 2.0
   */
  public void loadPackageEcore(CDOPackage cdoPackage)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    accessor.readPackageEcore(cdoPackage);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IStoreAccessor accessor = null;

    try
    {
      accessor = repository.getStore().getReader(null);
      Collection<CDOPackageInfo> packageInfos = accessor.readPackageInfos();
      for (CDOPackageInfo info : packageInfos)
      {
        addPackage(CDOModelUtil.createProxyPackage(this, info.getPackageURI(), info.isDynamic(), info.getMetaIDRange(),
            info.getParentURI()));
      }
    }
    finally
    {
      if (accessor != null)
      {
        accessor.release();
      }
    }
  }
}
