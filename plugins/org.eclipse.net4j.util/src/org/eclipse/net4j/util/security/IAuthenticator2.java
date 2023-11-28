/*
 * Copyright (c) 2013, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.net4j.util.security;

/**
 * Extension interface for authenticators that can update user credentials in addition to authenticating them.
 *
 * @since 3.4
 * @author Christian W. Damus (CEA LIST)
 */
public interface IAuthenticator2 extends IAuthenticator, AdministrationPredicate
{
  /**
   * Updates the password <em>stored</em> for the user identified by {@code userID}.  The {@code oldPassword} is authenticated
   * {@linkplain IAuthenticator#authenticate(String, char[]) as per usual} and is replaced by the {@code newPassword} only
   * (and atomically) on success.
   *
   * @param userID the ID of the user whose password is to be updated
   * @param oldPassword the user's current password attempt to verify against the <em>stored</em> password
   * @param newPassword the new password to replace the {@code oldPassword}
   *
   * @throws SecurityException on any failure to authenticate the {@code oldPassword} or validate and/or set the {@code newPassword}
   */
  public void updatePassword(String userID, char[] oldPassword, char[] newPassword);

  /**
   * Performs an administrative resets of the password <em>stored</em> for the user identified by {@code userID}.
   * The {@code adminID} and {@code adminPassword} must {@linkplain IAuthenticator#authenticate(String, char[]) authenticate}
   * to permit the {@code userID}'s password to be set to the {@code newPassword}.
   *
   * @param adminID the ID of the administrator requesting the reset
   * @param adminPassword the administrator's password
   * @param userID the ID of the user whose password is to be reset
   * @param newPassword the new password to replace the user's old password
   *
   * @throws SecurityException on any failure to authenticate the {@code oldPassword} or validate and/or set the {@code newPassword}
   */
  public void resetPassword(String adminID, char[] adminPassword, String userID, char[] newPassword);

  /**
   * Queries whether a given user has administrative privileges.
   *
   * @param userID an user ID, which may or may not exist
   *
   * @return whether the userID exists and has administrative privileges
   */
  @Override
  public boolean isAdministrator(String userID);
}
