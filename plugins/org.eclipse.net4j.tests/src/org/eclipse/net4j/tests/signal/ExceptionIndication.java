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

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.rmi.AlreadyBoundException;

/**
 * @author Eike Stepper
 */
public class ExceptionIndication extends IndicationWithResponse
{
  public static final String SIMULATED_EXCEPTION = "Simulated exception";

  private boolean exceptionInIndicating;

  public ExceptionIndication(TestSignalProtocol protocol)
  {
    super(protocol, TestSignalProtocol.SIGNAL_EXCEPTION);
  }

  public boolean isExceptionInIndicating()
  {
    return exceptionInIndicating;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    exceptionInIndicating = in.readBoolean();
    if (exceptionInIndicating)
    {
      throwException();
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    if (!exceptionInIndicating)
    {
      throwException();
    }

    out.writeBoolean(true);
  }

  private void throwException() throws Exception
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

  private void throwNestedException() throws Exception
  {
    throw new AlreadyBoundException(SIMULATED_EXCEPTION);
  }
}
