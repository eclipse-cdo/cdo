/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class ArrayRequest extends RequestWithConfirmation<byte[]>
{
  private byte[] data;

  private boolean flush;

  public ArrayRequest(SignalProtocol<?> protocol, byte[] data)
  {
    this(protocol, data, false);
  }

  public ArrayRequest(SignalProtocol<?> protocol, byte[] data, boolean flush)
  {
    super(protocol, TestSignalProtocol.SIGNAL_ARRAY);
    this.data = data;
    this.flush = flush;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeByteArray(data);

    if (flush)
    {
      out.flush();
    }
  }

  @Override
  protected byte[] confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readByteArray();
  }
}
