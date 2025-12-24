/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server.internal.jdbc;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.ServerConsumer;
import org.eclipse.net4j.jms.internal.server.ServerDestination;
import org.eclipse.net4j.jms.internal.server.store.AbstractStore;
import org.eclipse.net4j.jms.internal.server.store.AbstractTransaction;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class JDBCTransaction extends AbstractTransaction
{
  private Connection connection;

  public JDBCTransaction(AbstractStore store, Connection connection)
  {
    super(store);
    this.connection = connection;
  }

  public Connection getConnection()
  {
    return connection;
  }

  public void dispose()
  {
    connection = null;
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
