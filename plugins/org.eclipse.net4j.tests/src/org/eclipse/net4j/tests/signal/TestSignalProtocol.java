/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.rmi.AlreadyBoundException;

/**
 * @author Eike Stepper
 */
public class TestSignalProtocol extends SignalProtocol<Object>
{
  public static final String PROTOCOL_NAME = "signal.protocol";

  public static final short SIGNAL_INT = 1;

  public static final short SIGNAL_INT_FAIL = 2;

  public static final short SIGNAL_ARRAY = 3;

  public static final short SIGNAL_STRING = 4;

  public static final short SIGNAL_ASYNC = 5;

  public static final short SIGNAL_EXCEPTION = 6;

  public static final String SIMULATED_EXCEPTION = "Simulated exception";

  public TestSignalProtocol(IConnector connector)
  {
    open(connector);
  }

  private TestSignalProtocol()
  {
  }

  public String getType()
  {
    return PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case SIGNAL_INT:
      return new IntIndication(this);

    case SIGNAL_INT_FAIL:
      return new IntFailIndication(this);

    case SIGNAL_ARRAY:
      return new ArrayIndication(this);

    case SIGNAL_STRING:
      return new StringIndication(this);

    case SIGNAL_ASYNC:
      return new AsyncIndication(this);

    case SIGNAL_EXCEPTION:
      return new ExceptionIndication(this);

    default:
      return null;
    }
  }

  public void throwException() throws Exception
  {
    try
    {
      throwNestedException();
    }
    catch (Exception ex)
    {
      throw new ClassNotFoundException(SIMULATED_EXCEPTION, ex);
    }
  }

  public void throwNestedException() throws Exception
  {
    throw new AlreadyBoundException(SIMULATED_EXCEPTION);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ServerProtocolFactory
  {
    public Factory()
    {
      super(PROTOCOL_NAME);
    }

    public TestSignalProtocol create(String description) throws ProductCreationException
    {
      return new TestSignalProtocol();
    }
  }
}
