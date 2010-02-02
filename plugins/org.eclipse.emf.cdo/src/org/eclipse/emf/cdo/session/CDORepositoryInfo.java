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

import org.eclipse.emf.cdo.common.CDOTimeProvider;

/**
 * Describes a model repository a {@link CDOSession session} is connected to.
 * 
 * @author Eike Stepper
 * @see CDOSession#getRepositoryInfo()
 * @since 3.0
 */
public interface CDORepositoryInfo extends CDOTimeProvider
{
  /**
   * Returns the name of this repository.
   * 
   * @see IRepository#getName()
   */
  public String getName();

  /**
   * Returns the UUID of this repository.
   * 
   * @see IRepository#getUUID()
   */
  public String getUUID();

  /**
   * Returns the creation time of this repository.
   * 
   * @see IRepository#getCreationTime()
   */
  public long getCreationTime();

  /**
   * Returns the approximate current time of this repository.
   * <p>
   * Same as calling <code>getCurrentTime(false)</code>.
   * 
   * @see #getTimeStamp(boolean)
   */
  public long getTimeStamp();

  /**
   * Returns the approximate current time of this repository by optionally refreshing the approximation from the server.
   */
  public long getTimeStamp(boolean forceRefresh);

  /**
   * Returns <code>true</code> if this repository supports auditing, <code>false</code> otherwise.
   * 
   * @see IRepository#isSupportingAudits()
   */
  public boolean isSupportingAudits();

  /**
   * Returns <code>true</code> if this repository supports auditing, <code>false</code> otherwise.
   * 
   * @see IRepository#isSupportingAudits()
   */
  public boolean isSupportingBranches();
}
