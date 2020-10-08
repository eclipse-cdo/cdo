/*
 * Copyright (c) 2010-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.SecurityUtil;

/**
 * @author Eike Stepper
 */
@Deprecated
public class CDOAuthenticatorImpl implements org.eclipse.emf.cdo.common.protocol.CDOAuthenticator
{
  private String encryptionAlgorithmName = SecurityUtil.PBE_WITH_MD5_AND_DES;

  private byte[] encryptionSaltBytes = SecurityUtil.DEFAULT_SALT;

  private int encryptionIterationCount = SecurityUtil.DEFAULT_ITERATION_COUNT;

  private IPasswordCredentialsProvider credentialsProvider;

  public CDOAuthenticatorImpl()
  {
  }

  @Override
  public String getEncryptionAlgorithmName()
  {
    return encryptionAlgorithmName;
  }

  @Override
  public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
  {
    this.encryptionAlgorithmName = encryptionAlgorithmName;
  }

  @Override
  public byte[] getEncryptionSaltBytes()
  {
    return encryptionSaltBytes;
  }

  @Override
  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
  {
    this.encryptionSaltBytes = encryptionSaltBytes;
  }

  @Override
  public int getEncryptionIterationCount()
  {
    return encryptionIterationCount;
  }

  @Override
  public void setEncryptionIterationCount(int encryptionIterationCount)
  {
    this.encryptionIterationCount = encryptionIterationCount;
  }

  @Override
  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    return credentialsProvider;
  }

  @Override
  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    this.credentialsProvider = credentialsProvider;
  }

  @Override
  public org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult authenticate(byte[] randomToken)
  {
    if (credentialsProvider == null)
    {
      throw new IllegalStateException("No credentials provider configured"); //$NON-NLS-1$
    }

    IPasswordCredentials credentials = credentialsProvider.getCredentials();
    if (credentials != null)
    {
      String userID = credentials.getUserID();
      byte[] cryptedToken = encryptToken(credentials.getPassword(), randomToken);
      return new org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult(userID, cryptedToken);
    }

    return null;
  }

  protected byte[] encryptToken(char[] password, byte[] token)
  {
    try
    {
      return SecurityUtil.pbeEncrypt(token, password, encryptionAlgorithmName, encryptionSaltBytes, encryptionIterationCount);
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
}
