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
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.tests.signal.ExceptionRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.io.IOUtil;

import java.rmi.AlreadyBoundException;

/**
 * @author Eike Stepper
 */
public class ExceptionTest extends AbstractProtocolTest
{
  public ExceptionTest()
  {
  }

  public void testExceptionInIRequesting() throws Exception
  {
    exceptionInPhase(1);
  }

  public void testExceptionInIndicating() throws Exception
  {
    exceptionInPhase(2);
  }

  public void testExceptionInResponding() throws Exception
  {
    exceptionInPhase(3);
  }

  public void testExceptionInConfirming() throws Exception
  {
    exceptionInPhase(4);
  }

  private void exceptionInPhase(int phase) throws Exception
  {
    IConnector connector = startTransport();
    TestSignalProtocol protocol = new TestSignalProtocol(connector);
    protocol.open(connector);

    try
    {
      new ExceptionRequest(protocol, phase).send();
      fail("Exception expected");
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
      ClassNotFoundException cnfe = null;
      if (phase == 2 || phase == 3)
      {
        if (ex instanceof RemoteException)
        {
          cnfe = (ClassNotFoundException)ex.getCause();
        }
        else
        {
          fail("RemoteException expected");
        }
      }
      else
      {
        cnfe = (ClassNotFoundException)ex;
      }

      AlreadyBoundException abe = (AlreadyBoundException)cnfe.getCause();
      assertEquals(TestSignalProtocol.SIMULATED_EXCEPTION, abe.getMessage());
    }
  }
}
