/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public interface IUserManagement extends AdministrationPredicate
{
  public void addUser(String userID, char[] password);

  public void removeUser(String userID);

  public void setPassword(String userID, char[] newPassword);

  public void setAdministrator(String userID, boolean administrator);

  /**
   * @author Eike Stepper
   * @since 3.27
   */
  public interface Attributed extends IUserManagement
  {
    public void setAttribute(String userID, String attribute, String value);
  }
}
