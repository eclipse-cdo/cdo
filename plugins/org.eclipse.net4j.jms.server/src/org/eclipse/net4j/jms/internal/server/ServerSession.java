/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server;

import org.eclipse.net4j.internal.jms.DestinationImpl;
import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.jms.internal.server.messages.Messages;
import org.eclipse.net4j.jms.server.ISession;
import org.eclipse.net4j.jms.server.IStore;
import org.eclipse.net4j.jms.server.IStoreTransaction;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class ServerSession extends Lifecycle implements ISession
{
  private ServerConnection connection;

  private int id;

  private ConcurrentMap<Long, ServerConsumer> consumers = new ConcurrentHashMap<>();

  public ServerSession(ServerConnection connection, int id)
  {
    this.connection = connection;
    this.id = id;
  }

  @Override
  public ServerConnection getConnection()
  {
    return connection;
  }

  @Override
  public int getID()
  {
    return id;
  }

  public long registerConsumer(DestinationImpl dest, String messageSelector, boolean noLocal, boolean durable)
  {
    Server server = connection.getServer();
    String name = dest.getName();
    ServerDestination destination = server.getDestination(name);
    if (destination == null)
    {
      OM.LOG.error(MessageFormat.format(Messages.getString("ServerSession_0"), name)); //$NON-NLS-1$
      return -1;
    }

    ServerConsumer consumer = server.createConsumer(destination, messageSelector, noLocal, durable);
    consumer.setSession(this);
    consumers.put(consumer.getID(), consumer);
    destination.addConsumer(consumer);
    return consumer.getID();
  }

  public void handleAcknowledge()
  {
    IStore store = connection.getServer().getStore();
    IStoreTransaction transaction = store.startTransaction();
    handleAcknowledgeInTransaction(transaction);
    store.commitTransaction(transaction);
  }

  public void handleAcknowledgeInTransaction(IStoreTransaction transaction)
  {
    for (ServerConsumer consumer : consumers.values())
    {
      consumer.handleAcknowledge(transaction);
    }
  }

  public void handleRecover()
  {
    IStore store = connection.getServer().getStore();
    IStoreTransaction transaction = store.startTransaction();
    Collection<ServerConsumer> values = consumers.values();
    for (ServerConsumer consumer : values)
    {
      consumer.handleRecover(transaction);
    }

    store.commitTransaction(transaction);
  }

  public String[] handleCommit(MessageImpl[] messages)
  {
    Server server = connection.getServer();
    IStore store = server.getStore();

    IStoreTransaction transaction = store.startTransaction();
    handleAcknowledgeInTransaction(transaction);
    String[] messageIDs = server.handleClientMessagesInTransaction(transaction, messages);
    store.commitTransaction(transaction);

    return messageIDs;
  }
}
