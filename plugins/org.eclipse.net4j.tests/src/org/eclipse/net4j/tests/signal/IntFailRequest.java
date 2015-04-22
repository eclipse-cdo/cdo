/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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
public class IntFailRequest extends RequestWithConfirmation<Integer>
{
  private int data;

  public IntFailRequest(TestSignalProtocol protocol, int data)
  {
    super(protocol, TestSignalProtocol.SIGNAL_INT_FAIL);
    this.data = data;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(data);
  }

  @Override
  protected Integer confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readInt();
  }
}
