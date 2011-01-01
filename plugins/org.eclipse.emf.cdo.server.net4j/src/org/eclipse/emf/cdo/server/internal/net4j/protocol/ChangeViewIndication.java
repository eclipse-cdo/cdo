/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ChangeViewIndication extends CDOServerReadIndicationWithMonitoring
{
  private boolean[] existanceFlags;

  public ChangeViewIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_VIEW);
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
        int viewID = in.readInt();
        CDOBranchPoint branchPoint = in.readCDOBranchPoint();

        int size = in.readInt();
        List<CDOID> invalidObjects = new ArrayList<CDOID>(size);
        for (int i = 0; i < size; i++)
        {
          CDOID id = in.readCDOID();
          invalidObjects.add(id);
        }

        InternalView view = getSession().getView(viewID);
        existanceFlags = view.changeTarget(branchPoint, invalidObjects);
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
    out.writeInt(existanceFlags.length);
    for (int i = 0; i < existanceFlags.length; i++)
    {
      out.writeBoolean(existanceFlags[i]);
    }
  }
}
