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

  private CDORevisionImpl revision;

  private int referenceChunk;

  public LoadRevisionIndication()
  {
    super(CDOProtocolConstants.SIGNAL_LOAD_REVISION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    CDOID id = CDOIDImpl.read(in);
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

      referenceChunk = CDORevisionImpl.COMPLETE_REFERENCES;
    }

    boolean historical = in.readBoolean();
    if (historical)
    {
      long timeStamp = in.readLong();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read timeStamp: {0}", timeStamp);
      }

      revision = getRevisionManager().getRevision(id, referenceChunk, timeStamp);
    }
    else
    {
      revision = getRevisionManager().getRevision(id, referenceChunk);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    revision.write(out, getSession(), referenceChunk);
  }
}
