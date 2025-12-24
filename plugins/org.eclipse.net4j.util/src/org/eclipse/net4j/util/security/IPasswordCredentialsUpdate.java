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
package org.eclipse.net4j.util.security;

/**
 * Password credentials with an update (new password) in addition to the usual
 * user ID and password for authentication of the password change.
 *
 * @since 3.4
 * @author Christian W. Damus (CEA LIST)
 */
public interface IPasswordCredentialsUpdate extends IPasswordCredentials
{
  /**
   * Queries the new password to be set for the {@linkplain IUserAware#getUserID() user}.
   * It is the responsibility of the provider of an update to verify that this is actually
   * the new password intended by the user.
   */
  public char[] getNewPassword();
}
