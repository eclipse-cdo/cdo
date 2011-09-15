/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common;

import org.eclipse.net4j.buddies.common.IAccount;
import org.eclipse.net4j.util.ObjectUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Account implements IAccount, Serializable
{
  private static final long serialVersionUID = 1L;

  private String userID;

  private transient String password;

  private Map<String, String> properties = new HashMap<String, String>();

  private long timeStamp;

  protected Account()
  {
  }

  public Account(String userID, String password)
  {
    this.userID = userID;
    this.password = password;
  }

  public String getUserID()
  {
    return userID;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public boolean authenticate(String password)
  {
    return ObjectUtil.equals(password, this.password);
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public void touch()
  {
    timeStamp = System.currentTimeMillis();
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }
}
