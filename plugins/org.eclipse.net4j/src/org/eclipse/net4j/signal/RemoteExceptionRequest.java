/*
 * Copyright (c) 2008-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
class RemoteExceptionRequest extends Request
{
  public static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, RemoteExceptionRequest.class);

  private final int correlationID;

  private final boolean responding;

  private final String message;

  private final String originalMessage;

  private final Throwable t;

  public RemoteExceptionRequest(SignalProtocol<?> protocol, int correlationID, boolean responding, String message, Throwable t)
  {
    this(protocol, correlationID, responding, message, RemoteException.getFirstLine(message), t);
  }

  public RemoteExceptionRequest(SignalProtocol<?> protocol, int correlationID, boolean responding, String message, String originalMessage, Throwable t)
  {
    super(protocol, SignalProtocol.SIGNAL_REMOTE_EXCEPTION);
    this.correlationID = correlationID;
    this.message = message;
    this.originalMessage = originalMessage;
    this.t = t;
    this.responding = responding;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing remote exception for signal {0}: {1}", correlationID, originalMessage); //$NON-NLS-1$
    }

    out.writeInt(correlationID);
    out.writeBoolean(responding);
    out.writeString(message);
    out.writeString(originalMessage);
    out.writeByteArray(ExtendedIOUtil.serializeThrowable(t));
  }
}
