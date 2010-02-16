/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class LockObjectsIndication extends RefreshSessionIndication
{
  private List<Object> objectsToBeLocked = new ArrayList<Object>();

  private IView view;

  private LockType lockType;

  public LockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    super.indicating(in);
    int viewID = in.readInt();
    lockType = in.readCDOLockType();
    long timeout = in.readLong();

    try
    {
      view = getSession().getView(viewID);
      InternalLockManager lockManager = getRepository().getLockManager();
      lockManager.lock(lockType, view, objectsToBeLocked, timeout);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  protected CDORevisionKey handleViewedRevision(CDOBranch branch, CDORevisionKey revision)
  {
    if (getRepository().isSupportingBranches())
    {
      objectsToBeLocked.add(CDOIDUtil.createIDAndBranch(revision.getID(), branch));
    }
    else
    {
      objectsToBeLocked.add(revision.getID());
    }

    return revision;
  }

  @Override
  protected void writeDetachedObject(CDODataOutput out, CDORevisionKey key) throws IOException
  {
    getRepository().getLockManager().unlock(lockType, view, objectsToBeLocked);
    throw new IllegalArgumentException("Objects has been detached: " + key); //$NON-NLS-1$
  }
}
