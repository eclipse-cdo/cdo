/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class ExceptionIndication extends IndicationWithResponse
{
  private int phase;

  private boolean ioProblem;

  public ExceptionIndication(TestSignalProtocol protocol)
  {
    super(protocol, TestSignalProtocol.SIGNAL_EXCEPTION);
  }

  public int getPhase()
  {
    return phase;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    phase = in.readInt();
    ioProblem = in.readBoolean();
    if (phase == 2)
    {
      ((TestSignalProtocol)getProtocol()).throwException(ioProblem);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    if (phase == 3)
    {
      ((TestSignalProtocol)getProtocol()).throwException(ioProblem);
    }

    out.writeBoolean(true);
  }
}
