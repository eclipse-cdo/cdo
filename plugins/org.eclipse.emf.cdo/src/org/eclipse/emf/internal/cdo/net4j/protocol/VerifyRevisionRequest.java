/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.bundle.OM;

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
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, VerifyRevisionRequest.class);

  private Collection<InternalCDORevision> revisions;

  public VerifyRevisionRequest(CDOClientProtocol protocol, Collection<InternalCDORevision> revisions)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_VERIFY_REVISION);
    this.revisions = revisions;
  }

  public VerifyRevisionRequest(CDOClientProtocol protocol, InternalCDORevision revision)
  {
    this(protocol, Collections.singleton(revision));
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} IDs and versions", revisions.size());
    }

    out.writeInt(revisions.size());
    for (InternalCDORevision revision : revisions)
    {
      CDOID id = revision.getID();
      int version = revision.getVersion();
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing ID and version: {0}v{1}", id, version);
      }

      out.writeCDOID(id);
      out.writeInt(version);
    }
  }

  @Override
  protected List<InternalCDORevision> confirming(CDODataInput in) throws IOException
  {
    ArrayList<InternalCDORevision> result = new ArrayList<InternalCDORevision>();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} timeStamps", revisions.size());
    }

    for (InternalCDORevision revision : revisions)
    {
      long revised = in.readLong();
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading timeStamp: {0}", revised);
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
