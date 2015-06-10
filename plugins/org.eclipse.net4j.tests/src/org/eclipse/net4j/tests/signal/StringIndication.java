/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
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
public class StringIndication extends IndicationWithResponse
{
  private String data;

  public StringIndication(TestSignalProtocol protocol)
  {
    super(protocol, TestSignalProtocol.SIGNAL_STRING);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    data = in.readString();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(data);
  }
}
