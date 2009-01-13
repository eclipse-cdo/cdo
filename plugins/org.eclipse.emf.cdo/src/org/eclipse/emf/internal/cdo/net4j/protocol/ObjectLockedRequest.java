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

/**
 * @author Simon McDuff
 */
public class ObjectLockedRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, ObjectLockedRequest.class);

  private CDOView view;

  private CDOObject object;

  private RWLockManager.LockType lockType;

  public ObjectLockedRequest(CDOClientProtocol protocol, CDOView view, CDOObject object, RWLockManager.LockType lockType)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OBJECT_LOCKED);
    this.view = view;
    this.object = object;
    this.lockType = lockType;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Requesting if object {0} has  of lock for object {1}", object.cdoID(),
          lockType == RWLockManager.LockType.READ ? "read" : "write");
    }

    out.writeInt(view.getViewID());
    out.writeCDOLockType(lockType);
    out.writeCDOID(object.cdoID());
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
