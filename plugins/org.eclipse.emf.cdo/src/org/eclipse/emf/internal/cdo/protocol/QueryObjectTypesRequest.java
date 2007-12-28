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

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class QueryObjectTypesRequest extends CDOClientRequest<CDOClassRef[]>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, QueryObjectTypesRequest.class);

  /**
   * TODO Consider single id
   */
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
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} IDs", ids.size());
    }
    out.writeInt(ids.size());

    for (CDOID id : ids)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing ID: {0}", id);
      }
      CDOIDImpl.write(out, id);
    }
  }

  @Override
  protected CDOClassRef[] confirming(ExtendedDataInputStream in) throws IOException
  {
    CDOClassRef[] types = new CDOClassRef[ids.size()];
    for (int i = 0; i < types.length; i++)
    {
      types[i] = new CDOClassRefImpl(in, null);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read type: {0}", types[i]);
      }
    }

    return types;
  }
}
