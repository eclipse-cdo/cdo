/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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
public interface IUserManager
{
  public void addUser(String userID, char[] password);

  public void removeUser(String userID);

  /**
   * @since 2.0
   */
  public byte[] encrypt(String userID, byte[] data, String algorithmName, byte[] salt, int count) throws SecurityException;
}
