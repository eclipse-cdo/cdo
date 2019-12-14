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
package org.eclipse.net4j.jms.internal.server;

import org.eclipse.net4j.internal.jms.ConnectionFactoryImpl;
import org.eclipse.net4j.internal.jms.JMSDestination;
import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.jms.internal.server.messages.Messages;
import org.eclipse.net4j.jms.server.IDestination;
import org.eclipse.net4j.jms.server.IServer;
import org.eclipse.net4j.jms.server.IStore;
import org.eclipse.net4j.jms.server.IStoreTransaction;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.concurrent.NonBlockingLongCounter;
import org.eclipse.net4j.util.concurrent.QueueWorker;
import org.eclipse.net4j.util.om.OMPlatform;

import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class Server extends QueueWorker<MessageImpl> implements IServer
{
  public static final Server INSTANCE = new Server();

  private static final boolean REBIND_DESTINATIONS = true;

  private NonBlockingLongCounter messageIDCounter = new NonBlockingLongCounter();

  private NonBlockingLongCounter consumerIDCounter = new NonBlockingLongCounter();

  private IStore store = null;

  private Context jndiContext;

  private ConcurrentMap<String, ServerDestination> destinations = new ConcurrentHashMap<>();

  private Set<ServerConnection> connections = new HashSet<>();

  private ConcurrentMap<Long, ServerConsumer> consumers = new ConcurrentHashMap<>();

  public Server()
  {
  }

  public IStore getStore()
  {
    return store;
  }

  public void setStore(IStore store)
  {
    this.store = store;
  }

  @Override
  public IDestination createDestination(String name, IDestination.Type type)
  {
    ServerDestination destination = new ServerDestination(name, type);
    ServerDestination existing = destinations.putIfAbsent(name, destination);
    if (existing != null)
    {
      throw new IllegalStateException("Destination " + type + " " + name + " does already exist"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    IStoreTransaction transaction = store.startTransaction();
    transaction.destinationAdded(destination);
    store.commitTransaction(transaction);

    try
    {
      destination.bind(jndiContext, REBIND_DESTINATIONS);
    }
    catch (NamingException ex)
    {
      OM.LOG.error(ex);
      destinations.remove(name);
      destination = null;
    }

    return destination;
  }

  public ServerDestination getDestination(String name)
  {
    return destinations.get(name);
  }

  public ServerDestination getServerDestination(Destination jmsDestination)
  {
    String name = ((JMSDestination)jmsDestination).getName();
    ServerDestination destination = destinations.get(name);
    if (destination == null)
    {
      OM.LOG.error(Messages.getString("Server.3") + name); //$NON-NLS-1$
      return null;
    }

    return destination;
  }

  @Override
  public ServerConnection logon(String userName, String password)
  {
    ServerConnection connection = new ServerConnection(this, userName);
    synchronized (connections)
    {
      connections.add(connection);
    }

    return connection;
  }

  public ServerConsumer createConsumer(ServerDestination destination, String messageSelector, boolean noLocal, boolean durable)
  {
    long consumerID = consumerIDCounter.increment();
    ServerConsumer consumer = new ServerConsumer(consumerID, destination, messageSelector, noLocal, durable);
    consumers.put(consumer.getID(), consumer);
    return consumer;
  }

  public ServerConsumer getConsumer(long consumerID)
  {
    return consumers.get(consumerID);
  }

  public String[] handleClientMessages(MessageImpl[] messages)
  {
    IStoreTransaction transaction = store.startTransaction();
    String[] messageIDs = handleClientMessagesInTransaction(transaction, messages);
    store.commitTransaction(transaction);
    return messageIDs;
  }

  public String[] handleClientMessagesInTransaction(IStoreTransaction transaction, MessageImpl[] messages)
  {
    String[] messageIDs = new String[messages.length];
    for (int i = 0; i < messages.length; i++)
    {
      MessageImpl message = messages[i];
      if (getServerDestination(message.getJMSDestination()) == null)
      {
        return null;
      }

      messageIDs[i] = "ID:NET4J:" + HexUtil.longToHex(messageIDCounter.increment()); //$NON-NLS-1$
      message.setJMSMessageID(messageIDs[i]);
    }

    for (MessageImpl message : messages)
    {
      transaction.messageReceived(message);
      addWork(message);
    }

    return messageIDs;
  }

  @Override
  protected String getThreadName()
  {
    return "jms-server"; //$NON-NLS-1$
  }

  @Override
  protected void work(WorkContext context, MessageImpl message)
  {
    ServerDestination destination = getServerDestination(message.getJMSDestination());
    IStoreTransaction transaction = store.startTransaction();
    destination.handleClientMessage(transaction, message);
    store.commitTransaction(transaction);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (store == null)
    {
      throw new IllegalStateException("store == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    String name = OMPlatform.INSTANCE.getProperty("net4j.jms.connection.factory.name", "net4j.jms.ConnectionFactory"); //$NON-NLS-1$ //$NON-NLS-2$
    String type = OMPlatform.INSTANCE.getProperty("net4j.jms.connector.type", "tcp"); //$NON-NLS-1$ //$NON-NLS-2$
    String desc = OMPlatform.INSTANCE.getProperty("net4j.jms.connector.description", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$

    jndiContext = new InitialContext();
    jndiContext.rebind(name, new ConnectionFactoryImpl(type, desc));
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (connections)
    {
      for (ServerConnection connection : connections)
      {
        try
        {
          connection.close();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    jndiContext.close();
    jndiContext = null;
    super.doDeactivate();
  }
}
