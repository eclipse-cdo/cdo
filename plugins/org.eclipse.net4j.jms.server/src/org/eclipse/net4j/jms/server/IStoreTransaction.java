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
package org.eclipse.net4j.jms.server;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.jms.internal.server.ServerConsumer;
import org.eclipse.net4j.jms.internal.server.ServerDestination;

/**
 * @author Eike Stepper
 */
public interface IStoreTransaction
{
  public String[] getDestinationNames();

  public ServerDestination getDestination(String name);

  public long[] getConsumerIDs();

  public ServerConsumer getConsumer(long id);

  public void destinationAdded(ServerDestination destination);

  public void destinationRemoved(ServerDestination destination);

  public void consumerAdded(ServerConsumer consumer);

  public void consumerRemoved(ServerConsumer consumer);

  public void messageReceived(MessageImpl message);

  public void messageSent(MessageImpl message, long consumerID);

  public void messageAcknowledged(MessageImpl message, long consumerID);
}
