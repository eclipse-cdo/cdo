/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class InvalidationRequest extends Request
{
  private static final ContextTracer PROTOCOL = new ContextTracer(CDOProtocol.DEBUG_PROTOCOL, InvalidationRequest.class);

  private long timeStamp;

  private CDORevisionImpl[] dirtyObjects;

  public InvalidationRequest(IChannel channel, long timeStamp, CDORevisionImpl[] dirtyObjects)
  {
    super(channel);
    this.timeStamp = timeStamp;
    this.dirtyObjects = dirtyObjects;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.INVALIDATION_SIGNAL;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing timeStamp: {0,date} {0,time}", timeStamp);
    }

    out.writeLong(timeStamp);

    int size = dirtyObjects.length;
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} IDs", size);
    }

    out.writeInt(size);
    for (int i = 0; i < dirtyObjects.length; i++)
    {
      CDORevisionImpl dirty = dirtyObjects[i];
      CDOIDImpl.write(out, dirty.getID());
    }
  }
}
