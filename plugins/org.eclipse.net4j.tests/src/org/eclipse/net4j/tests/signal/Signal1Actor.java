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

import org.eclipse.net4j.signal.SignalActor;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.util.stream.BufferInputStream;
import org.eclipse.net4j.util.stream.BufferOutputStream;

/**
 * @author Eike Stepper
 */
public class Signal1Actor extends SignalActor<byte[]>
{
  private byte[] data;

  public Signal1Actor(Channel channel, byte[] data)
  {
    super(channel);
    this.data = data;
  }

  @Override
  protected short getSignalID()
  {
    return TestSignalProtocol.SIGNAL1;
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    writeByteArray(data);
    out.flush();
    
    byte[] result = readByteArray();
    setResult(result);
  }
}
