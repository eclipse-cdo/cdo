/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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
public class PartialReadRequest extends RequestWithConfirmation<Boolean>
{
  public static final int DATA = 4711;

  public PartialReadRequest(TestSignalProtocol protocol)
  {
    super(protocol, TestSignalProtocol.SIGNAL_PARTIAL_READ);
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    // This is the only data PartialReadIndication will read.
    out.writeInt(DATA);

    // This will cause the current buffer to be sent.
    out.flush();

    // This will be interpreted as the signalID of a new incoming indication by SignalProtocol.handleBuffer().
    out.writeShort(Short.MAX_VALUE);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
