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
import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ResourceIDIndication extends CDOServerIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(CDOProtocol.DEBUG_PROTOCOL,
      ResourceIDIndication.class);

  private CDOID id;

  public ResourceIDIndication()
  {
    super(CDOProtocolConstants.RESOURCE_ID_SIGNAL);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    // TODO Optimize transfer of URIs/paths
    final String path = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read path: {0}", path);
    }

    try
    {
      transact(new Runnable()
      {
        public void run()
        {
          id = getResourceManager().getResourceID(path);
        }
      });
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      id = CDOID.NULL;
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing ID: {0}", id);
    }

    CDOIDImpl.write(out, id);
  }
}
