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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RepositoryTimeResult;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOTimeRequest<RESULT> extends CDOClientRequest<RESULT>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDOTimeRequest.class);

  private RepositoryTimeResult repositoryTimeResult = new RepositoryTimeResult();

  public CDOTimeRequest(CDOClientProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  public RepositoryTimeResult getRepositoryTimeResult()
  {
    return repositoryTimeResult;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    repositoryTimeResult.setRequested(System.currentTimeMillis());
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Requested: {0,date} {0,time}", repositoryTimeResult.getRequested());
    }
  }

  @Override
  protected RESULT confirming(CDODataInput in) throws IOException
  {
    repositoryTimeResult.setConfirmed(System.currentTimeMillis());
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Confirmed: {0,date} {0,time}", repositoryTimeResult.getConfirmed());
    }

    repositoryTimeResult.setIndicated(in.readLong());
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read indicated: {0,date} {0,time}", repositoryTimeResult.getIndicated());
    }

    repositoryTimeResult.setResponded(in.readLong());
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read responded: {0,date} {0,time}", repositoryTimeResult.getResponded());
    }

    return null;
  }
}
