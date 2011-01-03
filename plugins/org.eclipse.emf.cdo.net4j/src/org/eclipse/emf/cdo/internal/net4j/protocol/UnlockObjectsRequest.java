/***************************************************************************
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simon McDuff
 */
public class UnlockObjectsRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, UnlockObjectsRequest.class);

  private CDOView view;

  private Collection<? extends CDOObject> objects;

  private LockType lockType;

  public UnlockObjectsRequest(CDOClientProtocol protocol, CDOView view, Collection<? extends CDOObject> objects,
      LockType lockType)
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
      if (TRACER.isEnabled())
      {
        TRACER.format("Unlocking all objects for view {0}", view.getViewID()); //$NON-NLS-1$
      }

      out.writeInt(CDOProtocolConstants.RELEASE_ALL_LOCKS);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Unlocking of type {0} requested for view {1}", lockType == LockType.READ ? "read" //$NON-NLS-1$ //$NON-NLS-2$
            : "write", view.getViewID()); //$NON-NLS-1$
      }

      out.writeInt(objects.size());
      for (CDOObject object : objects)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Unlocking requested for objects {0}", object.cdoID()); //$NON-NLS-1$
        }

        CDOIDAndBranch idAndBranch = CDOIDUtil.createIDAndBranch(object.cdoID(), view.getBranch());
        out.writeCDOIDAndBranch(idAndBranch);
      }
    }
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
