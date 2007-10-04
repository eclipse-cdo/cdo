/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.security;

import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.SecurityUtil;

/**
 * @author Eike Stepper
 */
public abstract class UserManagerNegotiator extends Negotiator
{
  public static final String DEFAULT_ALGORITHM_NAME = SecurityUtil.PBE_WITH_MD5_AND_DES;

  private String algorithmName = DEFAULT_ALGORITHM_NAME;

  private IUserManager userManager;

  public UserManagerNegotiator(boolean starter)
  {
    super(starter);
  }

  public String getAlgorithmName()
  {
    return algorithmName;
  }

  public void setAlgorithmName(String algorithmName)
  {
    this.algorithmName = algorithmName;
  }

  public IUserManager getUserManager()
  {
    return userManager;
  }

  public void setUserManager(IUserManager userManager)
  {
    this.userManager = userManager;
  }

  protected byte[] encrypt(String userID, byte[] data) throws SecurityException
  {
    return userManager.encrypt(userID, data, algorithmName);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (algorithmName == null)
    {
      throw new IllegalStateException("algorithmName == null");
    }

    if (userManager == null)
    {
      throw new IllegalStateException("userManager == null");
    }
  }
}
