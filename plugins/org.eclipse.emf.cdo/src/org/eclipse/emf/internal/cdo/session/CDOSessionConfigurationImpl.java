/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionConfigurationImpl implements CDOSessionConfiguration
{
  private InternalCDOSession session;

  private String repositoryName;

  private CDOSession.ExceptionHandler exceptionHandler;

  private CDOPackageRegistry packageRegistry;

  private CDORevisionCache revisionCache;

  private CDOAuthenticator authenticator = new AuthenticatorImpl();

  private boolean activateOnOpen = true;

  public CDOSessionConfigurationImpl()
  {
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    checkNotOpen();
    this.repositoryName = repositoryName;
  }

  public CDOSession.ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }

  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler)
  {
    checkNotOpen();
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * @since 2.0
   */
  public CDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  /**
   * @since 2.0
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    checkNotOpen();
    this.packageRegistry = packageRegistry;
  }

  /**
   * @since 2.0
   */
  public CDORevisionCache getRevisionCache()
  {
    return revisionCache;
  }

  /**
   * @since 2.0
   */
  public void setRevisionCache(CDORevisionCache revisionCache)
  {
    checkNotOpen();
    this.revisionCache = revisionCache;
  }

  public CDOAuthenticator getAuthenticator()
  {
    return authenticator;
  }

  public void setAuthenticator(CDOAuthenticator authenticator)
  {
    checkNotOpen();
    this.authenticator = authenticator;
  }

  public boolean isActivateOnOpen()
  {
    return activateOnOpen;
  }

  public void setActivateOnOpen(boolean activateOnOpen)
  {
    checkNotOpen();
    this.activateOnOpen = activateOnOpen;
  }

  public boolean isSessionOpen()
  {
    if (session == null)
    {
      return false;
    }

    if (!session.isClosed())
    {
      return true;
    }

    session = null;
    return false;
  }

  /**
   * @since 2.0
   */
  public CDOSession openSession()
  {
    if (!isSessionOpen())
    {
      session = createSession();
      session.setRepositoryName(repositoryName);
      session.setPackageRegistry((InternalCDOPackageRegistry)packageRegistry);
      session.getRevisionManager().setCache(revisionCache);
      session.setAuthenticator(authenticator);

      if (activateOnOpen)
      {
        session.activate();
      }
    }

    return session;
  }

  protected void checkNotOpen()
  {
    if (isSessionOpen())
    {
      throw new IllegalStateException(Messages.getString("CDOSessionConfigurationImpl.0")); //$NON-NLS-1$
    }
  }

  protected abstract InternalCDOSession createSession();

  /**
   * @author Eike Stepper
   */
  protected class AuthenticatorImpl implements CDOAuthenticator
  {
    private String encryptionAlgorithmName = SecurityUtil.PBE_WITH_MD5_AND_DES;

    private byte[] encryptionSaltBytes = SecurityUtil.DEFAULT_SALT;

    private int encryptionIterationCount = SecurityUtil.DEFAULT_ITERATION_COUNT;

    private IPasswordCredentialsProvider credentialsProvider;

    public AuthenticatorImpl()
    {
    }

    public String getEncryptionAlgorithmName()
    {
      return encryptionAlgorithmName;
    }

    public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
    {
      checkSessionNotOpened();
      this.encryptionAlgorithmName = encryptionAlgorithmName;
    }

    public byte[] getEncryptionSaltBytes()
    {
      return encryptionSaltBytes;
    }

    public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
    {
      checkSessionNotOpened();
      this.encryptionSaltBytes = encryptionSaltBytes;
    }

    public int getEncryptionIterationCount()
    {
      return encryptionIterationCount;
    }

    public void setEncryptionIterationCount(int encryptionIterationCount)
    {
      checkSessionNotOpened();
      this.encryptionIterationCount = encryptionIterationCount;
    }

    public IPasswordCredentialsProvider getCredentialsProvider()
    {
      return credentialsProvider;
    }

    public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
    {
      checkSessionNotOpened();
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
      session.setUserID(userID);

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

    private void checkSessionNotOpened()
    {
      if (session != null)
      {
        throw new IllegalStateException("Not allowed after the session has been opened"); //$NON-NLS-1$
      }
    }
  }
}
