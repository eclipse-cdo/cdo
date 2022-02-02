/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session.remote;

import org.eclipse.net4j.util.event.IEvent;

/**
 * A generic {@link IEvent event} fired from a {@link CDORemoteTopic remote topic}.
 *
 * @author Eike Stepper
 * @since 4.17
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORemoteTopicEvent extends IEvent
{
  /**
   * Returns the topic that emitted this event.
   */
  @Override
  public CDORemoteTopic getSource();

  /**
   * Returns the remote session that caused this event, i.e., the sender.
   */
  public CDORemoteSession getRemoteSession();

  /**
   * A {@link CDORemoteTopicEvent remote topic event} fired from a {@link CDORemoteTopic topic}
   * when a {@link MessageReceived#getMessage() message} from a {@link CDORemoteSession remote session} has
   * been received.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface MessageReceived extends CDORemoteTopicEvent
  {
    /**
     * Returns the message that the {@link #getRemoteSession() sender} has sent to the {@link #getSource() topic}
     * of this event.
     */
    public CDORemoteSessionMessage getMessage();
  }
}
