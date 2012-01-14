/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */

package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OCommitInfo
{
  private int branchID;

  private long timeStamp;

  private long previousTimeStamp;

  private String userID;

  private String comment;

  public DB4OCommitInfo(int branchID, long timeStamp, long previousTimeStamp, String userID, String comment)
  {
    this.branchID = branchID;
    this.timeStamp = timeStamp;
    this.previousTimeStamp = previousTimeStamp;
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
    InternalCDOBranch branch = branchManager.getBranch(branchID);
    CDOCommitInfo commitInfo = manager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, null);
    handler.handleCommitInfo(commitInfo);
  }
}
