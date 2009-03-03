/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.internal.net4j;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.util.security.INegotiator;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class TransportConfig implements ITransportConfig
{
  private IBufferProvider bufferProvider;

  /**
   * An optional executor to be used by the {@link IChannel}s to process their receive queues instead of the current
   * thread. If not <code>null</code> the sender and the receiver peers become decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  private IProtocolProvider protocolProvider;

  private INegotiator negotiator;

  public TransportConfig()
  {
  }

  public TransportConfig(ExecutorService receiveExecutor, IBufferProvider bufferProvider,
      IProtocolProvider protocolProvider, INegotiator negotiator)
  {
    this.receiveExecutor = receiveExecutor;
    this.bufferProvider = bufferProvider;
    this.protocolProvider = protocolProvider;
    this.negotiator = negotiator;
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public IProtocolProvider getProtocolProvider()
  {
    return protocolProvider;
  }

  public void setProtocolProvider(IProtocolProvider protocolProvider)
  {
    this.protocolProvider = protocolProvider;
  }

  public INegotiator getNegotiator()
  {
    return negotiator;
  }

  public void setNegotiator(INegotiator negotiator)
  {
    this.negotiator = negotiator;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format(
        "TransportConfig[receiveExecutor={0}, bufferProvider={1}, protocolProvider={2}, negotiator={3}]",
        receiveExecutor, bufferProvider, protocolProvider, negotiator);
  }
}
