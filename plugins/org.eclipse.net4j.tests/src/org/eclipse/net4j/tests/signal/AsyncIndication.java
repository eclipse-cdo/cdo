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

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class AsyncIndication extends Indication
{
  private String data;

  public AsyncIndication(SignalProtocol<?> protocol)
  {
    super(protocol, TestSignalProtocol.SIGNAL_ASYNC);
  }

  public String getData()
  {
    return data;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    data = in.readString();
  }
}
