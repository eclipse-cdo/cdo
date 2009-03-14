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
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;

import org.eclipse.emf.internal.cdo.session.CDORevisionManagerImpl;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOSessionConfiguration
{
  /**
   * @see CDOSession#getName()
   */
  public String getRepositoryName();

  /**
   * @see CDOSession#getName()
   */
  public void setRepositoryName(String repositoryName);

  /**
   * @see CDOSession#getPackageRegistry()
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * The package registry must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getPackageRegistry()
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry);

  /**
   * @see CDORevisionManagerImpl#getCache()
   */
  public CDORevisionCache getRevisionCache();

  /**
   * The revision cache must be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDORevisionManagerImpl#setCache(CDORevisionCache)
   */
  public void setRevisionCache(CDORevisionCache revisionCache);

  /**
   * Returns <code>true</code> if the session opened by {@link #openSession()} will be automatically activated,
   * <code>false</code> otherwise.
   */
  public boolean isActivateOnOpen();

  /**
   * Specifies whether the session opened by {@link #openSession()} will be automatically activated or not.
   */
  public void setActivateOnOpen(boolean activateOnOpen);

  /**
   * Returns <code>true</code> if the session for this configuration is currently open, <code>false</code> otherwise.
   */
  public boolean isSessionOpen();

  /**
   * Opens the session for this configuration. Once the session is openend this method always returns the same session
   * instance. Therefore it is impossible to change this configuration while the session is open.
   */
  public CDOSession openSession();
}
