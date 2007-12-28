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
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResourcePathRequest extends CDOClientRequest<String>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, ResourcePathRequest.class);

  private CDOID id;

  public ResourcePathRequest(IChannel channel, CDOID id)
  {
    super(channel);
    this.id = id;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_RESOURCE_PATH;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing ID: {0}", id);
    }

    CDOIDImpl.write(out, id);
  }

  @Override
  protected String confirming(ExtendedDataInputStream in) throws IOException
  {
    // TODO Optimize transfer of URIs/paths
    String path = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read path: {0}", path);
    }

    return path;
  }
}
