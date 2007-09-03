/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionIndication.class);

  protected CDOID id;

  protected int referenceChunk;

  public LoadRevisionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_REVISION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    id = CDOIDImpl.read(in);
    if (id.isTemporary())
    {
      id = CDOIDImpl.create(-id.getValue());
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read ID: {0}", id);
      }

      referenceChunk = in.readInt();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read referenceChunk: {0}", referenceChunk);
      }
    }
    else
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read ID: {0}", id);
      }

      referenceChunk = CDORevision.UNCHUNKED;
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    CDORevisionImpl revision = getRevision();
    revision.write(out, getSession(), referenceChunk);
  }

  protected CDORevisionImpl getRevision()
  {
    return getRevisionManager().getRevision(id, referenceChunk);
  }
}
