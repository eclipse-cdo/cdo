/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server.store;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.ServerConsumer;
import org.eclipse.net4j.jms.internal.server.ServerDestination;

/**
 * @author Eike Stepper
 */
public class NOOPTransaction extends AbstractTransaction
{
  public NOOPTransaction(AbstractStore store)
  {
    super(store);
  }

  @Override
  protected String[] doGetDestinationNames()
  {
    return NO_DESTINATION_NAMES;
  }

  @Override
  protected ServerDestination doGetDestination(String name)
  {
    return null;
  }

  @Override
  protected long[] doGetConsumerIDs()
  {
    return NO_CONSUMER_IDS;
  }

  @Override
  protected ServerConsumer doGetConsumer(long id)
  {
    return null;
  }

  @Override
  protected void doDestinationAdded(ServerDestination destination)
  {
  }

  @Override
  protected void doDestinationRemoved(ServerDestination destination)
  {
  }

  @Override
  protected void doConsumerAdded(ServerConsumer consumer)
  {
  }

  @Override
  protected void doConsumerRemoved(ServerConsumer consumer)
  {
  }

  @Override
  protected void doMessageReceived(MessageImpl message)
  {
  }

  @Override
  protected void doMessageSent(MessageImpl message, long consumerID)
  {
  }

  @Override
  protected void doMessageAcknowledged(MessageImpl message, long consumerID)
  {
  }
}
