/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDurableLockingManager
{
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks);

  /**
   * Returns the {@link LockArea lock area} specified by the given durableLockingID, never <code>null</code>.
   * 
   * @throws LockAreaNotFoundException
   *           if the given durableLockingID is unknown.
   */
  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException;

  public void getLockAreas(String userIDPrefix, LockArea.Handler handler);

  public void deleteLockArea(String durableLockingID);

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface LockArea extends CDOBranchPoint
  {
    public String getDurableLockingID();

    public String getUserID();

    public boolean isReadOnly();

    public Map<CDOID, LockGrade> getLocks();

    /**
     * @author Eike Stepper
     */
    public interface Handler
    {
      public boolean handleLockArea(LockArea area);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LockAreaNotFoundException extends IllegalStateException
  {
    private static final long serialVersionUID = 1L;

    private String durableLockingID;

    public LockAreaNotFoundException(String durableLockingID)
    {
      super("No lock area for ID=" + durableLockingID);
      this.durableLockingID = durableLockingID;
    }

    public LockAreaNotFoundException(String message, Throwable cause, String durableLockingID)
    {
      super(message, cause);
      this.durableLockingID = durableLockingID;
    }

    public String getDurableLockingID()
    {
      return durableLockingID;
    }
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   */
  public enum LockGrade
  {
    NONE(0), READ(1), WRITE(2), READ_WRITE(READ.getValue() | WRITE.getValue());

    private final int value;

    private LockGrade(int value)
    {
      this.value = value;
    }

    public int getValue()
    {
      return value;
    }

    public boolean isRead()
    {
      return (value & 1) != 0;
    }

    public boolean isWrite()
    {
      return (value & 2) != 0;
    }

    public LockGrade getUpdated(LockType type, boolean on)
    {
      int mask = type == LockType.READ ? 1 : 2;
      if (on)
      {
        return get(value | mask);
      }

      return get(value & ~mask);
    }

    public static LockGrade get(LockType type)
    {
      if (type == LockType.READ)
      {
        return READ;
      }

      if (type == LockType.WRITE)
      {
        return WRITE;
      }

      return NONE;
    }

    public static LockGrade get(boolean read, boolean write)
    {
      return get((read ? 1 : 0) | (write ? 2 : 0));
    }

    public static LockGrade get(int value)
    {
      switch (value)
      {
      case 0:
        return NONE;

      case 1:
        return READ;

      case 2:
        return WRITE;

      case 3:
        return READ_WRITE;

      default:
        throw new IllegalArgumentException("Invalid lock grade: " + value);
      }
    }
  }
}
