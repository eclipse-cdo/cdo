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
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

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
      storeTransaction.execute(new AddPackageOperation(cdoPackage));
    }
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
    throw new ImplementationError("No generated model on server side");
  }

  /**
   * @author Eike Stepper
   */
  private final class AddPackageOperation implements ITransactionalOperation<IStoreWriter>
  {
    private CDOPackageImpl cdoPackage;

    private AddPackageOperation(CDOPackageImpl cdoPackage)
    {
      this.cdoPackage = cdoPackage;
    }

    public void phase1(IStoreWriter storeWriter) throws Exception
    {
      storeWriter.writePackage(cdoPackage);
    }

    public void phase2(IStoreWriter storeWriter)
    {
      addPackage(cdoPackage);
    }

    public void undoPhase1(IStoreWriter storeWriter)
    {
    }
  }
}
