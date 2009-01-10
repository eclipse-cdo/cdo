/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.io.IStreamWrapper;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface ISignalProtocol<INFRA_STRUCTURE> extends IProtocol<INFRA_STRUCTURE>
{
  public static final long NO_TIMEOUT = BufferInputStream.NO_TIMEOUT;

  public long getTimeout();

  public void setTimeout(long timeout);

  public IStreamWrapper getStreamWrapper();

  public void setStreamWrapper(IStreamWrapper streamWrapper);

  public void addStreamWrapper(IStreamWrapper streamWrapper);

  public IFailOverStrategy getFailOverStrategy();

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy);

  public IChannel open(IFailOverStrategy failOverStrategy);

  public IChannel open(IConnector connector);

  public IChannel open();

  public void close();
}
