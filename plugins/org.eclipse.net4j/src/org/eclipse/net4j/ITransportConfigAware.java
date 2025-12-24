/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IConnector;

/**
 * A concept that has a {@link ITransportConfig transport configuration}, typically a {@link IChannelMultiplexer channel
 * multiplexer}, {@link IConnector connector} or {@link IAcceptor acceptor}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface ITransportConfigAware
{
  /**
   * Returns the current transport configuration if there is one, a new empty one otherwise.
   */
  public ITransportConfig getConfig();

  /**
   * Sets a new transport configuration by <b>copying</b> the given one.
   */
  public void setConfig(ITransportConfig config);
}
