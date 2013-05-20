/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.server.db4o.IDB4OStore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Caspar De Groot
 */
public class DB4OLockArea
{
  private static Map<LockArea, DB4OLockArea> map = new HashMap<LockArea, DB4OLockArea>();

  private String id;

  private String userID;

  private long timestamp;

  private int branchID;

  private boolean readOnly;

  private List<DB4OLockEntry> lockEntries = new LinkedList<DB4OLockEntry>();

  public static DB4OLockArea getPrimitiveLockArea(LockArea lockArea)
  {
    DB4OLockArea primitive = map.get(lockArea);
    if (primitive == null)
    {
      primitive = new DB4OLockArea();
    }

    primitive.id = lockArea.getDurableLockingID();
    primitive.userID = lockArea.getUserID();
    primitive.timestamp = lockArea.getTimeStamp();
    primitive.branchID = lockArea.getBranch().getID();
    primitive.readOnly = lockArea.isReadOnly();

    List<DB4OLockEntry> newList = DB4OLockEntry.getPrimitiveLockEntries(primitive, lockArea.getLocks());
    primitive.lockEntries = newList;

    return primitive;
  }

  public static LockArea getLockArea(IDB4OStore store, DB4OLockArea primitive)
  {
    // Reconstruct the branchpoint
    //
    CDOBranchManager branchManager = store.getRepository().getBranchManager();
    CDOBranch branch = branchManager.getBranch(primitive.branchID);
    CDOBranchPoint branchpoint = branch.getPoint(primitive.timestamp);

    // Reconstruct the lockMap
    //
    Map<CDOID, LockGrade> lockMap = CDOIDUtil.createMap();
    for (DB4OLockEntry entry : primitive.getLockEntries())
    {
      CDOID cdoid = CDOIDUtil.createLong(entry.getCdoID());
      LockGrade lockGrade = LockGrade.get(entry.getLockGrade());
      lockMap.put(cdoid, lockGrade);
    }

    LockArea lockArea = CDOLockUtil.createLockArea(primitive.id, primitive.userID, branchpoint, primitive.readOnly,
        lockMap);
    map.put(lockArea, primitive);
    return lockArea;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }

  public String getUserID()
  {
    return userID;
  }

  public long getTimestamp()
  {
    return timestamp;
  }

  public int getBranchID()
  {
    return branchID;
  }

  public List<DB4OLockEntry> getLockEntries()
  {
    return lockEntries;
  }
}
