/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.embedded;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.embedded.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.internal.cdo.session.CDOSessionConfigurationImpl;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class EmbeddedClientSessionConfiguration extends CDOSessionConfigurationImpl implements CDOSessionConfiguration
{
  private InternalRepository repository;

  public EmbeddedClientSessionConfiguration()
  {
    setRevisionCache(CDORevisionCache.NOOP);
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    checkNotOpen();
    this.repository = (InternalRepository)repository;
  }

  @Override
  public CDOPackageRegistry getPackageRegistry()
  {
    return repository.getPackageRegistry();
  }

  @Override
  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    throw new UnsupportedOperationException("The package registry of the repository must be used");
  }

  @Override
  public org.eclipse.emf.cdo.server.embedded.CDOSession openSession()
  {
    return (org.eclipse.emf.cdo.server.embedded.CDOSession)super.openSession();
  }

  @Override
  protected InternalCDOSession createSession()
  {
    if (isActivateOnOpen())
    {
      CheckUtil.checkState(repository, "Specify a repository"); //$NON-NLS-1$
    }

    return new EmbeddedClientSession(repository);
  }
}
