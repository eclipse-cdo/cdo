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

import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
class ExceptionMessageIndication extends Indication
{
  public ExceptionMessageIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_EXCEPTION_MESSAGE);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    int correlationID = in.readInt();
    String message = in.readString();
    getProtocol().stopSignal(correlationID, message);
  }
}
