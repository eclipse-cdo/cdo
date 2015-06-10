/*
 * Copyright (c) 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
