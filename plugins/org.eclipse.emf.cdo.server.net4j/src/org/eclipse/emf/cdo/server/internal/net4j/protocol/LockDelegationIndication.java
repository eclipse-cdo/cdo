/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.CheckUtil;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class LockDelegationIndication extends LockObjectsIndication
{
  private InternalView view;

  private String lockAreaID;

  public LockDelegationIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_DELEGATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lockAreaID = in.readString();
    super.indicating(in);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      super.responding(out);
    }
    finally
    {
      view.close();
    }
  }

  @Override
  protected IView getView(int viewID, CDOBranch viewedBranch)
  {
    // The view needs a lockArea...
    InternalLockManager lockManager = getRepository().getLockManager();
    InternalSession session = getSession();
    LockArea lockArea;

    try
    {
      lockArea = lockManager.getLockArea(lockAreaID);

      // If we get here, the lockArea already exists.
      view = (InternalView)lockManager.openView(session, InternalSession.TEMP_VIEW_ID, true, lockAreaID);
    }
    catch (LockAreaNotFoundException e)
    {
      // If we get here, the lockArea does not yet exist on the master, so we open
      // a view without a lockArea first, then create a lockArea with the given ID,
      // and associate it with the view.
      view = session.openView(InternalSession.TEMP_VIEW_ID, viewedBranch.getHead());
      lockArea = lockManager.createLockArea(view, lockAreaID);
      view.setDurableLockingID(lockAreaID);
    }

    // The viewID received as an argument, is the ID of the client's view, which
    // does not exist on the master. So we ignore this argument and open a new
    // view instead.
    CheckUtil.checkState(lockAreaID.equals(lockArea.getDurableLockingID()), "lockAreaID has incorrect value");

    return view;
  }
}
