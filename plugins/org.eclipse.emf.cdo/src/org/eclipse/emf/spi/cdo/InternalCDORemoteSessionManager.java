/*
 * Copyright (c) 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDORemoteSessionManager extends CDORemoteSessionManager, ILifecycle, IExecutorServiceProvider
{
  /**
   * @since 3.0
   */
  @Override
  public InternalCDOSession getLocalSession();

  /**
   * @since 3.0
   */
  public void setLocalSession(InternalCDOSession localSession);

  /**
   * @since 4.17
   */
  @Override
  public InternalCDORemoteSession[] getRemoteSessions();

  /**
   * @since 4.17
   */
  public InternalCDORemoteSession getRemoteSession(int sessionID);

  /**
   * @since 3.0
   */
  public InternalCDORemoteSession createRemoteSession(int sessionID, String userID, boolean subscribed);

  public void handleRemoteSessionOpened(int sessionID, String userID);

  public void handleRemoteSessionClosed(int sessionID);

  /**
   *@deprecated As of 4.8 use {@link #handleRemoteSessionSubscribed(int, String, boolean)}.
   */
  @Deprecated
  public void handleRemoteSessionSubscribed(int sessionID, boolean subscribed);

  /**
   * @since 4.17
   */
  public void handleRemoteSessionSubscribed(int sessionID, String topicID, boolean subscribed);

  /**
   * @since 3.0
   * @deprecated As of 4.8 use {@link #handleRemoteSessionMessage(int, String, CDORemoteSessionMessage)}.
   */
  @Deprecated
  public void handleRemoteSessionMessage(int sessionID, CDORemoteSessionMessage message);

  /**
   * @since 4.17
   */
  public void handleRemoteSessionMessage(int sessionID, String topicID, CDORemoteSessionMessage message);

  /**
   * @since 4.17
   */
  @Override
  public InternalCDORemoteTopic subscribeTopic(String id);

  /**
   * @since 4.17
   */
  public void unsubscribeTopic(InternalCDORemoteTopic remoteTopic);

  /**
   * @since 4.17
   */
  @Override
  public InternalCDORemoteTopic[] getSubscribedTopics();

  /**
   * @since 4.17
   */
  public InternalCDORemoteTopic getSubscribedTopic(String id);
}
