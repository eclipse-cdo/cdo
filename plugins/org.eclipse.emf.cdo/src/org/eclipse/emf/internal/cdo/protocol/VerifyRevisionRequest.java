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
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerifyRevisionRequest extends CDOClientRequest<List<CDORevisionImpl>>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, VerifyRevisionRequest.class);

  private Collection<CDORevisionImpl> revisions;

  public VerifyRevisionRequest(IChannel channel, Collection<CDORevisionImpl> revisions)
  {
    super(channel);
    this.revisions = revisions;
  }

  public VerifyRevisionRequest(IChannel channel, CDORevisionImpl revision)
  {
    this(channel, Collections.singleton(revision));
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_VERIFY_REVISION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} IDs and versions", revisions.size());
    }

    out.writeInt(revisions.size());
    for (CDORevisionImpl revision : revisions)
    {
      CDOID id = revision.getID();
      int version = revision.getVersion();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing ID and version: {0}v{1}", id, version);
      }

      CDOIDImpl.write(out, id);
      out.writeInt(version);
    }
  }

  @Override
  protected List<CDORevisionImpl> confirming(ExtendedDataInputStream in) throws IOException
  {
    ArrayList<CDORevisionImpl> result = new ArrayList<CDORevisionImpl>();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} timeStamps", revisions.size());
    }

    for (CDORevisionImpl revision : revisions)
    {
      long revised = in.readLong();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Reading timeStamp: {0}", revised);
      }

      if (revised != CDORevision.UNSPECIFIED_DATE)
      {
        revision.setRevised(revised);
        result.add(revision);
      }
    }

    return result;
  }
}
