/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server;

import org.eclipse.net4j.internal.jms.DestinationImpl;
import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.QueueImpl;
import org.eclipse.net4j.internal.jms.TopicImpl;
import org.eclipse.net4j.jms.server.IDestination;
import org.eclipse.net4j.jms.server.IStore;
import org.eclipse.net4j.jms.server.IStoreTransaction;
import org.eclipse.net4j.util.concurrent.RoundRobinList;

import javax.naming.Context;
import javax.naming.NamingException;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class ServerDestination implements IDestination
{
  private String name;

  private Type type;

  private RoundRobinList<ServerConsumer> consumers = new RoundRobinList<>();

  public ServerDestination(String name, Type type)
  {
    this.name = name;
    this.type = type;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Type getType()
  {
    return type;
  }

  public DestinationImpl bind(Context context, boolean rebind) throws NamingException
  {
    DestinationImpl destination = type == Type.QUEUE ? new QueueImpl(name) : new TopicImpl(name);
    if (rebind)
    {
      context.rebind(name, destination);
    }
    else
    {
      context.bind(name, destination);
    }

    return destination;
  }

  public boolean addConsumer(ServerConsumer consumer)
  {
    if (consumer.isDurable())
    {
      IStore store = Server.INSTANCE.getStore();
      IStoreTransaction transaction = store.startTransaction();
      transaction.consumerAdded(consumer);
      store.commitTransaction(transaction);
    }

    return consumers.add(consumer);
  }

  public boolean removeConsumer(final long consumerID)
  {
    final boolean[] modified = { false };
    consumers.executeWrites(new Runnable()
    {
      @Override
      public void run()
      {
        for (Iterator<ServerConsumer> it = consumers.iterator(); it.hasNext();)
        {
          ServerConsumer consumer = it.next();
          if (consumer.getID() == consumerID)
          {
            it.remove();
            modified[0] = true;
            return;
          }
        }
      }
    });

    return modified[0];
  }

  /**
   * Called by worker thread of the server
   */
  public void handleClientMessage(IStoreTransaction transaction, MessageImpl message)
  {
    if (type == Type.QUEUE)
    {
      ServerConsumer consumer = consumers.element();
      if (consumer != null)
      {
        consumer.handleClientMessage(transaction, message);
      }
    }
    else
    {
      ServerConsumer[] consumers = this.consumers.toArray(new ServerConsumer[0]);
      for (ServerConsumer consumer : consumers)
      {
        consumer.handleClientMessage(transaction, message);
      }
    }
  }
}
