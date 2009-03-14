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

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SetAuditRequest extends CDOClientRequest<boolean[]>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SetAuditRequest.class);

  private int viewID;

  private long timeStamp;

  private List<InternalCDOObject> invalidObjects;

  public SetAuditRequest(CDOClientProtocol protocol, int viewID, long timeStamp, List<InternalCDOObject> invalidObjects)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SET_AUDIT);
    this.viewID = viewID;
    this.timeStamp = timeStamp;
    this.invalidObjects = invalidObjects;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing viewID: {0}", viewID);
    }

    out.writeInt(viewID);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing timeStamp: {0,date} {0,time}", timeStamp);
    }

    out.writeLong(timeStamp);

    int size = invalidObjects.size();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} IDs", size);
    }

    out.writeInt(size);
    for (InternalCDOObject object : invalidObjects)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing ID: {0}", object.cdoID());
      }

      out.writeCDOID(object.cdoID());
    }
  }

  @Override
  protected boolean[] confirming(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} existanceFlags", size);
    }

    boolean[] existanceFlags = new boolean[size];
    for (int i = 0; i < size; i++)
    {
      boolean existanceFlag = in.readBoolean();
      existanceFlags[i] = existanceFlag;
      if (TRACER.isEnabled())
      {
        TRACER.format("Read existanceFlag: {0}", existanceFlag);
      }
    }

    return existanceFlags;
  }
}
