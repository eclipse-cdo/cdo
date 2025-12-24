/*
 * Copyright (c) 2008, 2011-2013, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
 * @author Eike Stepper
 */
public class PasswordCredentials extends Credentials implements IPasswordCredentials
{
  private char[] password;

  public PasswordCredentials(String userID, char[] password)
  {
    super(userID);
    this.password = password;
  }

  /**
   * @since 3.4
   */
  public PasswordCredentials(String userID, String password)
  {
    this(userID, SecurityUtil.toCharArray(password));
  }

  /**
   * @since 3.4
   */
  public PasswordCredentials(String userID)
  {
    super(userID);
  }

  @Override
  public char[] getPassword()
  {
    return password;
  }

  public void setPassword(char[] password)
  {
    this.password = password;
  }
}
