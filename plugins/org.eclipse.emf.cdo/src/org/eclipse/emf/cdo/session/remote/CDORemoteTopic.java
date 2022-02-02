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

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.Set;

/**
 * A topic to that the {@link CDORemoteSessionManager#getLocalSession() local session} and
 * {@link CDORemoteSession remote sessions} can subscribe in order to receive {@link IEvent events}
 * about their {@link IContainerEvent participation} and the {@link CDORemoteSessionMessage messages}
 * that they send to the topic.
 * <p>
 * Use the {@link CDORemoteSessionManager#subscribeTopic(String) CDORemoteSessionManager.subscribeTopic()}
 * method to subscribe to a topic and the {@link #unsubscribe() CDORemoteTopic.unsubscribe()} method
 * to unsubscribe from it.
 * <p>
 * Use the {@link #sendMessage(CDORemoteSessionMessage) CDORemoteTopic.sendMessage()} method
 * to send a message to all subscribed {@link CDORemoteSession remote sessions}.
 * <p>
 * This fires the following {@link IEvent events} to {@link #addListener(IListener) registered} listeners:
 * <ul>
 * <li> {@link IContainerEvent} with {@link CDORemoteSession} as generic type argument to reflect subscribed
 *      or unsubscribed remote sessions.
 * <li> {@link CDORemoteSessionEvent.MessageReceived} to deliver custom data
 *      {@link CDORemoteSession#sendMessage(CDORemoteSessionMessage) sent} from other sessions.
 * </ul>
 *
 * @author Eike Stepper
 * @since 4.17
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
*/
public interface CDORemoteTopic extends IContainer<CDORemoteSession>, Comparable<CDORemoteTopic>
{
  /**
   * Returns the remote session manager that manages this remote topic.
   */
  public CDORemoteSessionManager getManager();

  /**
   * Returns the <code>id</code> that identifies this topic.
   */
  public String getID();

  /**
   * Returns an array of the {@link CDORemoteSession remote sessions} that participate in this topic.
   */
  public CDORemoteSession[] getRemoteSessions();

  /**
   * Sends the given <code>message</code> to all subscribed {@link CDORemoteSession remote sessions}.
   *
   * @return The set of {@link CDORemoteSession recipients} that the message has been forwarded to by the server.
   *         <b>Note:</b> No assumption must be made on whether a recipient session received the message and was able to
   *         handle it adequately!
   */
  public Set<CDORemoteSession> sendMessage(CDORemoteSessionMessage message);

  /**
   * Returns <code>false</code> if the {@link #unsubscribe()} method had been called, <code>true</code> otherwise.
   */
  public boolean isSubscribed();

  /**
   * Unsubscribes from this topic. All further calls to {@link #isSubscribed()} will return <code>false</code>.
   */
  public void unsubscribe();
}
