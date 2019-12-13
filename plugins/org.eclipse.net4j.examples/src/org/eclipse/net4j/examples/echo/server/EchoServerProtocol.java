/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.examples.echo.server;

import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoServerProtocol extends SignalProtocol<Object> implements EchoProtocol
{
  public EchoServerProtocol()
  {
    super(PROTOCOL_NAME);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ECHO_SIGNAL:
      return new EchoIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends ServerProtocolFactory
  {
    public Factory()
    {
      super(PROTOCOL_NAME);
    }

    @Override
    public Object create(String description) throws ProductCreationException
    {
      return new EchoServerProtocol();
    }
  }
}
