/*
 * Copyright (c) 2010-2013, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.commit;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOCommitInfoManager extends CDOCommitInfoManager, ILifecycle
{
  /**
   * @since 4.2
   */
  public void setRepository(CDOCommonRepository repository);

  /**
   * @since 4.15
   */
  public CDOBranchManager getBranchManager();

  /**
   * @since 4.15
   */
  public void setBranchManager(CDOBranchManager branchManager);

  public CommitInfoLoader getCommitInfoLoader();

  public void setCommitInfoLoader(CommitInfoLoader commitInfoLoader);

  /**
   * @since 4.2
   */
  public void notifyCommitInfoHandlers(CDOCommitInfo commitInfo);

  /**
   * @since 4.0
   */
  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, CDOCommitData commitData);

  /**
   * @since 4.6
   */
  public CDOCommitInfo createCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, CDOBranchPoint mergeSource,
      CDOCommitData commitData);

  /**
   * @since 4.6
   */
  public void setLastCommitOfBranch(CDOBranch branch, long lastCommit);

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public interface CommitInfoLoader
  {
    public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler);

    public CDOCommitData loadCommitData(long timeStamp);
  }
}
