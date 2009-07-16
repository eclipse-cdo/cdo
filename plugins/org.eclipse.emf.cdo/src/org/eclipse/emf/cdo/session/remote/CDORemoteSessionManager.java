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
package org.eclipse.emf.cdo.session.remote;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionEvent.CustomData;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * Provides collaborative access to the {@link #getRemoteSessions() remote sessions} that are connected to the same
 * repository as the {@link #getLocalSession() local session}. A CDORemoteSessionManager can be subscribed or
 * unsubscribed to changes in the set of remote sessions. It is subscribed if at least one is true:
 * <ol>
 * <li>At least one {@link IListener listener} is registered with this remote session manager.
 * <li>{@link #isForceSubscription() Force subscription} is <code>true</code>.
 * </ol>
 * If this remote session manager is subscribed it eventually fires the following {@link IEvent events} to
 * {@link #addListener(IListener) registered} listeners:
 * <ul>
 * <li> {@link IContainerEvent} with {@link CDORemoteSession} as generic type argument to reflect opened or closed remote
 * sessions.
 * <li> {@link CDORemoteSessionEvent.SubscriptionChanged} to reflect the ability of the remote session to receive and
 * possibly handle remote messages from other sessions.
 * <li> {@link CDORemoteSessionEvent.CustomData} to deliver custom data
 * {@link CDORemoteSession#sendCustomData(String, byte[]) sent} from other sessions .
 * </ul>
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORemoteSessionManager extends IContainer<CDORemoteSession>
{
  /**
   * Returns the {@link CDOSession local session} this CDORemoteSessionManager belongs to.
   */
  public CDOSession getLocalSession();

  /**
   * Returns the set of {@link CDORemoteSession remote sessions} that are connected to the same repository as the
   * {@link #getLocalSession() local session}. If this CDORemoteSessionManager itself is {@link #isSubscribed()
   * subscribed} the result is returned from a local cache for remote sessions, otherwise it is requested from the
   * server each time this method is called.
   */
  public CDORemoteSession[] getRemoteSessions();

  /**
   * Returns <code>true</code> if this CDORemoteSessionManager is subscribed to changes in the set of remote sessions
   * and delivers {@link CustomData custom data events}, <code>false</code> otherwise. It is subscribed if at least one
   * is true:
   * <ol>
   * <li>At least one {@link IListener listener} is registered with this remote session manager.
   * <li>{@link #isForceSubscription() Force subscription} is <code>true</code>.
   * </ol>
   * 
   * @see #addListener(IListener)
   * @see #setForceSubscription(boolean)
   */
  public boolean isSubscribed();

  /**
   * Returns <code>true</code> if this CDORemoteSessionManager shall be subscribed to changes in the set of remote
   * sessions and delivers {@link CustomData custom data events} even if no {@link IListener listener} is registered,
   * <code>false</code> otherwise.
   * 
   * @see #addListener(IListener)
   * @see #setForceSubscription(boolean)
   */
  public boolean isForceSubscription();

  /**
   * Enables or disables subscription to changes in the set of remote sessions even if no {@link IListener listener} is
   * registered.
   * 
   * @see #addListener(IListener)
   * @see #setForceSubscription(boolean)
   */
  public void setForceSubscription(boolean forceSubscription);

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public interface LocalSubscriptionChangedEvent extends IEvent
  {
    public CDORemoteSessionManager getSource();

    public boolean isSubscribed();
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class EventAdapter extends ContainerEventAdapter<CDORemoteSession>
  {
    public EventAdapter()
    {
    }

    protected void onLocalSubscription(boolean subscribed)
    {
    }

    protected void onOpened(CDORemoteSession remoteSession)
    {
    }

    protected void onClosed(CDORemoteSession remoteSession)
    {
    }

    protected void onSubscribed(CDORemoteSession remoteSession)
    {
    }

    protected void onUnsubscribed(CDORemoteSession remoteSession)
    {
    }

    protected void onCustomData(CDORemoteSession remoteSession, String type, byte[] data)
    {
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      if (event instanceof LocalSubscriptionChangedEvent)
      {
        LocalSubscriptionChangedEvent e = (LocalSubscriptionChangedEvent)event;
        onLocalSubscription(e.isSubscribed());
      }
      else if (event instanceof CDORemoteSessionEvent.SubscriptionChanged)
      {
        CDORemoteSessionEvent.SubscriptionChanged e = (CDORemoteSessionEvent.SubscriptionChanged)event;
        if (e.isSubscribed())
        {
          onSubscribed(e.getRemoteSession());
        }
        else
        {
          onUnsubscribed(e.getRemoteSession());
        }
      }
      else if (event instanceof CDORemoteSessionEvent.CustomData)
      {
        CDORemoteSessionEvent.CustomData e = (CDORemoteSessionEvent.CustomData)event;
        onCustomData(e.getRemoteSession(), e.getType(), e.getData());
      }
      else
      {
        super.notifyOtherEvent(event);
      }
    }

    @Override
    protected final void onAdded(IContainer<CDORemoteSession> container, CDORemoteSession element)
    {
      onOpened(element);
    }

    @Override
    protected final void onRemoved(IContainer<CDORemoteSession> container, CDORemoteSession element)
    {
      onClosed(element);
    }
  }
}
