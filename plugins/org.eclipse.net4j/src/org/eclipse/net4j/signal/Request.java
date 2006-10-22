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
package org.eclipse.net4j.signal;

import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class Request extends Signal
{
  protected Request(SignalProtocol protocol)
  {
    setProtocol(protocol);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  public Object send() throws Exception
  {
    return getProtocol().sendRequest(this);
  }

  @Override
  public String toString()
  {
    return "Request[" + getSignalID() + ", " + getProtocol() + ", correlation="
        + getCorrelationID() + "]";
  }

  protected abstract void requesting(OutputStream stream) throws Exception;
}
