/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Request2 extends RequestWithConfirmation<byte[]>
{
  private byte[] data;

  public Request2(Channel channel, byte[] data)
  {
    super(channel);
    this.data = data;
  }

  @Override
  protected short getSignalID()
  {
    return TestSignalProtocol.SIGNAL2;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeByteArray(data);
  }

  @Override
  protected byte[] confirming(ExtendedDataInputStream in) throws IOException
  {
    return in.readByteArray();
  }
}
