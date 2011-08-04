/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class LockObjectsIndication extends CDOServerWriteIndication
{
  private List<CDORevisionKey> staleRevisions = new LinkedList<CDORevisionKey>();

  private boolean timedOut;

  private boolean passiveUpdatesEnabled;

  private long requiredTimestamp;

  private boolean staleNoUpdate;

  public LockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalSession session = getSession();
    passiveUpdatesEnabled = session.isPassiveUpdateEnabled();

    int viewID = in.readInt();
    LockType lockType = in.readCDOLockType();
    long timeout = in.readLong();
    CDOBranch viewedBranch = in.readCDOBranch();

    int nRevisions = in.readInt();
    CDORevisionKey[] revKeys = new CDORevisionKey[nRevisions];
    for (int i = 0; i < nRevisions; i++)
    {
      revKeys[i] = in.readCDORevisionKey();
    }

    List<Object> objectsToBeLocked = new ArrayList<Object>();
    boolean isSupportingBranches = getRepository().isSupportingBranches();
    for (CDORevisionKey revKey : revKeys)
    {
      CDOID id = revKey.getID();
      if (isSupportingBranches)
      {
        objectsToBeLocked.add(CDOIDUtil.createIDAndBranch(id, viewedBranch));
      }
      else
      {
        objectsToBeLocked.add(id);
      }
    }

    IView view = session.getView(viewID);
    InternalLockManager lockManager = getRepository().getLockManager();

    try
    {
      lockManager.lock(true, lockType, view, objectsToBeLocked, timeout);
    }
    catch (TimeoutRuntimeException ex)
    {
      timedOut = true;
      return;
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }

    try
    {
      for (CDORevisionKey revKey : revKeys)
      {
        checkStale(viewedBranch, revKey);
      }
    }
    catch (IllegalArgumentException ex)
    {
      lockManager.unlock(true, lockType, view, objectsToBeLocked);
      throw ex;
    }

    // If some of the clients' revisions are stale and it has passiveUpdates disabled,
    // then the locks are useless so we release them and report the stale revisions (later)
    staleNoUpdate = staleRevisions.size() > 0 && !passiveUpdatesEnabled;
    if (staleNoUpdate)
    {
      lockManager.unlock(true, lockType, view, objectsToBeLocked);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    boolean lockSuccesful = !timedOut && !staleNoUpdate;
    out.writeBoolean(lockSuccesful);

    if (lockSuccesful)
    {
      boolean clientMustWait = staleRevisions.size() > 0;
      out.writeBoolean(clientMustWait);
      if (clientMustWait)
      {
        out.writeLong(requiredTimestamp);
      }
    }
    else
    {
      out.writeBoolean(timedOut);
      if (!timedOut)
      {
        out.writeInt(staleRevisions.size());
        for (CDORevisionKey staleRevision : staleRevisions)
        {
          out.writeCDORevisionKey(staleRevision);
        }
      }
    }
  }

  private void checkStale(CDOBranch viewedBranch, CDORevisionKey revKey)
  {
    CDOID id = revKey.getID();
    InternalCDORevision rev = getRepository().getRevisionManager().getRevision(id, viewedBranch.getHead(),
        CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);

    if (rev == null)
    {
      throw new IllegalArgumentException(String.format("Object %s not found in branch %s (possibly detached)", id,
          viewedBranch));
    }

    if (!revKey.equals(rev))
    {
      staleRevisions.add(revKey);
      requiredTimestamp = Math.max(requiredTimestamp, rev.getTimeStamp());
    }
  }
}
