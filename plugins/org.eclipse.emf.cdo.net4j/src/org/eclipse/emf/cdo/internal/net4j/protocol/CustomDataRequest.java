/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CustomDataRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CustomDataRequest.class);

  private int receiverID;

  private String type;

  private byte[] data;

  public CustomDataRequest(CDOClientProtocol protocol, CDORemoteSession receiver, String type, byte[] data)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CUSTOM_DATA);
    receiverID = receiver.getSessionID();
    this.type = type;
    this.data = data;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing receiverID: " + receiverID); //$NON-NLS-1$
    }

    out.writeInt(receiverID);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing type: " + type); //$NON-NLS-1$
    }

    out.writeString(type);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing data: " + HexUtil.bytesToHex(data)); //$NON-NLS-1$
    }

    out.writeByteArray(data);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
