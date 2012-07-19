/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

import java.util.Collection;
import java.util.Map;

/**
 * @author Caspar De Groot
 */
public class DB4ODurableLockingManager extends Lifecycle
{
  public DB4ODurableLockingManager()
  {
  }

  public LockArea createLockArea(DB4OStoreAccessor accessor, String userID, CDOBranchPoint branchPoint,
      boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    String durableLockingID = getNextDurableLockingID(accessor);
    LockArea lockArea = CDOLockUtil.createLockArea(durableLockingID, userID, branchPoint, readOnly, locks);
    storeLockArea(accessor, lockArea);
    return lockArea;
  }

  private void storeLockArea(DB4OStoreAccessor accessor, LockArea area)
  {
    ObjectContainer objectContainer = accessor.getObjectContainer();
    DB4OLockArea primitiveLockArea = DB4OLockArea.getPrimitiveLockArea(area);
    objectContainer.store(primitiveLockArea);
    objectContainer.commit();
  }

  public LockArea getLockArea(DB4OStoreAccessor accessor, String durableLockingID) throws LockAreaNotFoundException
  {
    DB4OLockArea primitive = getPrimitiveLockArea(accessor, durableLockingID);
    return DB4OLockArea.getLockArea(accessor.getStore(), primitive);
  }

  private DB4OLockArea getPrimitiveLockArea(DB4OStoreAccessor accessor, String durableLockingID)
      throws LockAreaNotFoundException
  {
    ObjectContainer container = accessor.getObjectContainer();
    Query query = container.query();
    query.constrain(DB4OLockArea.class);
    query.descend("id").constrain(durableLockingID);

    ObjectSet<?> lockAreas = query.execute();
    if (lockAreas.isEmpty())
    {
      throw new LockAreaNotFoundException(durableLockingID);
    }

    if (lockAreas.size() > 1)
    {
      throw new AssertionError("Lockarea stored more than once in object database");
    }

    return (DB4OLockArea)lockAreas.get(0);
  }

  public void getLockAreas(DB4OStoreAccessor accessor, String userIDPrefix, Handler handler)
  {
    ObjectContainer container = accessor.getObjectContainer();
    Query query = container.query();
    query.constrain(DB4OLockArea.class);

    if (userIDPrefix.length() > 0)
    {
      query.descend("userID").constrain(userIDPrefix).startsWith(true);
    }

    ObjectSet<?> primitives = query.execute();
    for (Object primitive : primitives)
    {
      LockArea area = DB4OLockArea.getLockArea(accessor.getStore(), (DB4OLockArea)primitive);
      if (!handler.handleLockArea(area))
      {
        break;
      }
    }
  }

  public void deleteLockArea(DB4OStoreAccessor accessor, String durableLockingID)
  {
    DB4OLockArea primitive = getPrimitiveLockArea(accessor, durableLockingID);
    ObjectContainer container = accessor.getObjectContainer();
    container.delete(primitive);
    container.commit();
  }

  public void lock(DB4OStoreAccessor accessor, String durableLockingID, LockType type,
      Collection<? extends Object> objectsToLock)
  {
    // TODO (CD) Refactor? Next chunk of code copied verbatim from MEMStore.lock
    LockArea area = getLockArea(accessor, durableLockingID);
    Map<CDOID, LockGrade> locks = area.getLocks();

    InternalLockManager lockManager = accessor.getStore().getRepository().getLockingManager();
    for (Object objectToLock : objectsToLock)
    {
      CDOID id = lockManager.getLockKeyID(objectToLock);
      LockGrade grade = locks.get(id);
      if (grade != null)
      {
        grade = grade.getUpdated(type, true);
      }
      else
      {
        grade = LockGrade.get(type);
      }

      locks.put(id, grade);
    }

    storeLockArea(accessor, area);
  }

  public void unlock(DB4OStoreAccessor accessor, String durableLockingID, LockType type,
      Collection<? extends Object> objectsToUnlock)
  {
    // TODO (CD) Refactor? Next chunk of code copied verbatim from MEMStore.lock
    LockArea area = getLockArea(accessor, durableLockingID);
    Map<CDOID, LockGrade> locks = area.getLocks();

    InternalLockManager lockManager = accessor.getStore().getRepository().getLockingManager();
    for (Object objectToUnlock : objectsToUnlock)
    {
      CDOID id = lockManager.getLockKeyID(objectToUnlock);
      LockGrade grade = locks.get(id);
      if (grade != null)
      {
        grade = grade.getUpdated(type, false);
        if (grade == LockGrade.NONE)
        {
          locks.remove(id);
        }
        else
        {
          locks.put(id, grade);
        }
      }
    }

    storeLockArea(accessor, area);
  }

  public void unlock(DB4OStoreAccessor accessor, String durableLockingID)
  {
    LockArea area = getLockArea(accessor, durableLockingID);
    Map<CDOID, LockGrade> locks = area.getLocks();
    locks.clear();

    storeLockArea(accessor, area);
  }

  // TODO: Refactor -- this was copied verbatim from DurableLockingManager
  private String getNextDurableLockingID(DB4OStoreAccessor accessor)
  {
    for (;;)
    {
      String durableLockingID = CDOLockUtil.createDurableLockingID();

      try
      {
        getLockArea(accessor, durableLockingID); // Check uniqueness
        // Not unique; try once more...
      }
      catch (LockAreaNotFoundException ex)
      {
        return durableLockingID;
      }
    }
  }
}
