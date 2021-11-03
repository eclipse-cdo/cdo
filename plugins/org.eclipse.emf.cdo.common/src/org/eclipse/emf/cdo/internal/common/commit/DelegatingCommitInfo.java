/*
 * Copyright (c) 2010-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingCommitInfo implements CDOCommitInfo
{
  public DelegatingCommitInfo()
  {
  }

  protected abstract CDOCommitInfo getDelegate();

  @Override
  public CDOBranch getBranch()
  {
    return getDelegate().getBranch();
  }

  @Override
  public CDOCommitInfoManager getCommitInfoManager()
  {
    return getDelegate().getCommitInfoManager();
  }

  @Override
  public long getPreviousTimeStamp()
  {
    return getDelegate().getPreviousTimeStamp();
  }

  @Override
  public CDOCommitInfo getPreviousCommitInfo()
  {
    return getDelegate().getPreviousCommitInfo();
  }

  @Override
  public long getTimeStamp()
  {
    return getDelegate().getTimeStamp();
  }

  @Override
  public String getUserID()
  {
    return getDelegate().getUserID();
  }

  @Override
  public String getComment()
  {
    return getDelegate().getComment();
  }

  @Override
  public CDOBranchPoint getMergeSource()
  {
    return getDelegate().getMergeSource();
  }

  @Override
  public CDOCommitInfo getMergedCommitInfo()
  {
    return getDelegate().getMergedCommitInfo();
  }

  @Override
  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  @Override
  public boolean isInitialCommit()
  {
    return getDelegate().isInitialCommit();
  }

  @Override
  public boolean isCommitDataLoaded()
  {
    return getDelegate().isCommitDataLoaded();
  }

  @Override
  public List<CDOPackageUnit> getNewPackageUnits()
  {
    return getDelegate().getNewPackageUnits();
  }

  @Override
  public List<CDOIDAndVersion> getNewObjects()
  {
    return getDelegate().getNewObjects();
  }

  @Override
  public List<CDORevisionKey> getChangedObjects()
  {
    return getDelegate().getChangedObjects();
  }

  @Override
  public List<CDOIDAndVersion> getDetachedObjects()
  {
    return getDelegate().getDetachedObjects();
  }

  @Override
  public List<CDOID> getAffectedIDs()
  {
    return getDelegate().getAffectedIDs();
  }

  @Override
  public Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    return getDelegate().getChangeKinds();
  }

  @Override
  public CDOChangeKind getChangeKind(CDOID id)
  {
    return getDelegate().getChangeKind(id);
  }

  @Override
  public CDOChangeSetData copy()
  {
    return getDelegate().copy();
  }

  @Override
  public void merge(CDOChangeSetData changeSetData)
  {
    getDelegate().merge(changeSetData);
  }

  @Override
  public String toString()
  {
    return getDelegate().toString();
  }
}
