/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
public class PartialReadIndication extends IndicationWithResponse
{
  public PartialReadIndication(TestSignalProtocol protocol)
  {
    super(protocol, TestSignalProtocol.SIGNAL_INT);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int data = in.readInt();
    if (data != PartialReadRequest.DATA)
    {
      throw new Error("Read " + data + " instead of " + PartialReadRequest.DATA);
    }

    // Do not read the remaining 2 bytes here that PartialReadRequest has sent!
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(true);
  }
}
