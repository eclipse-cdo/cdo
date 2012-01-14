/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class ExceptionRequest extends RequestWithConfirmation<Boolean>
{
  private int phase;

  private boolean ioProblem;

  public ExceptionRequest(TestSignalProtocol protocol, int phase, boolean ioProblem)
  {
    super(protocol, TestSignalProtocol.SIGNAL_EXCEPTION);
    this.phase = phase;
    this.ioProblem = ioProblem;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(phase);
    out.writeBoolean(ioProblem);
    if (phase == 1)
    {
      ((TestSignalProtocol)getProtocol()).throwException(ioProblem);
    }
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    if (phase == 4)
    {
      ((TestSignalProtocol)getProtocol()).throwException(ioProblem);
    }

    return in.readBoolean();
  }
}
