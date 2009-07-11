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
  public org.eclipse.emf.cdo.server.embedded.CDOSession openSession()
  {
    return (org.eclipse.emf.cdo.server.embedded.CDOSession)super.openSession();
  }

  public InternalCDOSession createSession()
  {
    if (isActivateOnOpen())
    {
      CheckUtil.checkState(repository, "Specify a repository"); //$NON-NLS-1$
    }

    return new EmbeddedClientSession(this);
  }

  @Override
  public void activateSession(InternalCDOSession session) throws Exception
  {
    super.activateSession(session);
    session.setSessionProtocol(new EmbeddedClientSessionProtocol((EmbeddedClientSession)session));
    session.setRepositoryInfo(new RepositoryInfo());
  }

  @Override
  public void deactivateSession(InternalCDOSession session) throws Exception
  {
    super.deactivateSession(session);
  }

  /**
   * @author Eike Stepper
   */
  protected class RepositoryInfo implements org.eclipse.emf.cdo.session.CDORepositoryInfo
  {
    public RepositoryInfo()
    {
    }

    public long getCreationTime()
    {
      return repository.getCreationTime();
    }

    public long getCurrentTime()
    {
      return getCurrentTime(false);
    }

    public long getCurrentTime(boolean forceRefresh)
    {
      return System.currentTimeMillis();
    }

    public String getName()
    {
      return repository.getName();
    }

    public String getUUID()
    {
      return repository.getUUID();
    }

    public boolean isSupportingAudits()
    {
      return repository.isSupportingAudits();
    }
  }
}
