/*
 * Copyright (c) 2011, 2012, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SwitchTargetIndication extends CDOServerReadIndicationWithMonitoring
{
  private List<CDORevisionDelta> allChangedObjects = new ArrayList<>();

  private List<CDOID> allDetachedObjects = new ArrayList<>();

  public SwitchTargetIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SWITCH_TARGET);
  }

  @Override
  protected int getIndicatingWorkPercent()
  {
    return 90;
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws IOException
  {
    try
    {
      monitor.begin();
      Async async = monitor.forkAsync();

      try
      {
        int viewID = in.readXInt();
        CDOBranchPoint branchPoint = in.readCDOBranchPoint();

        int size = in.readXInt();
        List<CDOID> invalidObjects = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
          CDOID id = in.readCDOID();
          invalidObjects.add(id);
        }

        InternalView view = getView(viewID);
        view.changeTarget(branchPoint, invalidObjects, allChangedObjects, allDetachedObjects);
      }
      finally
      {
        async.stop();
      }

    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeXInt(allChangedObjects.size());
    for (CDORevisionDelta delta : allChangedObjects)
    {
      out.writeCDORevisionDelta(delta);
    }

    out.writeXInt(allDetachedObjects.size());
    for (CDOID id : allDetachedObjects)
    {
      out.writeCDOID(id);
    }
  }
}
