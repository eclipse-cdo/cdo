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
import org.eclipse.emf.cdo.protocol.CDOIDRange;

import org.eclipse.net4j.util.ImplementationError;

/**
 * @author Eike Stepper
 */
public class RepositoryPackageManager extends CDOPackageManagerImpl
{
  private Repository repository;

  public RepositoryPackageManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
  }

  @Override
  public void addPackage(CDOPackageImpl cdoPackage)
  {
    CDOIDRange metaIDRange = cdoPackage.getMetaIDRange();
    if (metaIDRange != null && metaIDRange.isTemporary())
    {
      CDOIDRange newRange = repository.getMetaIDRange(metaIDRange.getCount());
      cdoPackage.setMetaIDRange(newRange);
    }

    repository.getStore().addPackage(this, cdoPackage);
  }

  public void addPackageToCache(CDOPackageImpl cdoPackage)
  {
    super.addPackage(cdoPackage);
  }

  @Override
  protected void resolve(CDOPackageImpl cdoPackage)
  {
    repository.getStore().loadPackage(cdoPackage);
  }

  @Override
  protected String provideEcore(CDOPackageImpl cdoPackage)
  {
    throw new ImplementationError("No generated model on server side");
  }
}
