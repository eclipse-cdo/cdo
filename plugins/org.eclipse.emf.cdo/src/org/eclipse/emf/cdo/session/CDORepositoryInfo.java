/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonRepository;

/**
 * Describes a model repository a {@link CDOSession session} is connected to.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORepositoryInfo extends CDOCommonRepository
{
  /**
   * Returns the session.
   *
   * @since 4.2
   */
  public CDOSession getSession();

  /**
   * Returns the approximate current time of this repository by optionally refreshing the approximation from the server.
   *
   * @see CDOCommonRepository#getTimeStamp()
   */
  public long getTimeStamp(boolean forceRefresh);
}
