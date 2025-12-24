/*
 * Copyright (c) 2009-2012, 2016, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class ObjectLockedRequest extends CDOClientRequest<Boolean>
{
  private CDOView view;

  private CDOObject object;

  private LockType lockType;

  private boolean byOthers;

  public ObjectLockedRequest(CDOClientProtocol protocol, CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OBJECT_LOCKED);
    this.view = view;
    this.object = object;
    this.lockType = lockType;
    this.byOthers = byOthers;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(view.getViewID());
    out.writeCDOLockType(lockType);
    out.writeCDOID(object.cdoID());
    out.writeBoolean(byOthers);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
