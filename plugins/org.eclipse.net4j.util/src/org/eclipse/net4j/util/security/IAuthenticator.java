/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

/**
 * Authenticates users.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public interface IAuthenticator
{
  /**
   * Authenticates the user with the passed <code>userID</code> by checking whether the supplied <code>password</code>
   * matches the password <i>stored</i> for this user.
   * <p>
   * The implementation is required to throw a {@link SecurityException} if the passwords do <b>not</b> match.
   */
  public void authenticate(String userID, char[] password) throws SecurityException;
}
