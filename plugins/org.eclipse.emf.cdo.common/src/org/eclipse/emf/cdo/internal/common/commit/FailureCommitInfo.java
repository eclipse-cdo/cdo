/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class FailureCommitInfo implements CDOCommitInfo
{
  private final InternalCDOCommitInfoManager commitInfoManager;

  private final long timeStamp;

  private final long previousTimeStamp;

  public FailureCommitInfo(InternalCDOCommitInfoManager commitInfoManager, long timeStamp, long previousTimeStamp)
  {
    this.commitInfoManager = commitInfoManager;
    this.timeStamp = timeStamp;
    this.previousTimeStamp = previousTimeStamp;
  }

  @Override
  public CDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public long getPreviousTimeStamp()
  {
    return previousTimeStamp;
  }

  @Override
  public CDOCommitInfo getPreviousCommitInfo()
  {
    return previousTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE ? null : commitInfoManager.getCommitInfo(previousTimeStamp);
  }

  @Override
  public CDOBranch getBranch()
  {
    return null;
  }

  @Override
  public String getUserID()
  {
    return null;
  }

  @Override
  public String getComment()
  {
    return null;
  }

  @Override
  public CDOBranchPoint getMergeSource()
  {
    return null;
  }

  @Override
  public CDOCommitInfo getMergedCommitInfo()
  {
    return null;
  }

  @Override
  public boolean isEmpty()
  {
    return true;
  }

  @Override
  public boolean isInitialCommit()
  {
    return false;
  }

  @Override
  public boolean isCommitDataLoaded()
  {
    return true;
  }

  @Override
  public List<CDOPackageUnit> getNewPackageUnits()
  {
    return Collections.emptyList();
  }

  @Override
  public List<CDOIDAndVersion> getNewObjects()
  {
    return Collections.emptyList();
  }

  @Override
  public List<CDORevisionKey> getChangedObjects()
  {
    return Collections.emptyList();
  }

  @Override
  public List<CDOIDAndVersion> getDetachedObjects()
  {
    return Collections.emptyList();
  }

  @Override
  public Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    return Collections.emptyMap();
  }

  @Override
  public CDOChangeKind getChangeKind(CDOID id)
  {
    return null;
  }

  @Override
  public CDOChangeSetData copy()
  {
    return this;
  }

  @Override
  public void merge(CDOChangeSetData changeSetData)
  {
    throw new UnsupportedOperationException();
  }
}
