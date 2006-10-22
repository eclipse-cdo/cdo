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

import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.stream.BufferInputStream;
import org.eclipse.net4j.util.stream.BufferOutputStream;

/**
 * @author Eike Stepper
 */
public class Signal1Reactor extends SignalReactor
{
  @Override
  protected short getSignalID()
  {
    return TestSignalProtocol.SIGNAL1;
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    byte[] data = readByteArray();

    writeByteArray(data);
    out.flush();
  }
}
