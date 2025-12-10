/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.protocol;

import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;

import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * The front-end of the CDO challenge/response authentication.
 *
 * @author Eike Stepper
 * @since 2.0
 * @deprecated As of 4.2 use {@link IPasswordCredentialsProvider} directly
 */
@Deprecated
public interface CDOAuthenticator
{
  @Deprecated
  public String getEncryptionAlgorithmName();

  @Deprecated
  public void setEncryptionAlgorithmName(String encryptionAlgorithmName);

  @Deprecated
  public byte[] getEncryptionSaltBytes();

  @Deprecated
  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes);

  @Deprecated
  public int getEncryptionIterationCount();

  @Deprecated
  public void setEncryptionIterationCount(int encryptionIterationCount);

  @Deprecated
  public IPasswordCredentialsProvider getCredentialsProvider();

  @Deprecated
  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider);

  /**
   * @since 4.0
   */
  @Deprecated
  public CDOAuthenticationResult authenticate(byte[] randomToken);
}
