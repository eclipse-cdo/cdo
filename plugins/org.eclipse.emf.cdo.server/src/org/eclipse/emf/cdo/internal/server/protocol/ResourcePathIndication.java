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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResourcePathIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      ResourcePathIndication.class);

  private String path;

  public ResourcePathIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_RESOURCE_PATH;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read viewID: {0}", viewID);
    }

    CDOID id = in.readCDOID();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read ID: {0}", id);
    }

    IView view = getSession().getView(viewID);
    path = view.getResourcePath(id);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing path: {0}", path);
    }

    out.writeString(path);
  }
}
