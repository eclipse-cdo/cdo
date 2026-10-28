/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import java.util.Arrays;
import java.util.Collections;

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
    protocol.setTrustingPeer(false);

    try
    {
      new ExceptionRequest(protocol, phase, ioProblem).send();
      fail("Exception expected"); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);

      if (phase == 2 || phase == 3)
      {
        assertTrue("RemoteException expected", ex instanceof RemoteException); //$NON-NLS-1$
        RemoteException remoteException = (RemoteException)ex;
        assertEquals(phase == 3, remoteException.whileResponding());
        assertEquals(TestSignalProtocol.SIMULATED_EXCEPTION, remoteException.getMessage());

        if (ioProblem)
        {
          assertEquals(Collections.singletonList(IOException.class.getName()), remoteException.getExceptionNames());
        }
        else
        {
          assertEquals(Arrays.asList(ClassNotFoundException.class.getName(), AlreadyBoundException.class.getName()), remoteException.getExceptionNames());
        }

        return;
      }

      if (ioProblem)
      {
        IOException ioe = (IOException)ex;
        assertEquals(TestSignalProtocol.SIMULATED_EXCEPTION, ioe.getMessage());
      }
      else
      {
        ClassNotFoundException cnfe = (ClassNotFoundException)ex;
        assertEquals(TestSignalProtocol.SIMULATED_EXCEPTION, cnfe.getMessage());
        AlreadyBoundException abe = (AlreadyBoundException)cnfe.getCause();
        assertEquals(TestSignalProtocol.SIMULATED_EXCEPTION, abe.getMessage());
      }
    }
  }
}
