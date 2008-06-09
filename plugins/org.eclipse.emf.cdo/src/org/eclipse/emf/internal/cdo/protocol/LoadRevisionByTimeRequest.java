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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByTimeRequest extends LoadRevisionRequest
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionByTimeRequest.class);

  private long timeStamp;

  public LoadRevisionByTimeRequest(IChannel channel, Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    super(channel, ids, referenceChunk);
    this.timeStamp = timeStamp;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_TIME;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    super.requesting(out);
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing timeStamp: {0}", timeStamp);
    }
    out.writeLong(timeStamp);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}(ids={1}, referenceChunk={2}, timeStamp={3})", getClass().getSimpleName(),
        getIds(), getReferenceChunk(), timeStamp);
  }
}
