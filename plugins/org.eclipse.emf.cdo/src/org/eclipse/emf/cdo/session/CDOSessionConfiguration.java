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
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOSessionConfiguration
{
  /**
   * @see CDOSession#getExceptionHandler()
   */
  public CDOSession.ExceptionHandler getExceptionHandler();

  /**
   * A special exception handler can be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getExceptionHandler()
   */
  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler);

  /**
   * @see CDOSession#getPackageRegistry()
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * A special package registry can be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDOSession#getPackageRegistry()
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry);

  /**
   * @see CDORevisionResolver#getCache()
   */
  public CDORevisionCache getRevisionCache();

  /**
   * A special revision cache can be set <b>before</b> the session is opened and can not be changed thereafter.
   * 
   * @see CDORevisionResolver#setCache(CDORevisionCache)
   */
  public void setRevisionCache(CDORevisionCache revisionCache);

  /**
   * Returns the authenticator of this configuration, never <code>null</code>.
   */
  public CDOAuthenticator getAuthenticator();

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
