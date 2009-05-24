package org.eclipse.emf.cdo.common.protocol;

import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOAuthenticator
{
  public String getEncryptionAlgorithmName();

  public void setEncryptionAlgorithmName(String encryptionAlgorithmName);

  public byte[] getEncryptionSaltBytes();

  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes);

  public int getEncryptionIterationCount();

  public void setEncryptionIterationCount(int encryptionIterationCount);

  public IPasswordCredentialsProvider getCredentialsProvider();

  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider);

  public CDOAuthenticationResult authenticate(byte[] randomToken);
}
