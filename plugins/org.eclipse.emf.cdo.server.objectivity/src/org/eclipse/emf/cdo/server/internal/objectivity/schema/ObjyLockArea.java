/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.objectivity.schema;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;

import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;
import com.objy.db.util.ooTreeSetX;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Ibrahim Sallam
 */
public class ObjyLockArea extends ooObj
{

  public static final int DEFAULT_DURABLE_LOCKING_ID_BYTES = 32;

  private String durableLockingID;

  private String userID;

  private ObjyBranch branchPoint;

  private boolean readOnly;

  private ooTreeSetX readLockSet;

  private ooTreeSetX writeLockSet;

  private ooTreeSetX readWriteLockSet;

  protected ObjyLockArea(String durableLockingID, String userID, ObjyBranch branchPoint, boolean readOnly)
  {
    this.durableLockingID = durableLockingID;
    this.userID = userID;
    this.branchPoint = branchPoint;
    this.readOnly = readOnly;
  }

  public static ObjyLockArea create(ooId scopeContOid, String durableLockingID, String userID, ObjyBranch branchPoint,
      boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    ObjyLockArea objyLockArea = new ObjyLockArea(durableLockingID, userID, branchPoint, readOnly);
    ooObj clusterObject = ooObj.create_ooObj(scopeContOid);
    clusterObject.cluster(objyLockArea);

    objyLockArea.createLockSets(locks);

    return objyLockArea;
  }

  protected void createLockSets(Map<CDOID, LockGrade> locks)
  {
    readLockSet = new ooTreeSetX();
    this.cluster(readLockSet);
    writeLockSet = new ooTreeSetX();
    this.cluster(writeLockSet);
    readWriteLockSet = new ooTreeSetX();
    this.cluster(readWriteLockSet);

    for (Entry<CDOID, LockGrade> entry : locks.entrySet())
    {
      CDOID id = entry.getKey();
      LockGrade grade = entry.getValue();

      switch (grade)
      {
      case READ_WRITE:
        readWriteLockSet.add(id);
        break;
      case READ:
        readLockSet.add(id);
        break;
      case WRITE:
        writeLockSet.add(id);
        break;
      }
    }

  }

  public String getDurableLockingID()
  {
    fetch();
    return durableLockingID;
  }

  public String getUserID()
  {
    fetch();
    return userID;
  }

  public ObjyBranch getBranch()
  {
    fetch();
    return branchPoint;
  }

  public long getTimeStamp()
  {
    fetch();
    return branchPoint.getBranchInfo().getBaseTimeStamp();
  }

  public boolean isReadOnly()
  {
    fetch();
    return readOnly;
  }

  public Map<CDOID, LockGrade> getLocks()
  {
    fetch();

    Map<CDOID, LockGrade> locks = CDOIDUtil.createMap();

    @SuppressWarnings("rawtypes")
    Iterator itr = readLockSet.iterator();
    while (itr.hasNext())
    {
      locks.put(OBJYCDOIDUtil.getCDOID(((ooObj)itr.next()).getOid()), LockGrade.READ);
    }

    itr = writeLockSet.iterator();
    while (itr.hasNext())
    {
      locks.put(OBJYCDOIDUtil.getCDOID(((ooObj)itr.next()).getOid()), LockGrade.WRITE);
    }

    itr = readWriteLockSet.iterator();
    while (itr.hasNext())
    {
      locks.put(OBJYCDOIDUtil.getCDOID(((ooObj)itr.next()).getOid()), LockGrade.READ_WRITE);
    }

    return locks;
  }

  @Override
  public String toString()
  {
    fetch();
    return MessageFormat.format(
        "ObjyLockArea[id={0}, user={1}, branchPoint={2}, readOnly={3}, readlocks={4}, writeLocks{5}, readWriteLocks{6}",
        durableLockingID, userID, branchPoint, readOnly, readLockSet.size(), writeLockSet.size(),
        readWriteLockSet.size());
  }
}
