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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResourceIDRequest extends CDOClientRequest<CDOID>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, ResourceIDRequest.class);

  private String path;

  public ResourceIDRequest(IChannel channel, String path)
  {
    super(channel, CDOProtocolConstants.RESOURCE_ID_SIGNAL);
    this.path = path;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing path: {0}", path);
    }

    // TODO Optimize transfer of URIs/paths
    out.writeString(path);
  }

  @Override
  protected CDOID confirming(ExtendedDataInputStream in) throws IOException
  {
    CDOID id = CDOIDImpl.read(in);
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read ID: {0}", id);
    }

    return id;
  }
}
