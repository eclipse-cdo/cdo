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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageManagerImpl;
import org.eclipse.emf.cdo.protocol.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.protocol.model.CDOModelUtil;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.StoreUtil;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class PackageManager extends CDOPackageManagerImpl implements IPackageManager
{
  private Repository repository;

  public PackageManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return repository.getStore().getCDOIDObjectFactory();
  }

  @Override
  protected void resolve(CDOPackageImpl cdoPackage)
  {
    if (!cdoPackage.isSystem())
    {
      IStoreReader storeReader = StoreUtil.getReader();
      storeReader.readPackage(cdoPackage);
    }
  }

  @Override
  protected String provideEcore(CDOPackageImpl cdoPackage)
  {
    // No generated model on server side
    return null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IStoreReader storeReader = null;

    try
    {
      storeReader = repository.getStore().getReader(null);
      Collection<CDOPackageInfo> packageInfos = storeReader.readPackageInfos();
      for (CDOPackageInfo info : packageInfos)
      {
        addPackage(CDOModelUtil.createProxyPackage(this, info.getPackageURI(), info.isDynamic(), info.getMetaIDRange()));
      }
    }
    finally
    {
      if (storeReader != null)
      {
        storeReader.release();
      }
    }
  }
}
