/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IAudit;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SetAuditIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, SetAuditIndication.class);

  private boolean[] existanceFlags;

  public SetAuditIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SET_AUDIT);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read viewID: {0}", viewID); //$NON-NLS-1$
    }

    long timeStamp = in.readLong();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read timeStamp: {0,date} {0,time}", timeStamp); //$NON-NLS-1$
    }

    int size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} IDs", size); //$NON-NLS-1$
    }

    List<CDOID> invalidObjects = new ArrayList<CDOID>(size);
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      invalidObjects.add(id);
      if (TRACER.isEnabled())
      {
        TRACER.format("Read ID: {0}", id); //$NON-NLS-1$
      }
    }

    IView view = getSession().getView(viewID);
    if (view instanceof IAudit)
    {
      IAudit audit = (IAudit)view;
      existanceFlags = audit.setTimeStamp(timeStamp, invalidObjects);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} existanceFlags", existanceFlags.length); //$NON-NLS-1$
    }

    out.writeInt(existanceFlags.length);
    for (int i = 0; i < existanceFlags.length; i++)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing existanceFlag: {0}", existanceFlags[i]); //$NON-NLS-1$
      }

      out.writeBoolean(existanceFlags[i]);
    }
  }
}
