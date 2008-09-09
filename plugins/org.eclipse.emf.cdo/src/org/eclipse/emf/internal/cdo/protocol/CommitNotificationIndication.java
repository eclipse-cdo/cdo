/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CommitNotificationIndication extends CDOClientIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      CommitNotificationIndication.class);

  public CommitNotificationIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_INVALIDATION;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    long timeStamp = in.readLong();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read timeStamp: {0,date} {0,time}", timeStamp);
    }

    int size = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} dirty IDs", size);
    }

    CDOSessionImpl session = getSession();
    Set<CDOIDAndVersion> dirtyOIDs = new HashSet<CDOIDAndVersion>();
    for (int i = 0; i < size; i++)
    {
      CDOIDAndVersion dirtyOID = in.readCDOIDAndVersion();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Read dirty ID: {0}", dirtyOID);
      }

      dirtyOIDs.add(dirtyOID);
    }

    size = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} Deltas", size);
    }

    List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>();
    for (int i = 0; i < size; i++)
    {
      CDORevisionDelta revisionDelta = in.readCDORevisionDelta();
      deltas.add(revisionDelta);
    }

    session.handleCommitNotification(timeStamp, dirtyOIDs, deltas, null);
  }
}
