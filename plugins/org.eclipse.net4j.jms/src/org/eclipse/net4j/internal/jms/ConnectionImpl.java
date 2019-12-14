/*
 * Copyright (c) 2007-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.internal.jms.messages.Messages;
import org.eclipse.net4j.internal.jms.protocol.JMSClientProtocol;
import org.eclipse.net4j.internal.jms.protocol.JMSLogonRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSOpenSessionRequest;
import org.eclipse.net4j.jms.JMSUtil;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.LifecycleEventConverter;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

import java.util.ArrayList;
import java.util.List;

public class ConnectionImpl extends Container<Session> implements Connection
{
  private String connectorType;

  private String connectorDescription;

  private String userName;

  private String password;

  private ExceptionListener exceptionListener;

  private String clientID;

  private ConnectionMetaData metaData = new ConnectionMetaDataImpl(this);

  private long sendTimeout = 2500;

  private IManagedContainer transportContainer;

  private JMSClientProtocol protocol;

  private List<SessionImpl> sessions = new ArrayList<>(0);

  private transient IListener sessionListener = new LifecycleEventConverter<Session>(this)
  {
    @Override
    protected IContainerEvent<Session> createContainerEvent(IContainer<Session> container, Session element, Kind kind)
    {
      if (kind == IContainerDelta.Kind.REMOVED)
      {
        removeSession((SessionImpl)element);
      }

      return new SingleDeltaContainerEvent<>(container, element, kind);
    }
  };

  private transient IListener channelListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      close();
    }
  };

  private boolean modified;

  private boolean stopped = true;

  public ConnectionImpl(IManagedContainer transportContainer, String connectorType, String connectorDescription, String userName, String password)
      throws JMSException
  {
    this.transportContainer = transportContainer == null ? JMSUtil.getTransportContainer() : transportContainer;
    if (transportContainer == null)
    {
      throw new JMSException(Messages.getString("ConnectionImpl_0")); //$NON-NLS-1$
    }

    this.connectorType = connectorType;
    this.connectorDescription = connectorDescription;
    this.userName = userName;
    this.password = password;

    IConnector connector = Net4jUtil.getConnector(transportContainer, connectorType, connectorDescription);
    JMSClientProtocol protocol = new JMSClientProtocol(this);
    IChannel channel = protocol.open(connector);
    channel.addListener(channelListener);

    try
    {
      if (!new JMSLogonRequest(protocol, userName, password).send())
      {
        throw new JMSException(Messages.getString("ConnectionImpl_1")); //$NON-NLS-1$
      }
    }
    catch (JMSException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new JMSException(ex.getMessage());
    }
  }

  @Override
  public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool,
      int maxMessages)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    ensureOpen();
    setModified();
    int sessionID = findFreeSessionID();
    SessionImpl session = new SessionImpl(this, sessionID, transacted, acknowledgeMode);
    addSession(session);

    try
    {
      if (!new JMSOpenSessionRequest(protocol, sessionID).send())
      {
        throw new JMSException(Messages.getString("ConnectionImpl_2")); //$NON-NLS-1$
      }
    }
    catch (JMSException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new JMSException(ex.getMessage());
    }

    return session;
  }

  public IManagedContainer getTransportContainer()
  {
    return transportContainer;
  }

  public String getConnectorType()
  {
    return connectorType;
  }

  public String getConnectorDescription()
  {
    return connectorDescription;
  }

  public String getUserName()
  {
    return userName;
  }

  public String getPassword()
  {
    return password;
  }

  @Override
  public ConnectionMetaData getMetaData()
  {
    ensureOpen();
    setModified();
    return metaData;
  }

  @Override
  public String getClientID()
  {
    ensureOpen();
    setModified();
    return clientID;
  }

  @Override
  public void setClientID(String clientID)
  {
    ensureOpen();
    if (clientID != null)
    {
      throw new IllegalStateException("clientID != null"); //$NON-NLS-1$
    }

    if (modified)
    {
      throw new IllegalStateException("modified == true"); //$NON-NLS-1$
    }

    this.clientID = clientID;
  }

  @Override
  public ExceptionListener getExceptionListener()
  {
    ensureOpen();
    setModified();
    return exceptionListener;
  }

  @Override
  public void setExceptionListener(ExceptionListener listener)
  {
    ensureOpen();
    setModified();
    exceptionListener = listener;
  }

  public long getSendTimeout()
  {
    return sendTimeout;
  }

  public void setSendTimeout(long sendTimeout)
  {
    this.sendTimeout = sendTimeout;
  }

  @Override
  public synchronized void start() throws JMSException
  {
    ensureOpen();
    setModified();
    if (stopped)
    {
      for (SessionImpl session : getSessions())
      {
        try
        {
          session.activate();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          throw new JMSException(ex.getMessage());
        }
      }

      stopped = false;
    }
  }

  @Override
  public synchronized void stop()
  {
    ensureOpen();
    setModified();
    if (!stopped)
    {
      for (SessionImpl session : getSessions())
      {
        session.deactivate();
      }

      stopped = true;
    }
  }

  @Override
  public synchronized void close()
  {
    if (protocol != null)
    {
      stop();
      for (SessionImpl session : getSessions())
      {
        session.close();
      }

      protocol.getChannel().removeListener(channelListener);
      protocol.close();
      protocol = null;
    }
  }

  /**
   * @since 2.0
   */
  public JMSClientProtocol getProtocol()
  {
    return protocol;
  }

  public void handleMessageFromSignal(int sessionID, long consumerID, MessageImpl message)
  {
    SessionImpl session = sessions.get(sessionID);
    session.handleServerMessage(consumerID, message);
  }

  public SessionImpl[] getSessions()
  {
    List<SessionImpl> result = new ArrayList<>(sessions.size());
    synchronized (sessions)
    {
      for (SessionImpl session : sessions)
      {
        if (session != null)
        {
          result.add(session);
        }
      }
    }

    return result.toArray(new SessionImpl[result.size()]);
  }

  @Override
  public Session[] getElements()
  {
    return getSessions();
  }

  @Override
  public boolean isEmpty()
  {
    return getSessions().length == 0;
  }

  private int findFreeSessionID()
  {
    synchronized (sessions)
    {
      int size = sessions.size();
      for (int i = 0; i < size; i++)
      {
        if (sessions.get(i) == null)
        {
          return i;
        }
      }

      return size;
    }
  }

  private void addSession(SessionImpl session)
  {
    synchronized (sessions)
    {
      int sessionID = session.getID();
      while (sessionID >= sessions.size())
      {
        sessions.add(null);
      }

      sessions.set(sessionID, session);
    }
  }

  private boolean removeSession(SessionImpl session)
  {
    synchronized (sessions)
    {
      int sessionID = session.getID();
      if (sessions.get(sessionID) == session)
      {
        session.removeListener(sessionListener);
        sessions.set(sessionID, null);
        return true;
      }

      return false;
    }
  }

  private void setModified()
  {
    modified = true;
  }

  private void ensureOpen() throws IllegalStateException
  {
    if (protocol == null)
    {
      throw new IllegalStateException("protocol == null"); //$NON-NLS-1$
    }
  }
}
