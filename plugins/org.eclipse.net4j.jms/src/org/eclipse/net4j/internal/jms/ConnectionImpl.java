/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.ITransportContainer;
import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.internal.jms.protocol.JMSClientProtocol;
import org.eclipse.net4j.internal.jms.protocol.JMSLogonRequest;
import org.eclipse.net4j.internal.jms.protocol.JMSOpenSessionRequest;
import org.eclipse.net4j.internal.util.container.LifecycleEventConverter;
import org.eclipse.net4j.internal.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.jms.JMSUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

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

public class ConnectionImpl extends Lifecycle implements Connection, IContainer<Session>
{
  private String connectorType;

  private String connectorDescription;

  private String userName;

  private String password;

  private ExceptionListener exceptionListener;

  private String clientID;

  private ConnectionMetaData metaData = new ConnectionMetaDataImpl(this);

  private long sendTimeout = 2500;

  private ITransportContainer transportContainer;

  private IChannel channel;

  private List<SessionImpl> sessions = new ArrayList(0);

  private transient IListener sessionListener = new LifecycleEventConverter(this)
  {
    @Override
    protected IContainerEvent createContainerEvent(IContainer container, Object element, Kind kind)
    {
      if (kind == IContainerDelta.Kind.REMOVED)
      {
        removeSession((SessionImpl)element);
      }

      return new ConnectionSessionsEvent((ConnectionImpl)container, (SessionImpl)element, kind);
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

  private boolean modified = false;

  private boolean stopped = true;

  public ConnectionImpl(ITransportContainer transportContainer, String connectorType, String connectorDescription,
      String userName, String password) throws JMSException
  {
    this.transportContainer = transportContainer == null ? JMSUtil.getTransportContainer() : transportContainer;
    if (transportContainer == null)
    {
      throw new JMSException("No transport container available");
    }

    this.connectorType = connectorType;
    this.connectorDescription = connectorDescription;
    this.userName = userName;
    this.password = password;

    IConnector connector = transportContainer.getConnector(connectorType, connectorDescription);
    JMSClientProtocol protocol = new JMSClientProtocol();
    protocol.setConnection(this);
    channel = connector.openChannel(protocol);
    channel.addListener(channelListener);

    try
    {
      if (!new JMSLogonRequest(channel, userName, password).send())
      {
        throw new JMSException("Server rejected logon request");
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

  public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector,
      ServerSessionPool sessionPool, int maxMessages)
  {
    throw new UnsupportedOperationException();
  }

  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages)
  {
    throw new UnsupportedOperationException();
  }

  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    ensureOpen();
    setModified();
    int sessionID = findFreeSessionID();
    SessionImpl session = new SessionImpl(this, sessionID, transacted, acknowledgeMode);
    addSession(session);

    try
    {
      if (!new JMSOpenSessionRequest(channel, sessionID).send())
      {
        throw new JMSException("Server rejected open session request");
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

  public ITransportContainer getTransportContainer()
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

  public ConnectionMetaData getMetaData()
  {
    ensureOpen();
    setModified();
    return metaData;
  }

  public String getClientID()
  {
    ensureOpen();
    setModified();
    return clientID;
  }

  public void setClientID(String clientID)
  {
    ensureOpen();
    if (clientID != null)
    {
      throw new IllegalStateException("clientID != null");
    }

    if (modified)
    {
      throw new IllegalStateException("modified == true");
    }

    this.clientID = clientID;
  }

  public ExceptionListener getExceptionListener()
  {
    ensureOpen();
    setModified();
    return exceptionListener;
  }

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

  public synchronized void close()
  {
    if (channel != null)
    {
      stop();
      for (SessionImpl session : getSessions())
      {
        session.close();
      }

      channel.removeListener(channelListener);
      channel.close();
      channel = null;
    }
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public void handleMessageFromSignal(int sessionID, long consumerID, MessageImpl message)
  {
    SessionImpl session = sessions.get(sessionID);
    session.handleServerMessage(consumerID, message);
  }

  public SessionImpl[] getSessions()
  {
    List<SessionImpl> result = new ArrayList(sessions.size());
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

  public Session[] getElements()
  {
    return getSessions();
  }

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
      else
      {
        return false;
      }
    }
  }

  private void setModified()
  {
    modified = true;
  }

  private void ensureOpen() throws IllegalStateException
  {
    if (channel == null)
    {
      throw new IllegalStateException("channel == null");
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ConnectionSessionsEvent extends SingleDeltaContainerEvent<Session> implements
      JMSConnectionSessionsEvent
  {
    private static final long serialVersionUID = 1L;

    public ConnectionSessionsEvent(ConnectionImpl connection, Session session, Kind kind)
    {
      super(connection, session, kind);
    }

    public Connection getConnection()
    {
      return (Connection)getContainer();
    }

    public Session getSession()
    {
      return getDeltaElement();
    }
  }
}
