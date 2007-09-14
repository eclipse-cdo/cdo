/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

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

  public void addPackages(ITransaction<IStoreWriter> storeTransaction, CDOPackageImpl[] cdoPackages)
  {
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      cdoPackage.setPackageManager(this);
    }

    storeTransaction.execute(new AddPackagesOperation(cdoPackages));
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
    IStoreReader storeReader = repository.getStore().getReader(null);
    Collection<CDOPackageInfo> packageInfos = storeReader.readPackageInfos();
    for (CDOPackageInfo info : packageInfos)
    {
      addPackage(new CDOPackageImpl(this, info.getPackageURI(), info.isDynamic(), info.getMetaIDRange()));
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AddPackagesOperation implements ITransactionalOperation<IStoreWriter>
  {
    private CDOPackageImpl[] cdoPackages;

    private AddPackagesOperation(CDOPackageImpl[] cdoPackages)
    {
      this.cdoPackages = cdoPackages;
    }

    public void phase1(IStoreWriter storeWriter) throws Exception
    {
      storeWriter.writePackages(cdoPackages);
    }

    public void phase2(IStoreWriter storeWriter)
    {
      for (CDOPackageImpl cdoPackage : cdoPackages)
      {
        addPackage(cdoPackage);
      }
    }

    public void undoPhase1(IStoreWriter storeWriter)
    {
    }
  }
}
