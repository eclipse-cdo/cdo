/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IProtocol;
import org.eclipse.net4j.transport.IProtocolFactory;

import org.eclipse.internal.net4j.transport.Protocol;
import org.eclipse.internal.net4j.transport.ProtocolFactory;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class TestProtocolFactory extends ProtocolFactory implements IProtocolFactory
{
  public static final String PROTOCOL_ID = "test.protocol";

  private CountDownLatch counter;

  public TestProtocolFactory(CountDownLatch counter)
  {
    this.counter = counter;
  }

  public String getProtocolID()
  {
    return PROTOCOL_ID;
  }

  public Set<ConnectorLocation> getLocations()
  {
    return IProtocolFactory.SYMMETRIC;
  }

  public IProtocol createProtocol(IChannel channel, Object protocolData)
  {
    return new TestProtocol(channel);
  }

  /**
   * @author Eike Stepper
   */
  private final class TestProtocol extends Protocol
  {
    public TestProtocol(IChannel channel)
    {
      super(channel);
    }

    public String getProtocolID()
    {
      return PROTOCOL_ID;
    }

    public void handleBuffer(IBuffer buffer)
    {
      System.out.println("BUFFER ARRIVED");
      buffer.release();
      counter.countDown();
    }
  }
}