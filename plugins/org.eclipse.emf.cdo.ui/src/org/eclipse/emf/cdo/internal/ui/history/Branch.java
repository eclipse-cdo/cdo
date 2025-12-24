/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.swt.graphics.Color;

/**
 * @author Eike Stepper
 */
public final class Branch extends SegmentList
{
  private final CDOBranch cdoBranch;

  private final Color color;

  private Branch baseCommitBranch;

  private long baseCommitTime;

  private long firstCommitTime;

  private long lastCommitTime;

  public Branch(Net net, CDOBranch cdoBranch)
  {
    super(net);
    this.cdoBranch = cdoBranch;
    color = net.getColor(cdoBranch.getID());

    CDOSession session = net.getSession();
    CDOID objectID = net.getObjectID();
    if (objectID != null)
    {
      CDORevisionManager revisionManager = session.getRevisionManager();

      CDOBranchPoint lastPoint = cdoBranch.getHead();
      CDORevision lastRevision = revisionManager.getRevision(objectID, lastPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);

      if (lastRevision != null && lastRevision.getBranch() == cdoBranch)
      {
        CDOBranchVersion firstVersion = cdoBranch.getVersion(CDOBranchVersion.FIRST_VERSION);
        CDORevision firstRevision = revisionManager.getRevisionByVersion(objectID, firstVersion, CDORevision.UNCHUNKED, true);

        firstCommitTime = firstRevision.getTimeStamp();
        lastCommitTime = lastRevision.getTimeStamp();
      }
    }
    else
    {
      CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      CDOCommitInfo firstCommit = commitInfoManager.getFirstOfBranch(cdoBranch);
      if (firstCommit != null)
      {
        firstCommitTime = firstCommit.getTimeStamp();
      }

      lastCommitTime = commitInfoManager.getLastCommitOfBranch(cdoBranch, true);
    }
  }

  public CDOBranch getCDOBranch()
  {
    return cdoBranch;
  }

  public Color getColor()
  {
    return color;
  }

  @Deprecated
  public Branch getBaseBranch()
  {
    CDOBranchPoint cdoBase = cdoBranch.getBase();
    CDOBranch cdoBaseBranch = cdoBase.getBranch();
    if (cdoBaseBranch == null)
    {
      return null;
    }

    Net net = getNet();
    return net.getBranch(cdoBaseBranch);
  }

  @Deprecated
  public long getBaseTime()
  {
    return cdoBranch.getBase().getTimeStamp();
  }

  public Branch getBaseCommitBranch()
  {
    determineBaseCommitIfNeeded();
    return baseCommitBranch;
  }

  public long getBaseCommitTime()
  {
    determineBaseCommitIfNeeded();
    return baseCommitTime;
  }

  public long getFirstCommitTime()
  {
    return firstCommitTime;
  }

  public long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public Segment getSegmentFrom(Segment[] segments)
  {
    for (int i = 0; i < segments.length; i++)
    {
      Segment segment = segments[i];
      if (segment != null && segment.getBranch() == this)
      {
        return segment;
      }
    }

    return null;
  }

  @Override
  public String toString()
  {
    return "Branch[" + cdoBranch.getPathName() + "]";
  }

  void adjustCommitTimes(long time)
  {
    if (firstCommitTime == 0)
    {
      firstCommitTime = time;
      lastCommitTime = time;
    }
    else if (time < firstCommitTime)
    {
      throw new RuntimeException("Must not happen because the constructor initializes firstCommitTime");
      // firstCommitTime = time;
    }
    else if (time > lastCommitTime)
    {
      lastCommitTime = time;
    }
  }

  private void determineBaseCommitIfNeeded()
  {
    if (baseCommitTime != 0 || cdoBranch.isMainBranch())
    {
      return;
    }

    CDOCommitInfoManager commitInfoManager = getNet().getSession().getCommitInfoManager();
    CDOCommitInfo commitInfo = commitInfoManager.getBaseOfBranch(cdoBranch);
    baseCommitBranch = getNet().getBranch(commitInfo.getBranch());
    baseCommitTime = commitInfo.getTimeStamp();
  }

  @Override
  protected Segment getNextSegment(Segment segment)
  {
    return segment.getNextInBranch();
  }

  @Override
  protected void setNextSegment(Segment segment, Segment nextSegment)
  {
    segment.setNextInBranch(nextSegment);
  }

  @Override
  protected Segment getPreviousSegment(Segment segment)
  {
    return segment.getPreviousInBranch();
  }

  @Override
  protected void setPreviousSegment(Segment segment, Segment previousSegment)
  {
    segment.setPreviousInBranch(previousSegment);
  }
}
