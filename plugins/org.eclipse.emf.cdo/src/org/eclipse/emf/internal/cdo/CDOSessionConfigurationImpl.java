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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;

/**
 * @author Eike Stepper
 */
public class CDOSessionConfigurationImpl implements CDOSessionConfiguration
{
  private CDOSessionImpl session;

  private IConnector connector;

  private String repositoryName;

  private IFailOverStrategy failOverStrategy;

  private CDOPackageRegistry packageRegistry;

  private CDORevisionCache revisionCache;

  private boolean activateOnOpen = true;

  public CDOSessionConfigurationImpl()
  {
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    checkNotOpen();
    this.connector = connector;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    checkNotOpen();
    this.repositoryName = repositoryName;
  }

  public IFailOverStrategy getFailOverStrategy()
  {
    return failOverStrategy;
  }

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    checkNotOpen();
    this.failOverStrategy = failOverStrategy;
  }

  /**
   * @since 2.0
   */
  public CDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  /**
   * @since 2.0
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    checkNotOpen();
    this.packageRegistry = packageRegistry;
  }

  /**
   * @since 2.0
   */
  public void setEagerPackageRegistry()
  {
    setPackageRegistry(CDOUtil.createEagerPackageRegistry());
  }

  /**
   * @since 2.0
   */
  public void setLazyPackageRegistry()
  {
    setPackageRegistry(CDOUtil.createLazyPackageRegistry());
  }

  /**
   * @since 2.0
   */
  public CDORevisionCache getRevisionCache()
  {
    return revisionCache;
  }

  /**
   * @since 2.0
   */
  public void setRevisionCache(CDORevisionCache revisionCache)
  {
    checkNotOpen();
    this.revisionCache = revisionCache;
  }

  public boolean isActivateOnOpen()
  {
    return activateOnOpen;
  }

  public void setActivateOnOpen(boolean activateOnOpen)
  {
    checkNotOpen();
    this.activateOnOpen = activateOnOpen;
  }

  public CDOSession openSession()
  {
    if (!isSessionOpen())
    {
      session = new CDOSessionImpl();
      session.setConnector(connector);
      session.setRepositoryName(repositoryName);
      session.setFailOverStrategy(failOverStrategy);
      session.setPackageRegistry(packageRegistry);
      session.getRevisionManager().setCache(revisionCache);

      if (activateOnOpen)
      {
        session.activate();
      }
    }

    return session;
  }

  public boolean isSessionOpen()
  {
    if (session == null)
    {
      return false;
    }

    if (session.isOpen())
    {
      return true;
    }

    session = null;
    return false;
  }

  private void checkNotOpen()
  {
    if (isSessionOpen())
    {
      throw new IllegalStateException("Session is already open");
    }
  }
}
