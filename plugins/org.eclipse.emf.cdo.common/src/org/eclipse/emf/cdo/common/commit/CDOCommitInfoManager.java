/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;

/**
 * Provides access to {@link CDOCommitInfo commit info} objects.
 *
 * @author Andre Dietisheim
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOCommitInfoManager extends CDOCommitInfoProvider, CDOCommitHistory.Provider<CDOBranch, CDOCommitHistory>
{
  /**
   * @since 4.2
   */
  public CDOCommonRepository getRepository();

  /**
   * Same as calling {@link #getCommitInfo(long, boolean) getCommitInfo(timeStamp, true)}.
   *
   * @since 4.0
   */
  public CDOCommitInfo getCommitInfo(long timeStamp);

  /**
   * Returns the commit info for the given time stamp. If no such commit info is locally available, it is loaded from
   * the repository if <code>loadOnDemand</code> is <code>true</code>.
   * Note that this method may return <code>null</code> even if <code>loadOnDemand</code> is <code>true</code> in case no
   * such commit info exists in the repository.
   *
   * @param timeStamp the time stamp of the commit info to retrieve.
   * @param loadOnDemand whether to load the commit info from the repository if not locally available.
   * @return the commit info for the given time stamp or <code>null</code> if no such commit info exists.
   * @since 4.6
   */
  public CDOCommitInfo getCommitInfo(long timeStamp, boolean loadOnDemand);

  /**
   *
   * @since 4.2
   */
  public CDOCommitInfo getCommitInfo(CDOBranch branch, long startTime, boolean up);

  public void getCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler);

  /**
   * @since 4.0
   */
  public void getCommitInfos(CDOBranch branch, long startTime, String reserved1, String reserved2, int count, CDOCommitInfoHandler handler);

  /**
   * @since 4.2
   */
  public CDOCommitInfo getBaseOfBranch(CDOBranch branch);

  /**
   * @since 4.2
   */
  public CDOCommitInfo getFirstOfBranch(CDOBranch branch);

  /**
   * @since 4.2
   */
  public CDOCommitInfo getLastOfBranch(CDOBranch branch);

  /**
   * @since 4.6
   */
  public long getLastCommitOfBranch(CDOBranch branch, boolean loadOnDemand);

  /**
   * @since 4.6
   */
  public long getLastCommit();
}
