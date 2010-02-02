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

import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.embedded.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
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

  private InternalCDORevisionManager revisionManager;

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

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public void setRevisionManager(CDORevisionManager revisionManager)
  {
    checkNotOpen();
    this.revisionManager = (InternalCDORevisionManager)revisionManager;
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
    EmbeddedClientSessionProtocol protocol = new EmbeddedClientSessionProtocol((EmbeddedClientSession)session);
    session.setSessionProtocol(protocol);
    protocol.activate();
    protocol.openSession(isPassiveUpdateEnabled());

    session.setLastUpdateTime(repository.getLastCommitTimeStamp());
    session.setRepositoryInfo(new RepositoryInfo());

    revisionManager = new CDORevisionManagerImpl();
    revisionManager.setSupportingBranches(session.getRepositoryInfo().isSupportingBranches());
    revisionManager.setCache(CDORevisionCache.NOOP);
    revisionManager.setRevisionLoader(session.getSessionProtocol());
    revisionManager.setRevisionLocker(session);
    revisionManager.activate();
  }

  @Override
  public void deactivateSession(InternalCDOSession session) throws Exception
  {
    revisionManager.deactivate();
    revisionManager = null;
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

    public long getTimeStamp()
    {
      return getTimeStamp(false);
    }

    public long getTimeStamp(boolean forceRefresh)
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

    public boolean isSupportingBranches()
    {
      return repository.isSupportingBranches();
    }
  }
}
