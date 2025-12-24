/*
 * Copyright (c) 2008-2013, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;

/**
 * @author Eike Stepper
 */
class RemoteExceptionRequest extends Request
{
  private final int correlationID;

  private final boolean responding;

  private final String message;

  private final Throwable t;

  public RemoteExceptionRequest(SignalProtocol<?> protocol, int correlationID, boolean responding, String message, Throwable t)
  {
    super(protocol, SignalProtocol.SIGNAL_REMOTE_EXCEPTION);
    this.correlationID = correlationID;
    this.message = message;
    this.t = t;
    this.responding = responding;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(correlationID);
    out.writeBoolean(responding);
    out.writeString(message);
    out.writeByteArray(ExtendedIOUtil.serializeThrowable(t));
  }
}
