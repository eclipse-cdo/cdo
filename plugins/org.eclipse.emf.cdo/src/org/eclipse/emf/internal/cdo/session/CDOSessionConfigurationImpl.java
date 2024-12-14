/*
 * Copyright (c) 2009-2012, 2019, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOUserInfoManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSessionConfiguration;

/**
 * @author Eike Stepper
 */
public abstract class CDOSessionConfigurationImpl extends Notifier implements InternalCDOSessionConfiguration
{
  private String userID;

  private boolean passiveUpdateEnabled = true;

  private PassiveUpdateMode passiveUpdateMode = PassiveUpdateMode.INVALIDATIONS;

  private LockNotificationMode lockNotificationMode = LockNotificationMode.IF_REQUIRED_BY_VIEWS;

  private IPasswordCredentialsProvider credentialsProvider;

  private byte[] oneTimeLoginToken;

  private CDOSession.ExceptionHandler exceptionHandler;

  private CDOIDGenerator idGenerator;

  private CDOFetchRuleManager fetchRuleManager;

  private InternalCDOBranchManager branchManager;

  private InternalCDOPackageRegistry packageRegistry;

  private InternalCDORevisionManager revisionManager;

  private InternalCDOCommitInfoManager commitInfoManager;

  private CDOUserInfoManager userInfoManager;

  private boolean loginPeek;

  private boolean mainBranchLocal;

  private boolean activateOnOpen = true;

  private InternalCDOSession session;

  private final IListener lifecycleEventAdapter = new LifecycleEventAdapter()
  {
    @Override
    protected void onActivated(final ILifecycle newSession)
    {
      newSession.removeListener(lifecycleEventAdapter);
      fireEvent(new SessionOpenedEvent()
      {
        @Override
        public CDOSessionConfiguration getSource()
        {
          return CDOSessionConfigurationImpl.this;
        }

        @Override
        public CDOSession getOpenedSession()
        {
          return (CDOSession)newSession;
        }
      });
    }
  };

  public CDOSessionConfigurationImpl()
  {
  }

  @Override
  public String getUserID()
  {
    return userID;
  }

  @Override
  public void setUserID(String userID)
  {
    checkNotOpen();
    this.userID = userID;
  }

