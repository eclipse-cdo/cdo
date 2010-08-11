/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */

package org.eclipse.emf.cdo.internal.server.db4o;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

/**
 * @author Victor Roldan Betancort
 */
public class CommitInfo
{
  private int branchID;

  private long timeStamp;

  private String userID;

  private String comment;

  public CommitInfo(int branchId, long timeStamp, String userID, String comment)
  {
    branchID = branchId;
    this.timeStamp = timeStamp;
    this.userID = userID;
    this.comment = comment;
  }

  public int getBranchID()
  {
    return branchID;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void handle(InternalCDOBranchManager branchManager, InternalCDOCommitInfoManager manager,
      CDOCommitInfoHandler handler)
  {
    CDOCommitInfo commitInfo = manager.createCommitInfo(branchManager.getBranch(branchID), timeStamp, userID, comment,
        null);
    handler.handleCommitInfo(commitInfo);
  }
}
