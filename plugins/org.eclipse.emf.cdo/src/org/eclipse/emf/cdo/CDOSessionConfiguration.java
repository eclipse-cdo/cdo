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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.emf.internal.cdo.CDORevisionManagerImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionConfiguration
{
  /**
   * @see CDOSession#getConnector()
   */
  public IConnector getConnector();

  /**
   * @see CDOSession#getConnector()
   */
  public void setConnector(IConnector connector);

  /**
   * @see CDOSession#getRepositoryName()
   */
  public String getRepositoryName();

  /**
   * @see CDOSession#getRepositoryName()
   */
  public void setRepositoryName(String repositoryName);

  /**
   * @see CDOSession#getFailOverStrategy()
   */
  public IFailOverStrategy getFailOverStrategy();

  /**
   * The fail-over strategy must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getFailOverStrategy()
   */
  public void setFailOverStrategy(IFailOverStrategy failOverStrategy);

  /**
   * @see CDOSession#getPackageRegistry()
   * @since 2.0
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * The package registry must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getPackageRegistry()
   * @since 2.0
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry);

  /**
   * The package registry must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getPackageRegistry()
   * @since 2.0
   */
  public void setEagerPackageRegistry();

  /**
   * The package registry must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getPackageRegistry()
   * @since 2.0
   */
  public void setLazyPackageRegistry();

  /**
   * @see CDORevisionManagerImpl#getCache()
   * @since 2.0
   */
  public CDORevisionCache getRevisionCache();

  /**
   * The revision cache must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDORevisionManagerImpl#setCache(CDORevisionCache)
   * @since 2.0
   */
  public void setRevisionCache(CDORevisionCache revisionCache);

  /**
   * Opens the session for this configuration. Once the session is openend this method always returns the same session
   * instance. Therefore it is impossible to change this configuration while the session is open.
   */
  public CDOSession openSession();

  /**
   * Returns <code>true</code> if the session for this configuration is currently open, <code>false</code> otherwise.
   */
  public boolean isSessionOpen();
}
