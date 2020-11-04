/*
 * Copyright (c) 2012, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.Arrays;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public class UserManagerAuthenticator extends Lifecycle implements IAuthenticator
{
  public static final int DEFAULT_TOKEN_LENGTH = 1024;

  @ExcludeFromDump
  private String encryptionAlgorithmName = SecurityUtil.PBE_WITH_MD5_AND_DES;

  @ExcludeFromDump
  private byte[] encryptionSaltBytes = SecurityUtil.DEFAULT_SALT;

  @ExcludeFromDump
  private int encryptionIterationCount = SecurityUtil.DEFAULT_ITERATION_COUNT;

  private int tokenLength = DEFAULT_TOKEN_LENGTH;

  private IRandomizer randomizer;

  private IUserManager userManager;

  public UserManagerAuthenticator()
  {
  }

  public String getEncryptionAlgorithmName()
  {
    return encryptionAlgorithmName;
  }

  public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
  {
    checkInactive();
    this.encryptionAlgorithmName = encryptionAlgorithmName;
  }

  public byte[] getEncryptionSaltBytes()
  {
    return encryptionSaltBytes;
  }

  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
  {
    checkInactive();
    this.encryptionSaltBytes = encryptionSaltBytes;
  }

  public int getEncryptionIterationCount()
  {
    return encryptionIterationCount;
  }

  public void setEncryptionIterationCount(int encryptionIterationCount)
  {
    checkInactive();
    this.encryptionIterationCount = encryptionIterationCount;
  }

  public int getTokenLength()
  {
    return tokenLength;
  }

  public void setTokenLength(int tokenLength)
  {
    checkInactive();
    this.tokenLength = tokenLength;
  }

  public IRandomizer getRandomizer()
  {
    return randomizer;
  }

  public void setRandomizer(IRandomizer randomizer)
  {
    checkInactive();
    this.randomizer = randomizer;
  }

  public IUserManager getUserManager()
  {
    return userManager;
  }

  public void setUserManager(IUserManager userManager)
  {
    checkInactive();
    this.userManager = userManager;
  }

  @Override
  public void authenticate(String userID, char[] password) throws SecurityException
  {
    try
    {
      byte[] randomToken = createRandomToken();
      byte[] cryptedTokenClient = SecurityUtil.pbeEncrypt(randomToken, password, encryptionAlgorithmName, encryptionSaltBytes, encryptionIterationCount);

      byte[] cryptedTokenServer = userManager.encrypt(userID, randomToken, encryptionAlgorithmName, encryptionSaltBytes, encryptionIterationCount);

      if (!Arrays.equals(cryptedTokenClient, cryptedTokenServer))
      {
        throw new SecurityException("Access denied"); //$NON-NLS-1$
      }
    }
    catch (SecurityException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      Throwable cause = ex.getCause();
      if (cause instanceof SecurityException)
      {
        throw (SecurityException)cause;
      }

      throw new SecurityException(ex);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(userManager, "userManager");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (randomizer == null)
    {
      randomizer = new Randomizer();
    }

    LifecycleUtil.activate(randomizer);
  }

  protected byte[] createRandomToken()
  {
    byte[] token = new byte[tokenLength];
    randomizer.nextBytes(token);
    return token;
  }
}
