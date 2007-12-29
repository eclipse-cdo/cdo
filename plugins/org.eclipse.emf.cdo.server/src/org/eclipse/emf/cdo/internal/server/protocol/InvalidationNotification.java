/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class InvalidationNotification extends Request
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, InvalidationNotification.class);

  private long timeStamp;

  private CDOID[] dirtyIDs;

  public InvalidationNotification(IChannel channel, long timeStamp, CDOID[] dirtyIDs)
  {
    super(channel);
    this.timeStamp = timeStamp;
    this.dirtyIDs = dirtyIDs;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_INVALIDATION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing timeStamp: {0,date} {0,time}", timeStamp);
    }

    out.writeLong(timeStamp);

    int size = dirtyIDs.length;
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} IDs", size);
    }

    out.writeInt(size);
    for (int i = 0; i < dirtyIDs.length; i++)
    {
      CDOIDImpl.write(out, dirtyIDs[i]);
    }
  }
}
