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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
class ExceptionMessageRequest extends Request
{
  private int correlationID;

  private String message;

  public ExceptionMessageRequest(SignalProtocol<?> protocol, int correlationID, String message)
  {
    super(protocol);
    this.correlationID = correlationID;
    this.message = message;
  }

  @Override
  protected short getSignalID()
  {
    return SignalProtocol.SIGNAL_EXCEPTION_MESSAGE;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(correlationID);
    out.writeString(message);
  }
}
