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
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IProtocol;
import org.eclipse.net4j.transport.IProtocolFactory;

import org.eclipse.internal.net4j.transport.ProtocolFactory;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class TestSignalProtocol extends SignalProtocol
{
  public static final String PROTOCOL_ID = "signal.protocol";

  public static final short SIGNAL1 = 1;

  public static final short SIGNAL2 = 2;

  public static final short SIGNAL3 = 3;

  public static final short SIGNAL4 = 4;

  public TestSignalProtocol(IChannel channel)
  {
    super(channel);
  }

  public String getProtocolID()
  {
    return PROTOCOL_ID;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case SIGNAL1:
      return new Indication1();
    case SIGNAL2:
      return new Indication2();
    }

    throw new IllegalArgumentException("Invalid signalID " + signalID);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ProtocolFactory
  {
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
      return new TestSignalProtocol(channel);
    }
  }
}
