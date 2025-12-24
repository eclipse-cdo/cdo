/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server;

import javax.jms.Message;

/**
 * @author Eike Stepper
 */
public interface IStoreTransaction
{
  public String[] getDestinationNames();

  /**
   * @since 2.0
   */
  public IDestination getDestination(String name);

  public long[] getConsumerIDs();

  /**
   * @since 2.0
   */
  public IServerConsumer getConsumer(long id);

  /**
   * @since 2.0
   */
  public void destinationAdded(IDestination destination);

  /**
   * @since 2.0
   */
  public void destinationRemoved(IDestination destination);

  /**
   * @since 2.0
   */
  public void consumerAdded(IServerConsumer consumer);

  /**
   * @since 2.0
   */
  public void consumerRemoved(IServerConsumer consumer);

  /**
   * @since 2.0
   */
  public void messageReceived(Message message);

  /**
   * @since 2.0
   */
  public void messageSent(Message message, long consumerID);

  /**
   * @since 2.0
   */
  public void messageAcknowledged(Message message, long consumerID);
}
