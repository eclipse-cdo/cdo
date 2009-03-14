/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

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
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitNotificationIndication.class);

  public CommitNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    long timeStamp = in.readLong();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read timeStamp: {0,date} {0,time}", timeStamp);
    }

    CDOPackageUnit[] packageUnits = in.readCDOPackageUnits(null);
    InternalCDOPackageRegistry packageRegistry = (InternalCDOPackageRegistry)getSession().getPackageRegistry();
    for (int i = 0; i < packageUnits.length; i++)
    {
      packageRegistry.putPackageUnit((InternalCDOPackageUnit)packageUnits[i]);
    }

    int size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} dirty IDs", size);
    }

    InternalCDOSession session = getSession();
    Set<CDOIDAndVersion> dirtyOIDs = new HashSet<CDOIDAndVersion>();
    for (int i = 0; i < size; i++)
    {
      CDOIDAndVersion dirtyOID = in.readCDOIDAndVersion();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read dirty ID: {0}", dirtyOID);
      }

      dirtyOIDs.add(dirtyOID);
    }

    size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} Deltas", size);
    }

    List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>();
    for (int i = 0; i < size; i++)
    {
      CDORevisionDelta revisionDelta = in.readCDORevisionDelta();
      deltas.add(revisionDelta);
    }

    size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} Detach Objects", size);
    }

    List<CDOID> detachedObjects = new ArrayList<CDOID>();
    for (int i = 0; i < size; i++)
    {
      detachedObjects.add(in.readCDOID());
    }

    session.handleCommitNotification(timeStamp, dirtyOIDs, detachedObjects, deltas, null);
  }
}
