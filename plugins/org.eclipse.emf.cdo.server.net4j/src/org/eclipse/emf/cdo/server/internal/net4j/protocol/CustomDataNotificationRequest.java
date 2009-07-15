/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CustomDataNotificationRequest extends CDOServerRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CustomDataNotificationRequest.class);

  private int senderID;

  private String type;

  private byte[] data;

  public CustomDataNotificationRequest(IChannel channel, InternalSession sender, String type, byte[] data)
  {
    super(channel, CDOProtocolConstants.SIGNAL_CUSTOM_DATA_NOTIFICATION);
    senderID = sender.getSessionID();
    this.type = type;
    this.data = data;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Writing senderID: " + senderID); //$NON-NLS-1$
    }

    out.writeInt(senderID);
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
}
