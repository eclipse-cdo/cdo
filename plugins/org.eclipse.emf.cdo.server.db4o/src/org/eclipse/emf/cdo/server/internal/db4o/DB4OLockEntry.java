/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Caspar De Groot
 */
public class DB4OLockEntry
{
  private long cdoID;

  private int lockGrade;

  public DB4OLockEntry(long longCdoID, int intLockGrade)
  {
    cdoID = longCdoID;
    lockGrade = intLockGrade;
  }

  public static List<DB4OLockEntry> getPrimitiveLockEntries(DB4OLockArea primitive, Map<CDOID, LockGrade> locks)
  {
    List<DB4OLockEntry> newList = new LinkedList<DB4OLockEntry>();

    for (Entry<CDOID, LockGrade> entry : locks.entrySet())
    {
      CDOID cdoid = entry.getKey();
      long longCdoID = CDOIDUtil.getLong(cdoid);

      LockGrade lockGrade = entry.getValue();
      int intLockGrade = lockGrade.getValue();

      DB4OLockEntry lockEntry = getEntry(primitive.getLockEntries(), longCdoID);
      if (lockEntry == null)
      {
        lockEntry = new DB4OLockEntry(longCdoID, intLockGrade);
      }
      else
      {
        lockEntry.setLockGrade(intLockGrade);
      }

      newList.add(lockEntry);
    }

    return newList;
  }

  private void setLockGrade(int lockGrade)
  {
    this.lockGrade = lockGrade;
  }

  // TODO (CD) Avoid linear search
  private static DB4OLockEntry getEntry(List<DB4OLockEntry> entries, long targetID)
  {
    for (DB4OLockEntry entry : entries)
    {
      if (entry.cdoID == targetID)
      {
        return entry;
      }
    }

    return null;
  }

  public long getCdoID()
  {
    return cdoID;
  }

  public int getLockGrade()
  {
    return lockGrade;
  }
}
