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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
class RemoteExceptionIndication extends Indication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, RemoteExceptionIndication.class);

  private Throwable t;

  public RemoteExceptionIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_REMOTE_EXCEPTION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int correlationID = in.readInt();
    boolean responding = in.readBoolean();
    String message = in.readString();
    if (TRACER.isEnabled())
    {
      String msg = RemoteExceptionRequest.getFirstLine(message);
      TRACER.format("Reading remote exception for signal {0}: {1}", correlationID, msg);
    }

    try
    {
      t = (Throwable)in.readObject(OM.class.getClassLoader());
    }
    catch (Throwable couldNotLoadExceptionClass)
    {
      t = new RemoteException(message, responding);
    }

    getProtocol().handleRemoteException(correlationID, t, responding);
  }
}
