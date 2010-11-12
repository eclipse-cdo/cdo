package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.SecurityUtil;

/**
 * @author Eike Stepper
 */
public class CDOAuthenticatorImpl implements CDOAuthenticator
{
  private String encryptionAlgorithmName = SecurityUtil.PBE_WITH_MD5_AND_DES;

  private byte[] encryptionSaltBytes = SecurityUtil.DEFAULT_SALT;

  private int encryptionIterationCount = SecurityUtil.DEFAULT_ITERATION_COUNT;

  private IPasswordCredentialsProvider credentialsProvider;

  public CDOAuthenticatorImpl()
  {
  }

  public String getEncryptionAlgorithmName()
  {
    return encryptionAlgorithmName;
  }

  public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
  {
    this.encryptionAlgorithmName = encryptionAlgorithmName;
  }

  public byte[] getEncryptionSaltBytes()
  {
    return encryptionSaltBytes;
  }

  public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
  {
    this.encryptionSaltBytes = encryptionSaltBytes;
  }

  public int getEncryptionIterationCount()
  {
    return encryptionIterationCount;
  }

  public void setEncryptionIterationCount(int encryptionIterationCount)
  {
    this.encryptionIterationCount = encryptionIterationCount;
  }

  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    return credentialsProvider;
  }

  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    this.credentialsProvider = credentialsProvider;
  }

  public CDOAuthenticationResult authenticate(byte[] randomToken)
  {
    if (credentialsProvider == null)
    {
      throw new IllegalStateException("No credentials provider configured"); //$NON-NLS-1$
    }

    IPasswordCredentials credentials = credentialsProvider.getCredentials();
    String userID = credentials.getUserID();
    byte[] cryptedToken = encryptToken(credentials.getPassword(), randomToken);
    return new CDOAuthenticationResult(userID, cryptedToken);
  }

  protected byte[] encryptToken(char[] password, byte[] token)
  {
    try
    {
      return SecurityUtil.encrypt(token, password, encryptionAlgorithmName, encryptionSaltBytes,
          encryptionIterationCount);
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
