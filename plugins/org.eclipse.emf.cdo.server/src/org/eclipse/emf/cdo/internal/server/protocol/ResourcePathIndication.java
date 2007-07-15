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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResourcePathIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, ResourcePathIndication.class);

  private String path;

  public ResourcePathIndication()
  {
    super(CDOProtocolConstants.RESOURCE_PATH_SIGNAL);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    final CDOID id = CDOIDImpl.read(in);
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read ID: {0}", id);
    }

    transact(new Runnable()
    {
      public void run()
      {
        path = getResourceManager().getResourcePath(id);
      }
    });
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing path: {0}", path);
    }

    // TODO Optimize transfer of URIs/paths
    out.writeString(path);
  }
}
