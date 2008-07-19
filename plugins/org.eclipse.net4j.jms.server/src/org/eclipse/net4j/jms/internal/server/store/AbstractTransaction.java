/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.jms.internal.server.store;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.ServerConsumer;
import org.eclipse.net4j.jms.internal.server.ServerDestination;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.jms.server.IDestination;
import org.eclipse.net4j.jms.server.IServerConsumer;
import org.eclipse.net4j.jms.server.IStoreTransaction;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import javax.jms.Message;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTransaction implements IStoreTransaction
{
  public static final String[] NO_DESTINATION_NAMES = new String[0];

  public static final long[] NO_CONSUMER_IDS = new long[0];

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STORE, AbstractTransaction.class);

  private AbstractStore store;

  public AbstractTransaction(AbstractStore store)
  {
    this.store = store;
  }

  public AbstractStore getStore()
  {
    return store;
  }

  public String[] getDestinationNames()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Getting destination names");
    }

    String[] names = doGetDestinationNames();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Got destination names " + names);
    }

    return names;
  }

  public ServerDestination getDestination(String name)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Getting destination " + name);
    }

    ServerDestination destination = doGetDestination(name);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Got destination " + destination);
    }

    return destination;
  }

  public long[] getConsumerIDs()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Getting consumer IDs");
    }

    long[] ids = doGetConsumerIDs();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Got consumer IDs " + ids);
    }

    return ids;
  }

  public ServerConsumer getConsumer(long id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Getting consumer " + id);
    }

    ServerConsumer consumer = doGetConsumer(id);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Got consumer " + consumer);
    }

    return consumer;
  }

  public void destinationAdded(IDestination destination)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Adding destination " + destination);
    }

    doDestinationAdded((ServerDestination)destination);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Added destination " + destination);
    }
  }

  public void destinationRemoved(IDestination destination)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removing destination " + destination);
    }

    doDestinationRemoved((ServerDestination)destination);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed destination " + destination);
    }
  }

  public void consumerAdded(IServerConsumer consumer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Adding consumer " + consumer);
    }

    doConsumerAdded((ServerConsumer)consumer);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Added consumer " + consumer);
    }
  }

  public void consumerRemoved(IServerConsumer consumer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removing consumer " + consumer);
    }

    doConsumerRemoved((ServerConsumer)consumer);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed consumer " + consumer);
    }
  }

  public void messageReceived(Message message)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Logging received message " + message);
    }

    doMessageReceived((MessageImpl)message);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Logged received message " + message);
    }
  }

  public void messageSent(Message message, long consumerID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Logging sent message " + message);
    }

    doMessageSent((MessageImpl)message, consumerID);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Logged sent message " + message);
    }
  }

  public void messageAcknowledged(Message message, long consumerID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Logging acknowledged message " + message);
    }

    doMessageAcknowledged((MessageImpl)message, consumerID);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Logged acknowledged message " + message);
    }
  }

  protected abstract String[] doGetDestinationNames();

  protected abstract ServerDestination doGetDestination(String name);

  protected abstract long[] doGetConsumerIDs();

  protected abstract ServerConsumer doGetConsumer(long id);

  protected abstract void doDestinationAdded(ServerDestination destination);

  protected abstract void doDestinationRemoved(ServerDestination destination);

  protected abstract void doConsumerAdded(ServerConsumer consumer);

  protected abstract void doConsumerRemoved(ServerConsumer consumer);

  protected abstract void doMessageReceived(MessageImpl message);

  protected abstract void doMessageSent(MessageImpl message, long consumerID);

  protected abstract void doMessageAcknowledged(MessageImpl message, long consumerID);
}
