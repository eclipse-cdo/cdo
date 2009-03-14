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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class LoadRevisionByTimeRequest extends LoadRevisionRequest
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionByTimeRequest.class);

  private long timeStamp;

  public LoadRevisionByTimeRequest(CDOClientProtocol protocol, Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_TIME, ids, referenceChunk);
    this.timeStamp = timeStamp;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    super.requesting(out);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing timeStamp: {0}", timeStamp);
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
