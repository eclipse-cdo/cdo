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

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryTimeRequest<RESULT> extends CDOClientRequest<RESULT>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, RepositoryTimeRequest.class);

  private long requested;

  private long indicated;

  private long responded;

  private long confirmed;

  public RepositoryTimeRequest(IChannel channel)
  {
    super(channel);
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_REPOSITORY_TIME;
  }

  public long getRequested()
  {
    return requested;
  }

  public long getIndicated()
  {
    return indicated;
  }

  public long getResponded()
  {
    return responded;
  }

  public long getConfirmed()
  {
    return confirmed;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    requested = System.currentTimeMillis();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Requested: {0,date} {0,time}", requested);
    }
  }

  @Override
  protected RESULT confirming(CDODataInput in) throws IOException
  {
    confirmed = System.currentTimeMillis();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Confirmed: {0,date} {0,time}", confirmed);
    }

    indicated = in.readLong();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read indicated: {0,date} {0,time}", indicated);
    }

    responded = in.readLong();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read responded: {0,date} {0,time}", responded);
    }

    return null;
  }
}
