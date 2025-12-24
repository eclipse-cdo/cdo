/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
