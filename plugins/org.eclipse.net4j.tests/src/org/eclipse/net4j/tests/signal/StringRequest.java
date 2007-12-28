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

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class StringRequest extends RequestWithConfirmation<String>
{
  private String data;

  public StringRequest(IChannel channel, String data)
  {
    super(channel);
    this.data = data;
  }

  @Override
  protected short getSignalID()
  {
    return TestSignalProtocol.SIGNAL_STRING;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeString(data);
  }

  @Override
  protected String confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readString();
  }
}
