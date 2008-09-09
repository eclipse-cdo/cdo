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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassRef;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class QueryObjectTypesRequest extends CDOClientRequest<CDOClassRef[]>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      QueryObjectTypesRequest.class);

  private List<CDOID> ids;

  public QueryObjectTypesRequest(IChannel channel, List<CDOID> ids)
  {
    super(channel);
    this.ids = ids;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY_OBJECT_TYPES;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} IDs", ids.size());
    }

    out.writeInt(ids.size());
    for (CDOID id : ids)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Writing ID: {0}", id);
      }

      out.writeCDOID(id);
    }
  }

  @Override
  protected CDOClassRef[] confirming(CDODataInput in) throws IOException
  {
    CDOClassRef[] types = new CDOClassRef[ids.size()];
    for (int i = 0; i < types.length; i++)
    {
      types[i] = in.readCDOClassRef();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Read type: {0}", types[i]);
      }
    }

    return types;
  }
}
