/*
 * Copyright (c) 2013, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
 * Default implementation of the {@link IPasswordCredentialsUpdate} interface.
 *
 * @since 3.4
 * @author Christian W. Damus (CEA LIST)
 */
public class PasswordCredentialsUpdate extends PasswordCredentials implements IPasswordCredentialsUpdate
{
  private char[] newPassword;

  public PasswordCredentialsUpdate(String userID, char[] oldPassword, char[] newPassword)
  {
    super(userID, oldPassword);
    this.newPassword = newPassword;
  }

  public PasswordCredentialsUpdate(String userID, String password, String newPassword)
  {
    this(userID, SecurityUtil.toCharArray(password), SecurityUtil.toCharArray(newPassword));
  }

  public PasswordCredentialsUpdate(String userID)
  {
    super(userID);
  }

  @Override
  public char[] getNewPassword()
  {
    return newPassword;
  }

  public void setNewPassword(char[] newPassword)
  {
    this.newPassword = newPassword;
  }
}
