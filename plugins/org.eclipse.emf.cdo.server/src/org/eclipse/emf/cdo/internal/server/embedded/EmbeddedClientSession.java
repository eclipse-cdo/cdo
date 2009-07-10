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

import org.eclipse.emf.cdo.server.embedded.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * @author Eike Stepper
 */
public class EmbeddedClientSession extends CDOSessionImpl implements CDOSession
{
  private InternalRepository repository;

  public EmbeddedClientSession(InternalRepository repository)
  {
    this.repository = repository;
    setPackageRegistry(repository.getPackageRegistry(false));
    setRevisionManager(repository.getRevisionManager());
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  protected CDOSessionProtocol createSessionProtocol()
  {
    return new EmbeddedClientSessionProtocol(this);
  }

  @Override
  protected void activatePackageRegistry()
  {
    // Do nothing
  }

  @Override
  protected void activateRevisionManager()
  {
    // Do nothing
  }

  @Override
  protected void deactivatePackageRegistry()
  {
    // Do nothing
  }

  @Override
  protected void deactivateRevisionManager()
  {
    // Do nothing
  }
}
