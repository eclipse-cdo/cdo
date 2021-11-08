/*
 * Copyright (c) 2008-2013, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;

/**
 * @author Eike Stepper
 */
class RemoteExceptionIndication extends Indication
{
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

    try
    {
      t = ExtendedIOUtil.deserializeThrowable(in.readByteArray());
    }
    catch (Throwable couldNotLoadExceptionClass)
    {
      // Fall through
    }

    if (t == null)
    {
      t = new RemoteException(message, responding);
    }

    SignalProtocol<?> protocol = getProtocol();
    if (protocol != null)
    {
      protocol.handleRemoteException(correlationID, t, responding);
    }
  }
}
