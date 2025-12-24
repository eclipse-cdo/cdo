/*
 * Copyright (c) 2008-2012, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.io.IStreamWrapper;

/**
 * A {@link IProtocol protocol} that consists of a number of stream-based {@link Signal signals}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public interface ISignalProtocol<INFRA_STRUCTURE> extends IProtocol<INFRA_STRUCTURE>, INotifier
{
  public static final long NO_TIMEOUT = BufferInputStream.NO_TIMEOUT;

  public static final long DEFAULT_TIMEOUT = 10 * 1000L;

  public long getTimeout();

  public void setTimeout(long timeout);

  public IStreamWrapper getStreamWrapper();

  public void setStreamWrapper(IStreamWrapper streamWrapper);

  public void addStreamWrapper(IStreamWrapper streamWrapper);

  public IChannel open(IConnector connector);

  public void close();

  /**
   * @author Eike Stepper
   * @since 4.13
   */
  public interface WithSignalCounters<INFRA_STRUCTURE> extends ISignalProtocol<INFRA_STRUCTURE>
  {
    public long getSentSignals();

    public long getReceivedSignals();
  }
}
