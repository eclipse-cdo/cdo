/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.Connector.Type;

import org.eclipse.internal.net4j.transport.AbstractProtocol;
import org.eclipse.internal.net4j.transport.AbstractProtocolFactory;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class TestProtocolFactory extends AbstractProtocolFactory implements ProtocolFactory
{
  public static final String PROTOCOL_ID = "test.protocol";

  private CountDownLatch counter;

  public TestProtocolFactory(CountDownLatch counter)
  {
    this.counter = counter;
  }

  public String getID()
  {
    return PROTOCOL_ID;
  }

  public Set<Type> getConnectorTypes()
  {
    return ProtocolFactory.SYMMETRIC;
  }

  public Protocol createProtocol(Channel channel)
  {
    return new TestProtocol(channel);
  }

  /**
   * @author Eike Stepper
   */
  private final class TestProtocol extends AbstractProtocol
  {
    public TestProtocol(Channel channel)
    {
      super(channel);
    }

    public String getProtocolID()
    {
      return PROTOCOL_ID;
    }

    public void handleBuffer(Buffer buffer)
    {
      System.out.println("BUFFER ARRIVED");
      buffer.release();
      counter.countDown();
    }
  }
}