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

/**
 * @author Eike Stepper
 */
class RemoteExceptionRequest extends Request
{
  private int correlationID;

  private String message;

  private Throwable t;

  public RemoteExceptionRequest(SignalProtocol<?> protocol, int correlationID, String message, Throwable t)
  {
    super(protocol, SignalProtocol.SIGNAL_REMOTE_EXCEPTION);
    this.correlationID = correlationID;
    this.message = message;
    this.t = t;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(correlationID);
    out.writeString(message);
    out.writeObject(t);
  }
}
