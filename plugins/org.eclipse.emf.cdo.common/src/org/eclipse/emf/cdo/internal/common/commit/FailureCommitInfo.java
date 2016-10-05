/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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

  public CDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public long getPreviousTimeStamp()
  {
    return previousTimeStamp;
  }

  public CDOCommitInfo getPreviousCommitInfo()
  {
    return previousTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE ? null
        : commitInfoManager.getCommitInfo(previousTimeStamp);
  }

  public CDOBranch getBranch()
  {
    return null;
  }

  public String getUserID()
  {
    return null;
  }

  public String getComment()
  {
    return null;
  }

  public CDOBranchPoint getMergeSource()
  {
    return null;
  }

  public CDOCommitInfo getMergedCommitInfo()
  {
    return null;
  }

  public boolean isEmpty()
  {
    return true;
  }

  public boolean isInitialCommit()
  {
    return false;
  }

  public boolean isCommitDataLoaded()
  {
    return true;
  }

  public List<CDOPackageUnit> getNewPackageUnits()
  {
    return Collections.emptyList();
  }

  public List<CDOIDAndVersion> getNewObjects()
  {
    return Collections.emptyList();
  }

  public List<CDORevisionKey> getChangedObjects()
  {
    return Collections.emptyList();
  }

  public List<CDOIDAndVersion> getDetachedObjects()
  {
    return Collections.emptyList();
  }

  public Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    return Collections.emptyMap();
  }

  public CDOChangeKind getChangeKind(CDOID id)
  {
    return null;
  }

  public CDOChangeSetData copy()
  {
    return this;
  }

  public void merge(CDOChangeSetData changeSetData)
  {
    throw new UnsupportedOperationException();
  }
}
