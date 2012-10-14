/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;

import org.eclipse.swt.graphics.Color;

/**
 * @author Eike Stepper
 */
public class Branch extends SegmentList
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

    CDOID objectID = net.getObjectID();
    if (objectID != null)
    {
      CDORevisionManager revisionManager = net.getSession().getRevisionManager();

      CDOBranchPoint lastPoint = cdoBranch.getHead();
      CDORevision lastRevision = revisionManager.getRevision(objectID, lastPoint, CDORevision.UNCHUNKED,
          CDORevision.DEPTH_NONE, true);

      if (lastRevision != null && lastRevision.getBranch() == cdoBranch)
      {
        CDOBranchVersion firstVersion = cdoBranch.getVersion(CDOBranchVersion.FIRST_VERSION);
        CDORevision firstRevision = revisionManager.getRevisionByVersion(objectID, firstVersion, CDORevision.UNCHUNKED,
            true);

        firstCommitTime = firstRevision.getTimeStamp();
        lastCommitTime = lastRevision.getTimeStamp();
      }
    }
    else
    {
      firstCommitTime = getCommitTime(1L, true);
      lastCommitTime = getCommitTime(Long.MAX_VALUE, false);
    }
  }

  public final CDOBranch getCDOBranch()
  {
    return cdoBranch;
  }

  public final Color getColor()
  {
    return color;
  }

  @Deprecated
  public final Branch getBaseBranch()
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
  public final long getBaseTime()
  {
    return cdoBranch.getBase().getTimeStamp();
  }

  public final Branch getBaseCommitBranch()
  {
    determineBaseCommitIfNeeded();
    return baseCommitBranch;
  }

  public final long getBaseCommitTime()
  {
    determineBaseCommitIfNeeded();
    return baseCommitTime;
  }

  public final long getFirstCommitTime()
  {
    return firstCommitTime;
  }

  public final long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public final Segment getSegmentFrom(Segment[] segments)
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

  private long getCommitTime(long startTime, boolean up)
  {
    CDOCommitInfoManager commitInfoManager = getNet().getSession().getCommitInfoManager();
    CDOCommitInfo commitInfo = commitInfoManager.getCommitInfo(cdoBranch, startTime, up);
    if (commitInfo != null)
    {
      return commitInfo.getTimeStamp();
    }

    return 0;
  }

  private void determineBaseCommitIfNeeded()
  {
    if (baseCommitTime != 0 || cdoBranch.isMainBranch())
    {
      return;
    }

    CDOBranchPoint base = cdoBranch.getBase();
    while (baseCommitTime == 0)
    {
      CDOBranch baseBranch = base.getBranch();
      long baseTime = base.getTimeStamp();

      final Net net = getNet();
      CDOCommitInfoManager commitInfoManager = net.getSession().getCommitInfoManager();
      commitInfoManager.getCommitInfos(baseBranch, baseTime, null, null, -1, new CDOCommitInfoHandler()
      {
        public void handleCommitInfo(CDOCommitInfo commitInfo)
        {
          CDOBranch cdoBaseBranch = commitInfo.getBranch();
          baseCommitBranch = net.getBranch(cdoBaseBranch);
          baseCommitTime = commitInfo.getTimeStamp();
        }
      });

      base = baseBranch.getBase();
    }
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
