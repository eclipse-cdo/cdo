/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.jms.internal.server.protocol.JMSServerMessageRequest;
import org.eclipse.net4j.jms.internal.server.protocol.JMSServerProtocol;
import org.eclipse.net4j.jms.server.IServerConsumer;
import org.eclipse.net4j.jms.server.IStoreTransaction;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class ServerConsumer implements IServerConsumer
{
  private ServerSession session;

  private long id;

  private ServerDestination destination;

  private String messageSelector;

  private boolean noLocal;

  private boolean durable;

  private ConcurrentMap<String, MessageImpl> messages = new ConcurrentHashMap<String, MessageImpl>();

  public ServerConsumer(long id, ServerDestination destination, String messageSelector, boolean noLocal, boolean durable)
  {
    this.id = id;
    this.destination = destination;
    this.messageSelector = messageSelector;
    this.noLocal = noLocal;
    this.durable = durable;
  }

  @Override
  public ServerSession getSession()
  {
    return session;
  }

  public void setSession(ServerSession session)
  {
    this.session = session;
  }

  @Override
  public long getID()
  {
    return id;
  }

  @Override
  public ServerDestination getDestination()
  {
    return destination;
  }

  @Override
  public String getMessageSelector()
  {
    return messageSelector;
  }

  @Override
  public boolean isNoLocal()
  {
    return noLocal;
  }

  @Override
  public JMSServerProtocol getProtocol()
  {
    return session.getConnection().getProtocol();
  }

  @Override
  public boolean isDurable()
  {
    return durable;
  }

  public boolean handleClientMessage(IStoreTransaction transaction, MessageImpl message)
  {
    try
    {
      String messageID = message.getJMSMessageID();
      synchronized (messages)
      {
        messages.put(messageID, message);
      }

      new JMSServerMessageRequest(getProtocol(), session.getID(), id, message).sendAsync();
      transaction.messageSent(message, id);
      return true;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return false;
    }
  }

  public void handleAcknowledge(IStoreTransaction transaction)
  {
    synchronized (messages)
    {
      if (messages.isEmpty())
      {
        return;
      }

      for (MessageImpl message : messages.values())
      {
        transaction.messageAcknowledged(message, id);
        IOUtil.OUT().println("\nMessage acknowledged: " + message.getJMSMessageID() + "  (consumer=" + id + ")\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }

      messages.clear();
    }
  }

  public void handleRecover(IStoreTransaction transaction)
  {
    synchronized (messages)
    {
      if (messages.isEmpty())
      {
        return;
      }

      for (MessageImpl message : messages.values())
      {
        IOUtil.OUT().println("\nRecovering message: " + message.getJMSMessageID() + "  (consumer=" + id + ")\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        session.getConnection().getServer().addWork(message);
      }
    }
  }
}
