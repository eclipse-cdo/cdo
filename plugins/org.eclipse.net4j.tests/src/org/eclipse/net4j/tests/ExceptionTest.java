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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.SignalRemoteException;
import org.eclipse.net4j.tests.signal.ExceptionIndication;
import org.eclipse.net4j.tests.signal.ExceptionRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;

/**
 * @author Eike Stepper
 */
public class ExceptionTest extends AbstractProtocolTest
{
  public ExceptionTest()
  {
  }

  public void testExceptionInIndicating() throws Exception
  {
    run(true);
  }

  public void testExceptionInResponding() throws Exception
  {
    run(false);
  }

  private void run(boolean exceptionInIndicating) throws Exception
  {
    IConnector connector = startTransport();
    TestSignalProtocol protocol = new TestSignalProtocol(connector);
    protocol.open(connector);

    try
    {
      new ExceptionRequest(protocol, exceptionInIndicating).send();
      fail("SignalRemoteException expected");
    }
    catch (SignalRemoteException success)
    {
      assertEquals(ExceptionIndication.SIMULATED_EXCEPTION, success.getMessage());
    }
  }
}
