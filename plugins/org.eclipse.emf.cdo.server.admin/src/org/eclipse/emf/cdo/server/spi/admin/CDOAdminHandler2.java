/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.spi.admin;

import org.eclipse.emf.cdo.server.IRepository;

/**
 * An optional extension of the {@link CDOAdminHandler} interface that provides
 * additional queries and control functions, such as determination of whether
 * deletion of a particular repository is feasible even to attempt.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 4.2
 */
public interface CDOAdminHandler2 extends CDOAdminHandler
{
  /**
   * Queries whether a given {@code repository} can be deleted.  If it cannot,
   * then it will not be deactivated and the delete operation will fail with
   * an exception back to the client.
   */
  public boolean canDelete(IRepository delegate);

  /**
   * Authenticates the user as a server administrator, if applicable.
   *
   * @throws SecurityException if authentication is required and fails
   */
  public void authenticateAdministrator() throws SecurityException;
}
