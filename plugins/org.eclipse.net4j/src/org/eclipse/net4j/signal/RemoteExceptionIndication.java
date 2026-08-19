/*
 * Copyright (c) 2008-2013, 2021, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
class RemoteExceptionIndication extends Indication
{
  public RemoteExceptionIndication(SignalProtocol<?> protocol)
  {
    super(protocol, SignalProtocol.SIGNAL_REMOTE_EXCEPTION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int correlationID = in.readVarInt();
    boolean responding = in.readBoolean();

    int exceptionNameCount = in.readVarInt();
    List<String> exceptionNames = new ArrayList<>(exceptionNameCount);

    for (int i = 0; i < exceptionNameCount; i++)
    {
      exceptionNames.add(in.readString());
    }

    String message = in.readString();
    String remoteStackTrace = in.readString();
    byte[] throwableBytes = in.readByteArray();

    SignalProtocol<?> protocol = getProtocol();
    if (protocol != null)
    {
      boolean trustingPeer = protocol.isTrustingPeer();

      Throwable t = deserializeThrowable(throwableBytes, trustingPeer);
      if (t == null)
      {
        t = new RemoteException(message, exceptionNames, remoteStackTrace, responding);
      }

      protocol.handleRemoteException(correlationID, t, responding);
    }
  }

  private Throwable deserializeThrowable(byte[] throwableBytes, boolean trustingPeer)
  {
    if (trustingPeer)
    {
      try
      {
        return ExtendedIOUtil.deserializeThrowable(throwableBytes);
      }
      catch (Throwable couldNotLoadExceptionClass)
      {
        // Fall through
      }
    }

    return null;
  }
}
