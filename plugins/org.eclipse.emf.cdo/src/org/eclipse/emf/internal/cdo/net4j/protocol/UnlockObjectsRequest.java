/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simon McDuff
 */
public class UnlockObjectsRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, UnlockObjectsRequest.class);

  private CDOView view;

  private Collection<? extends CDOObject> objects;

  private RWLockManager.LockType lockType;

  public UnlockObjectsRequest(CDOClientProtocol protocol, CDOView view, Collection<? extends CDOObject> objects,
      RWLockManager.LockType lockType)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS);
    this.view = view;
    this.objects = objects;
    this.lockType = lockType;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(view.getViewID());
    out.writeCDOLockType(lockType);
    if (objects == null)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Unlocking all objects for view {0}", view.getViewID());
      }

      out.writeInt(CDOProtocolConstants.RELEASE_ALL_LOCKS);
    }
    else
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Unlocking of type {0} requested for view {1}",
            lockType == RWLockManager.LockType.READ ? "read" : "write", view.getViewID());
      }

      out.writeInt(objects.size());
      for (CDOObject object : objects)
      {
        if (PROTOCOL_TRACER.isEnabled())
        {
          PROTOCOL_TRACER.format("Unlocking requested for objects {0}", object.cdoID());
        }

        out.writeCDOID(object.cdoID());
      }
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
