/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.signal.ExceptionRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.IOException;
import java.rmi.AlreadyBoundException;

/**
 * @author Eike Stepper
 */
public class ExceptionTest extends AbstractConfigTest
{
  public ExceptionTest()
  {
  }

  public void testExceptionInRequesting() throws Exception
  {
    exceptionInPhase(1, false);
  }

  public void testExceptionInIndicating() throws Exception
  {
    exceptionInPhase(2, false);
  }

  public void testExceptionInResponding() throws Exception
  {
    exceptionInPhase(3, false);
  }

  public void testExceptionInConfirming() throws Exception
  {
    exceptionInPhase(4, false);
  }

  public void testIOExceptionInIRequesting() throws Exception
  {
    exceptionInPhase(1, true);
  }

  public void testIOExceptionInIndicating() throws Exception
  {
    exceptionInPhase(2, true);
  }

  public void testIOExceptionInResponding() throws Exception
  {
    exceptionInPhase(3, true);
  }

  public void testIOExceptionInConfirming() throws Exception
  {
    exceptionInPhase(4, true);
  }

  private void exceptionInPhase(int phase, boolean ioProblem) throws Exception
  {
    startTransport();
    TestSignalProtocol protocol = new TestSignalProtocol(getConnector());

    try
    {
      new ExceptionRequest(protocol, phase, ioProblem).send();
      fail("Exception expected"); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
      if (ioProblem)
      {
        IOException ioe = null;
        if (phase == 2 || phase == 3)
        {
          if (ex instanceof RemoteException)
          {
            assertEquals(((RemoteException)ex).whileResponding(), phase == 3);
            ioe = (IOException)ex.getCause();
          }
          else
          {
            fail("RemoteException expected"); //$NON-NLS-1$
          }
        }
        else
        {
          ioe = (IOException)ex;
        }

        assertEquals(TestSignalProtocol.SIMULATED_EXCEPTION, ioe.getMessage());
      }
      else
      {
        ClassNotFoundException cnfe = null;
        if (phase == 2 || phase == 3)
        {
          if (ex instanceof RemoteException)
          {
            assertEquals(((RemoteException)ex).whileResponding(), phase == 3);
            cnfe = (ClassNotFoundException)ex.getCause();
          }
          else
          {
            fail("RemoteException expected"); //$NON-NLS-1$
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
}
