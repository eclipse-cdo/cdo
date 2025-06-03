/*
 * Copyright (c) 2009-2013, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * Configures and opens new {@link CDOSession sessions}.
 * <p>
 * A session configuration can fire the following events:
 * <ul>
 * <li> {@link CDOSessionConfiguration.SessionOpenedEvent} after a session has been opened by this configuration.
 * </ul>
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOSessionConfiguration extends INotifier
{
  /**
   * @since 4.2
   */
  public String getUserID();

  /**
   * @since 4.2
   */
  public void setUserID(String userID);

  /**
   * @see CDOSession.Options#isPassiveUpdateEnabled()
   * @since 3.0
   */
  public boolean isPassiveUpdateEnabled();

  /**
   * @see CDOSession.Options#setPassiveUpdateEnabled(boolean)
   * @since 3.0
   */
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled);

  /**
   * @see CDOSession.Options#getPassiveUpdateMode()
   * @since 3.0
   */
  public PassiveUpdateMode getPassiveUpdateMode();

  /**
   * @see CDOSession.Options#setPassiveUpdateMode(PassiveUpdateMode)
   * @since 3.0
   */
  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode);

  /**
   * @since 4.1
   */
  public LockNotificationMode getLockNotificationMode();

  /**
   * @since 4.1
   */
  public void setLockNotificationMode(LockNotificationMode mode);

  /**
   * @see CDOSession#getExceptionHandler()
   */
  public CDOSession.ExceptionHandler getExceptionHandler();

  /**
   * A special exception handler can be set <b>before</b> the session is opened and can not be changed thereafter.
   *
   * @see CDOSession#getExceptionHandler()
   */
  public void setExceptionHandler(CDOSession.ExceptionHandler exceptionHandler);

  /**
   * @see CDOSession#getIDGenerator()
   * @since 4.1
   */
  public CDOIDGenerator getIDGenerator();

  /**
   * A special ID generator can be set <b>before</b> the session is opened and can not be changed thereafter. If not
   * <code>null</code>, the passed generator <b>must be</b> thread-safe.
   *
   * @see CDOSession#getIDGenerator()
   * @since 4.1
   */
  public void setIDGenerator(CDOIDGenerator idGenerator);

  /**
   * @see CDOSession#getFetchRuleManager()
   * @since 4.1
   */
  public CDOFetchRuleManager getFetchRuleManager();

  /**
   * A special ID generator can be set <b>before</b> the session is opened and can not be changed thereafter. If not
   * <code>null</code>, the passed generator <b>must be</b> thread-safe.
   *
   * @see CDOSession#getFetchRuleManager()
   * @since 4.1
   */
  public void setFetchRuleManager(CDOFetchRuleManager fetchRuleManager);

  /**
   * @since 4.2
   */
  public CDOBranchManager getBranchManager();

  /**
   * @since 4.2
   */
  public void setBranchManager(CDOBranchManager branchManager);

  /**
   * Returns the authenticator of this configuration, never <code>null</code>.
   *
   * @deprecated As of 4.2 use {@link #getCredentialsProvider()} and {@link #setCredentialsProvider(IPasswordCredentialsProvider)}, respectively
   */
  @Deprecated
  public org.eclipse.emf.cdo.common.protocol.CDOAuthenticator getAuthenticator();

  /**
   * @since 4.2
   */
  public IPasswordCredentialsProvider getCredentialsProvider();

  /**
   * @since 4.2
   */
  public void setCredentialsProvider(IPasswordCredentialsProvider credentialsProvider);

  /**
   * @since 4.27
   */
  public byte[] getOneTimeLoginToken();

  /**
   * @since 4.27
   */
  public void setOneTimeLoginToken(byte[] oneTimeLoginToken);

  /**
   * Returns <code>true</code> if the session opened by {@link #openSession()} will be automatically activated,
   * <code>false</code> otherwise.
   */
  public boolean isActivateOnOpen();

  /**
   * Specifies whether the session opened by {@link #openSession()} will be automatically activated or not.
   */
  public void setActivateOnOpen(boolean activateOnOpen);

  /**
   * Returns <code>true</code> if the session for this configuration is currently open, <code>false</code> otherwise.
   */
  public boolean isSessionOpen();

  /**
   * Opens the session for this configuration. Once the session is openend this method always returns the same session
   * instance. Therefore it is impossible to change this configuration while the session is open.
   */
  public CDOSession openSession();

  /**
   * Fired from a {@link CDOSessionConfiguration session configuration} after a new {@link CDOSession session} has been
   * opened.
   *
   * @author Eike Stepper
   * @since 4.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface SessionOpenedEvent extends IEvent
  {
    @Override
    public CDOSessionConfiguration getSource();

    public CDOSession getOpenedSession();
  }
}
