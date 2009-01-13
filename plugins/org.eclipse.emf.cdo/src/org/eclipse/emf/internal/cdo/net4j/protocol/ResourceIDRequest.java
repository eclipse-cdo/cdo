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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResourceIDRequest extends CDOClientRequest<CDOID>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, ResourceIDRequest.class);

  private int viewID;

  private String path;

  public ResourceIDRequest(CDOClientProtocol protocol, int viewID, String path)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_RESOURCE_ID);
    this.viewID = viewID;
    this.path = path;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing viewID: {0}", viewID);
    }

    out.writeInt(viewID);
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing path: {0}", path);
    }

    out.writeString(path);
  }

  @Override
  protected CDOID confirming(CDODataInput in) throws IOException
  {
    CDOID id = in.readCDOID();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read ID: {0}", id);
    }

    return id;
  }
}
