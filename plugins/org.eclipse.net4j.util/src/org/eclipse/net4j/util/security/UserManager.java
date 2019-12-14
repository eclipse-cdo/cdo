/*
 * Copyright (c) 2008-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class UserManager extends Lifecycle implements IUserManager, IAuthenticator
{
  @ExcludeFromDump
  protected transient Map<String, char[]> users = new HashMap<>();

  public UserManager()
  {
  }

  @Override
  public synchronized void addUser(String userID, char[] password)
  {
    users.put(userID, password);
    save(users);
  }

  @Override
  public synchronized void removeUser(String userID)
  {
    if (users.remove(userID) != null)
    {
      save(users);
    }
  }

  /**
   * @since 3.3
   */
  public synchronized char[] getPassword(String userID)
  {
    return users.get(userID);
  }

  /**
   * @since 3.3
   */
  @Override
  public void authenticate(String userID, char[] password)
  {
    char[] userPassword;
    synchronized (this)
    {
      userPassword = users.get(userID);
    }

    if (userPassword == null)
    {
      throw new SecurityException("No such user: " + userID); //$NON-NLS-1$
    }

    if (!Arrays.equals(userPassword, password))
    {
      throw new SecurityException("Wrong password for user: " + userID); //$NON-NLS-1$
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public byte[] encrypt(String userID, byte[] data, String algorithmName, byte[] salt, int count) throws SecurityException
  {
    char[] password;
    synchronized (this)
    {
      password = users.get(userID);
    }

    if (password == null)
    {
      throw new SecurityException("No such user: " + userID); //$NON-NLS-1$
    }

    try
    {
      return SecurityUtil.encrypt(data, password, algorithmName, salt, count);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new SecurityException(ex);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    load(users);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    users.clear();
    super.doDeactivate();
  }

  protected void load(Map<String, char[]> users) throws IORuntimeException
  {
  }

  protected void save(Map<String, char[]> users) throws IORuntimeException
  {
  }
}
