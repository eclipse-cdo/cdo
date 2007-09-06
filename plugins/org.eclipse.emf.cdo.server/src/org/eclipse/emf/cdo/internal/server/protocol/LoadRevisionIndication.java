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
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LoadRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionIndication.class);

  protected CDOID[] ids;

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
    referenceChunk = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read referenceChunk: {0}", referenceChunk);

    int size = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Reading {0} IDs", size);

    ids = new CDOID[size];
    for (int i = 0; i < size; i++)
    {
      CDOID id = CDOIDImpl.read(in);
      if (PROTOCOL.isEnabled()) PROTOCOL.format("Read ID: {0}", id);
      ids[i] = id;
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    Session session = getSession();
    List<CDORevisionImpl> containedRevisions = new ArrayList<CDORevisionImpl>(0);

    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing {0} revisions", ids.length);
    for (CDOID id : ids)
    {
      CDORevisionImpl revision = getRevision(id);
      revision.write(out, session, referenceChunk);
      session.collectContainedRevisions(revision, referenceChunk, containedRevisions);
    }

    out.writeInt(containedRevisions.size());
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing {0} additional revisions", containedRevisions.size());
    for (CDORevisionImpl revision : containedRevisions)
    {
      revision.write(out, session, referenceChunk);
    }
  }

  protected CDORevisionImpl getRevision(CDOID id)
  {
    return getRevisionManager().getRevision(id, referenceChunk);
  }
}
