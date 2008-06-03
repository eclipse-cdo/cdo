/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class UserManager extends Lifecycle implements IUserManager
{
  // Transient to prevent from logging
  protected transient Map<String, char[]> users = new HashMap<String, char[]>();

  public UserManager()
  {
  }

  public synchronized void addUser(String userID, char[] password)
  {
    users.put(userID, password);
    save(users);
  }

  public synchronized void removeUser(String userID)
  {
    if (users.remove(userID) != null)
    {
      save(users);
    }
  }

  public byte[] encrypt(String userID, byte[] data, String algorithmName) throws SecurityException
  {
    char[] password;
    synchronized (this)
    {
      password = users.get(userID);
    }

    if (password == null)
    {
      throw new SecurityException("No such user: " + userID);
    }

    try
    {
      return SecurityUtil.encrypt(data, password, algorithmName);
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
