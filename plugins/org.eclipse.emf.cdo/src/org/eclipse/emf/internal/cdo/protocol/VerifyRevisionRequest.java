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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class VerifyRevisionRequest extends CDOClientRequest<List<InternalCDORevision>>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, VerifyRevisionRequest.class);

  private Collection<InternalCDORevision> revisions;

  public VerifyRevisionRequest(IChannel channel, Collection<InternalCDORevision> revisions)
  {
    super(channel);
    this.revisions = revisions;
  }

  public VerifyRevisionRequest(IChannel channel, InternalCDORevision revision)
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
    for (InternalCDORevision revision : revisions)
    {
      CDOID id = revision.getID();
      int version = revision.getVersion();
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing ID and version: {0}v{1}", id, version);
      }

      CDOIDUtil.write(out, id);
      out.writeInt(version);
    }
  }

  @Override
  protected List<InternalCDORevision> confirming(ExtendedDataInputStream in) throws IOException
  {
    ArrayList<InternalCDORevision> result = new ArrayList<InternalCDORevision>();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} timeStamps", revisions.size());
    }

    for (InternalCDORevision revision : revisions)
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
