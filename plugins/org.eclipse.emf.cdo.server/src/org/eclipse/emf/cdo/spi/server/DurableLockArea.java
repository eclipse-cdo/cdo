/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;

import java.text.MessageFormat;
import java.util.Map;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @deprecated Use {@link CDOLockUtil#createLockArea(String, String, CDOBranchPoint, boolean, Map)} instead
 */
@Deprecated
public class DurableLockArea implements LockArea
{
  @Deprecated
  public static final int DEFAULT_DURABLE_LOCKING_ID_BYTES = 32;

  private String durableLockingID;

  private String userID;

  private CDOBranchPoint branchPoint;

  private boolean readOnly;

  private Map<CDOID, LockGrade> locks;

  @Deprecated
  public DurableLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    this.durableLockingID = durableLockingID;
    this.userID = userID;
    this.branchPoint = branchPoint;
    this.readOnly = readOnly;
    this.locks = locks;
  }

  @Deprecated
  @Override
  public String getDurableLockingID()
  {
    return durableLockingID;
  }

  @Deprecated
  @Override
  public String getUserID()
  {
    return userID;
  }

  @Deprecated
  @Override
  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  @Deprecated
  @Override
  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  @Deprecated
  @Override
  public boolean isReadOnly()
  {
    return readOnly;
  }

  @Deprecated
  @Override
  public Map<CDOID, LockGrade> getLocks()
  {
    return locks;
  }

  @Deprecated
  @Override
  public String toString()
  {
    return MessageFormat.format("DurableLockArea\nid={0}\nuser={1}\nbranchPoint={2}\nreadOnly={3}\nlocks={4}", durableLockingID, userID, branchPoint, readOnly,
        locks);
  }

  @Deprecated
  public static String createDurableLockingID()
  {
    return CDOLockUtil.createDurableLockingID();
  }

  @Deprecated
  public static String createDurableLockingID(int bytes)
  {
    return CDOLockUtil.createDurableLockingID(bytes);
  }

  /**
   * @since 4.1
   */
  @Deprecated
  @Override
  public boolean isMissing()
  {
    return false;
  }
}