  @Override
  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  @Override
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    checkNotOpen();
    uncheckedSetPassiveUpdateEnabled(passiveUpdateEnabled);
  }

  protected void uncheckedSetPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  @Override
  public PassiveUpdateMode getPassiveUpdateMode()
  {
    return passiveUpdateMode;
  }

  @Override
  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    checkNotOpen();
    uncheckedSetPassiveUpdateMode(passiveUpdateMode);
  }

  protected void uncheckedSetPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    this.passiveUpdateMode = passiveUpdateMode;
  }

  @Override
  public LockNotificationMode getLockNotificationMode()
  {
    return lockNotificationMode;
  }

  @Override
  public void setLockNotificationMode(LockNotificationMode lockNotificationMode)
  {
    checkNotOpen();
    uncheckedSetLockNotificationMode(lockNotificationMode);
  }

  protected void uncheckedSetLockNotificationMode(LockNotificationMode lockNotificationMode)
  {
    this.lockNotificationMode = lockNotificationMode;
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator()
  {
    return new org.eclipse.emf.cdo.common.protocol.CDOAuthenticator()
    {
      @Override
      public String getEncryptionAlgorithmName()
      {
        return null;
      }

      @Override
      public void setEncryptionAlgorithmName(String encryptionAlgorithmName)
      {
      }

      @Override
      public byte[] getEncryptionSaltBytes()
      {
        return null;
      }

      @Override
      public void setEncryptionSaltBytes(byte[] encryptionSaltBytes)
      {
      }

      @Override
      public int getEncryptionIterationCount()
      {
        return 0;
      }

      @Override
      public void setEncryptionIterationCount(int encryptionIterationCount)
      {
      }

      @Override
      public IPasswordCredentialsProvider getCredentialsProvider()
      {
        return CDOSessionConfigurationImpl.this.getCredentialsProvider();
      }

      @Override
      public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
      {
        CDOSessionConfigurationImpl.this.setCredentialsProvider(credentialsProvider);
      }

      @Override
      public org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult authenticate(byte[] randomToken)
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public IPasswordCredentialsProvider getCredentialsProvider()
  {
    return credentialsProvider;
  }

  @Override
  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider)
  {
    checkNotOpen();
    this.credentialsProvider = credentialsProvider;
  }

  @Override
  public byte[] getOneTimeLoginToken()
  {
    return oneTimeLoginToken;
  }

  @Override
  public void setOneTimeLoginToken(byte[] oneTimeLoginToken)
  {
    this.oneTimeLoginToken = oneTimeLoginToken;
  }

  @Override
  public CDOSession.ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }

  @Override
  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler)
  {
    checkNotOpen();
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public CDOIDGenerator getIDGenerator()
  {
    return idGenerator;
  }

  @Override
  public void setIDGenerator(CDOIDGenerator idGenerator)
  {
    checkNotOpen();
    this.idGenerator = idGenerator;
  }

  @Override
  public CDOFetchRuleManager getFetchRuleManager()
  {
    return fetchRuleManager;
  }

  @Override
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager)
  {
    checkNotOpen();
    this.fetchRuleManager = fetchRuleManager;
  }

  @Override
  public InternalCDOBranchManager getBranchManager()
  {
    return branchManager;
  }

  @Override
  public void setBranchManager(CDOBranchManager branchManager)
  {
    checkNotOpen();
    this.branchManager = (InternalCDOBranchManager)branchManager;
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    checkNotOpen();
    this.packageRegistry = (InternalCDOPackageRegistry)packageRegistry;
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public void setRevisionManager(CDORevisionManager revisionManager)
  {
    checkNotOpen();
    this.revisionManager = (InternalCDORevisionManager)revisionManager;
  }

  /**
   * Returns the commit info manager. The commit info manager may be used to query commit infos.
   *
   * @return the commit info manager
   * @see CDOCommitInfoManager
   */
  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  /**
   * Sets the commit info manager. The commit info manager may be used to query commit infos. May only be called as long
   * as the session's not opened yet
   *
   * @param commitInfoManager
   *          the new commit info manager
   * @see CDOCommitInfoManager
   */
  public void setCommitInfoManager(CDOCommitInfoManager commitInfoManager)
  {
    checkNotOpen();
    this.commitInfoManager = (InternalCDOCommitInfoManager)commitInfoManager;
  }

  public CDOUserInfoManager getUserInfoManager()
  {
    return userInfoManager;
  }

  public void setUserInfoManager(CDOUserInfoManager userInfoManager)
  {
    checkNotOpen();
    this.userInfoManager = userInfoManager;
  }

  @Override
  public boolean isLoginPeek()
  {
    return loginPeek;
  }

  @Override
  public void setLoginPeek(boolean loginPeek)
  {
    this.loginPeek = loginPeek;
  }

  @Override
  public boolean isMainBranchLocal()
  {
    return mainBranchLocal;
  }

  @Override
  public void setMainBranchLocal(boolean mainBranchLocal)
  {
    this.mainBranchLocal = mainBranchLocal;
  }

  @Override
  public boolean isActivateOnOpen()
  {
    return activateOnOpen;
  }

  @Override
  public void setActivateOnOpen(boolean activateOnOpen)
  {
    checkNotOpen();
    this.activateOnOpen = activateOnOpen;
  }

  @Override
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
  @Override
  public CDOSession openSession()
  {
    if (!isSessionOpen())
    {
      session = createSession();
      session.addListener(lifecycleEventAdapter);
      configureSession(session);

      if (activateOnOpen)
      {
        session.activate();
      }
    }

    return session;
  }

  protected void configureSession(InternalCDOSession session)
  {
    session.options().setPassiveUpdateEnabled(passiveUpdateEnabled);
    session.options().setPassiveUpdateMode(passiveUpdateMode);
    session.options().setLockNotificationMode(lockNotificationMode);

    session.setLoginPeek(loginPeek);
    session.setMainBranchLocal(mainBranchLocal);
    session.setExceptionHandler(exceptionHandler);
    session.setFetchRuleManager(fetchRuleManager);
    session.setIDGenerator(idGenerator);
    session.setCredentialsProvider(credentialsProvider);
    session.setOneTimeLoginToken(oneTimeLoginToken);
    session.setRevisionManager(revisionManager);
    session.setBranchManager(branchManager);
    session.setCommitInfoManager(commitInfoManager);
    session.setPackageRegistry(packageRegistry);
  }

  @Override
  public InternalCDOSession getSession()
  {
    checkOpen();
    return session;
  }

  protected void setSession(InternalCDOSession session)
  {
    this.session = session;
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
}
