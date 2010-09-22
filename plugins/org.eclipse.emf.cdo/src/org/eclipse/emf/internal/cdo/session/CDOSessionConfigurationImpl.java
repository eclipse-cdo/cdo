/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;

import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.session.remote.CDORemoteSessionManagerImpl;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionConfiguration;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionConfigurationImpl implements InternalCDOSessionConfiguration
{
  private boolean passiveUpdateEnabled = true;

  private PassiveUpdateMode passiveUpdateMode = PassiveUpdateMode.INVALIDATIONS;

  private CDOAuthenticator authenticator = new AuthenticatorImpl();

  private CDOSession.ExceptionHandler exceptionHandler;

  private boolean activateOnOpen = true;

  private InternalCDOSession session;

  public CDOSessionConfigurationImpl()
  {
  }

  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    checkNotOpen();
    uncheckedSetPassiveUpdateEnabled(passiveUpdateEnabled);
  }

  protected void uncheckedSetPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  public PassiveUpdateMode getPassiveUpdateMode()
  {
    return passiveUpdateMode;
  }

  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    checkNotOpen();
    uncheckedSetPassiveUpdateMode(passiveUpdateMode);
  }

  protected void uncheckedSetPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    this.passiveUpdateMode = passiveUpdateMode;
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

  public CDOSession.ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }

  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler)
  {
    checkNotOpen();
    this.exceptionHandler = exceptionHandler;
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
      session.setExceptionHandler(exceptionHandler);
      if (activateOnOpen)
      {
        session.activate();
      }
    }

    return session;
  }

  public InternalCDOSession getSession()
  {
    checkOpen();
    return session;
  }

  protected void setSession(InternalCDOSession session)
  {
    this.session = session;
  }

  public void activateSession(InternalCDOSession session) throws Exception
  {
    InternalCDORemoteSessionManager remoteSessionManager = new CDORemoteSessionManagerImpl();
    remoteSessionManager.setLocalSession(session);
    session.setRemoteSessionManager(remoteSessionManager);
    remoteSessionManager.activate();
  }

  public void deactivateSession(InternalCDOSession session) throws Exception
  {
    CDORemoteSessionManager remoteSessionManager = session.getRemoteSessionManager();
    session.setRemoteSessionManager(null);
    LifecycleUtil.deactivate(remoteSessionManager);

    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    LifecycleUtil.deactivate(sessionProtocol);
    session.setSessionProtocol(null);
    session = null;
  }

  protected void checkOpen()
  {
    if (!isSessionOpen())
    {
      throw new IllegalStateException(Messages.getString("CDOSessionConfigurationImpl.1")); //$NON-NLS-1$
    }
  }

  protected void checkNotOpen()
  {
    if (isSessionOpen())
    {
      throw new IllegalStateException(Messages.getString("CDOSessionConfigurationImpl.0")); //$NON-NLS-1$
    }
  }

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
