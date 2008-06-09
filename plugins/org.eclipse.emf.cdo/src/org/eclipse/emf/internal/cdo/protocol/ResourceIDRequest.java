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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

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
    super(channel);
    this.path = path;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_RESOURCE_ID;
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
    CDOID id = CDOIDUtil.read(in, getSession());
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read ID: {0}", id);
    }

    return id;
  }
}
