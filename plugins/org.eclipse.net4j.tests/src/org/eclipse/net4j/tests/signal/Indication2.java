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

import org.eclipse.net4j.signal.IndicationWithResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Indication2 extends IndicationWithResponse
{
  private byte[] data;

  @Override
  protected short getSignalID()
  {
    return TestSignalProtocol.SIGNAL2;
  }

  @Override
  protected void indicating(DataInputStream in) throws IOException
  {
    data = readByteArray();
  }

  @Override
  protected void responding(DataOutputStream out) throws IOException
  {
    writeByteArray(data);
  }
}
