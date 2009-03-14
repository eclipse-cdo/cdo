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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionConfigurationImpl implements CDOSessionConfiguration
{
  private InternalCDOSession session;

  private String repositoryName;

  private CDOPackageRegistry packageRegistry;

  private CDORevisionCache revisionCache;

  private boolean activateOnOpen = true;

  public CDOSessionConfigurationImpl()
  {
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

  public boolean isSessionOpen()
  {
    if (session == null)
    {
      return false;
    }

    if (!session.isClosed())
    {
      return true;
    }

    session = null;
    return false;
  }

  /**
   * @since 2.0
   */
  public CDOSession openSession()
  {
    if (!isSessionOpen())
    {
      session = createSession();
      session.setRepositoryName(repositoryName);
      session.setPackageRegistry(packageRegistry);
      session.getRevisionManager().setCache(revisionCache);

      if (activateOnOpen)
      {
        session.activate();
      }
    }

    return session;
  }

  protected void checkNotOpen()
  {
    if (isSessionOpen())
    {
      throw new IllegalStateException("Session is already open");
    }
  }

  protected abstract InternalCDOSession createSession();
}
