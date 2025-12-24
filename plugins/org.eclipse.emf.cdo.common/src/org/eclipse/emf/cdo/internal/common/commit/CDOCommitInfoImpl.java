/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreationContext;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.core.runtime.IAdaptable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOCommitInfoImpl extends CDOBranchPointImpl implements CDOCommitInfo, IAdaptable
{
  // private static final CDOCommitInfo[] NO_PARENTS = {};

  private final InternalCDOCommitInfoManager commitInfoManager;

  private final long previousTimeStamp;

  private final String userID;

  private final String comment;

  private final CDOBranchPoint mergeSource;

  private CDOCommitInfo mergedCommitInfo;

  private CDOCommitData commitData;

  // private transient CDOCommitInfo[] parents;

  public CDOCommitInfoImpl(InternalCDOCommitInfoManager commitInfoManager, CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, CDOBranchPoint mergeSource, CDOCommitData commitData)
  {
    super(branch, timeStamp);
    CheckUtil.checkArg(commitInfoManager, "commitInfoManager"); //$NON-NLS-1$
    this.commitInfoManager = commitInfoManager;
    this.previousTimeStamp = previousTimeStamp;
    this.userID = userID;
    this.comment = comment;
    this.mergeSource = mergeSource;
    this.commitData = commitData;
  }

  @Override
  public InternalCDOCommitInfoManager getCommitInfoManager()
  {
    return commitInfoManager;
  }

  // public synchronized CDOCommitInfo[] getParents()
  // {
  // if (parents == null)
  // {
  // CDOCommitInfo previousCommitInfo = commitInfoManager.getCommitInfo(previousTimeStamp);
  // if (previousCommitInfo != null)
  // {
  // parents = new CDOCommitInfo[] { previousCommitInfo };
  // }
  // else
  // {
  // parents = NO_PARENTS;
  // }
  // }
  //
  // return parents;
  // }

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
  public String getUserID()
  {
    return userID;
  }

  @Override
  public String getComment()
  {
    return comment;
  }

  @Override
  public CDOBranchPoint getMergeSource()
  {
    return mergeSource;
  }

  @Override
  public CDOCommitInfo getMergedCommitInfo()
  {
    if (mergeSource == null)
    {
      return null;
    }

    if (mergedCommitInfo == null)
    {
      mergedCommitInfo = commitInfoManager.getCommitInfo(mergeSource.getBranch(), mergeSource.getTimeStamp(), false);
    }

    return mergedCommitInfo;
  }

  @Override
  public boolean isInitialCommit()
  {
    return CDOCommonUtil.SYSTEM_USER_ID.equals(userID);
  }

  @Override
  public boolean isEmpty()
  {
    loadCommitDataIfNeeded();
    return commitData.isEmpty();
  }

  @Override
  public CDOChangeSetData copy()
  {
    return commitData == null ? null : commitData.copy();
  }

  @Override
  public void merge(CDOChangeSetData changeSetData)
  {
    loadCommitDataIfNeeded();
    commitData.merge(changeSetData);
  }

  @Override
  public synchronized List<CDOPackageUnit> getNewPackageUnits()
  {
    loadCommitDataIfNeeded();
    return commitData.getNewPackageUnits();
  }

  @Override
  public synchronized List<CDOIDAndVersion> getNewObjects()
  {
    loadCommitDataIfNeeded();
    return commitData.getNewObjects();
  }

  @Override
  public synchronized List<CDORevisionKey> getChangedObjects()
  {
    loadCommitDataIfNeeded();
    return commitData.getChangedObjects();
  }

  @Override
  public synchronized List<CDOIDAndVersion> getDetachedObjects()
  {
    loadCommitDataIfNeeded();
    return commitData.getDetachedObjects();
  }

  @Override
  public synchronized List<CDOID> getAffectedIDs()
  {
    loadCommitDataIfNeeded();
    return commitData.getAffectedIDs();
  }

  @Override
  public Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    loadCommitDataIfNeeded();
    return commitData.getChangeKinds();
  }

  @Override
  public CDOChangeKind getChangeKind(CDOID id)
  {
    loadCommitDataIfNeeded();
    return commitData.getChangeKind(id);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    if (adapter == CDOBranchCreationContext.class)
    {
      CDOCommonRepository repository = getBranch().getBranchManager().getRepository();
      if (repository.isSupportingBranches())
      {
        return new CDOBranchCreationContext()
        {
          @Override
          public CDOBranchPoint getBase()
          {
            return CDOCommitInfoImpl.this;
          }
        };
      }
    }

    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public int hashCode()
  {
    long timeStamp = getTimeStamp();

    final int prime = 31;
    int result = 0;
    result = prime * result + (commitInfoManager == null ? 0 : commitInfoManager.hashCode());
    result = prime * result + (int)(timeStamp ^ timeStamp >>> 32);
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (!(obj instanceof CDOCommitInfoImpl))
    {
      return false;
    }

    CDOCommitInfoImpl other = (CDOCommitInfoImpl)obj;
    if (commitInfoManager != other.commitInfoManager)
    {
      return false;
    }

    if (getTimeStamp() != other.getTimeStamp())
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return toString(this);
  }

  public static String toString(CDOCommitInfo commitInfo)
  {
    String data = null;
    if (commitInfo.isCommitDataLoaded())
    {
      data = CDOCommitDataImpl.toString(commitInfo);
    }

    long t = commitInfo.getTimeStamp();
    String timeStamp = CDOCommonUtil.formatTimeStamp(t) + " (" + t + ")";

    return MessageFormat.format("CommitInfo[{0}, {1}, {2}, {3}, {4}, {5}]", timeStamp, commitInfo.getBranch(), //$NON-NLS-1$
        commitInfo.getUserID(), commitInfo.getComment(), commitInfo.getMergeSource(), data);
  }

  @Override
  public synchronized boolean isCommitDataLoaded()
  {
    return commitData != null;
  }

  private synchronized void loadCommitDataIfNeeded()
  {
    if (commitData == null)
    {
      long timeStamp = getTimeStamp();

      CommitInfoLoader commitInfoLoader = commitInfoManager.getCommitInfoLoader();
      commitData = commitInfoLoader.loadCommitData(timeStamp);
    }
  }
}
