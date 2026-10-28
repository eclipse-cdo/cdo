/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

/**
 * Provider SPI for creating a fresh asynchronous message connection for one owning Net4j connector.
 * <p>
 * Factories are registered in the managed container under {@link #PRODUCT_GROUP}; the factory type
 * selects a provider and the description supplies provider-specific connection input. A factory may
 * also hold shared configuration, such as an HTTP client or timeout. Each returned connection has
 * independent runtime state, is not started until its connector activates, and is owned by that
 * connector; the factory itself is owned by the container.
 *
 * @author Eike Stepper
 * @since 4.23
 */
public abstract class MessageConnectionFactory extends Factory
{
  /**
   * The product group used to register providers of {@link IMessageConnection} instances.
   */
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.messageConnections"; //$NON-NLS-1$

  /**
   * Creates a factory registered under the message-connection product group.
   *
   * @param type the provider name used as the factory type
   */
  protected MessageConnectionFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  /**
   * Creates an independent, not-yet-started message connection. Implementations may combine the
   * description with configuration shared by the factory, but the returned connection must have
   * independent runtime state from connections created for other connectors.
   *
   * @param description provider-specific connection configuration
   * @return a new connection owned by the requesting connector
   * @throws ProductCreationException if the configuration cannot be parsed or the connection cannot be created
   */
  @Override
  public abstract IMessageConnection create(String description) throws ProductCreationException;
}
