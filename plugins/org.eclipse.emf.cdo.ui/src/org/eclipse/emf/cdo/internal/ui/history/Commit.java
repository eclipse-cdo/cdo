/*
 * Copyright (c) 2012, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Eike Stepper
 */
public final class Commit
{
  private final CDOCommitInfo commitInfo;

  private final Segment segment;

  private Segment[] rowSegments;

  private int commitCounter = -1;

  private Commit mergeSource;

  private List<Commit> mergeTargets;

  private Segment mergeSegment;

  public Commit(CDOCommitInfo commitInfo, Segment segment)
  {
    this.segment = segment;
    this.commitInfo = commitInfo;

    CDOCommitInfo mergedCommitInfo = commitInfo.getMergedCommitInfo();
    if (mergedCommitInfo != null)
    {
      Net net = getNet();

      mergeSource = net.getCommit(mergedCommitInfo);
      if (mergeSource != null)
      {
        mergeSource.addMergeTargets(Collections.singletonList(this));
      }
      else
      {
        net.addDanglingMergeTarget(mergedCommitInfo, this);
      }
    }
  }

  public CDOCommitInfo getCommitInfo()
  {
    return commitInfo;
  }

  public Net getNet()
  {
    return segment.getNet();
  }

  public Track getTrack()
  {
    return segment.getTrack();
  }

  public Branch getBranch()
  {
    return segment.getBranch();
  }

  public long getTime()
  {
    return commitInfo.getTimeStamp();
  }

  public Commit getMergeSource()
  {
    return mergeSource;
  }

  public List<Commit> getMergeTargets()
  {
    return mergeTargets;
  }

  public Segment getMergeSegment()
  {
    return mergeSegment;
  }

  public final Segment getSegment()
  {
    return segment;
  }

  public final Segment[] getRowSegments()
  {
    Net net = segment.getNet();
    int netCommitCounter = net.getCommitCounter();

    if (rowSegments == null || commitCounter < netCommitCounter)
    {
      long time = getTime();
      rowSegments = net.createRowSegments(time);

      // int xxx;
      // String dump = "row = " + time + " --> ";
      // for (Segment segment : rowSegments)
      // {
      // if (segment == null)
      // {
      // dump += " ";
      // }
      // else if (segment.isMerge())
      // {
      // dump += ":";
      // }
      // else
      // {
      // dump += "|";
      // }
      // }
      // System.out.println(dump);

      commitCounter = netCommitCounter;
    }

    return rowSegments;
  }

  public final boolean isFirstInBranch()
  {
    long firstTime = segment.getBranch().getFirstCommitTime();
    return getTime() == firstTime;
  }

  public final boolean isLastInBranch()
  {
    long lastTime = segment.getBranch().getLastCommitTime();
    return getTime() == lastTime;
  }

  @Override
  public String toString()
  {
    return "Commit[" + getTime() + " --> " + segment + "]";
  }

  void setMergeSource(Commit mergeSource)
  {
    this.mergeSource = mergeSource;
  }

  void addMergeTargets(List<Commit> mergeTargets)
  {
    for (Commit mergeTarget : mergeTargets)
    {
      addMergeTargetToList(mergeTarget);
    }

    computeMergeSegment();
  }

  private void addMergeTargetToList(Commit mergeTarget)
  {
    if (mergeTargets == null)
    {
      mergeTargets = new ArrayList<>(1);
    }
    else
    {
      for (ListIterator<Commit> it = mergeTargets.listIterator(); it.hasNext();)
      {
        Commit commit = it.next();
        if (commit.getTime() > mergeTarget.getTime())
        {
          it.previous();
          it.add(mergeTarget);
          return;
        }
      }
    }

    mergeTargets.add(mergeTarget);
  }

  private void computeMergeSegment()
  {
    if (mergeSegment != null)
    {
      mergeSegment.getTrack().removeSegment(mergeSegment);
      mergeSegment = null;
    }

    Commit lastMergeTarget = mergeTargets.get(mergeTargets.size() - 1);
    long lastMergeTime = lastMergeTarget.getTime();

    Net net = getNet();
    // Track track = getTrack();
    // Branch branch = getBranch();
    long commitTime = getTime();

    // if (isLastInBranch() && !track.hasSegment(commitTime + 1, lastMergeTime))
    // {
    // mergeSegment = new Segment(track, branch, this);
    // mergeSegment.adjustCommitTimes(commitTime + 1);
    // mergeSegment.adjustCommitTimes(lastMergeTime);
    // track.addSegment(mergeSegment, true);
    // }
    // else // if (net.hasBranchCommitBetween(mergeTarget.getBranch(), commitTime, lastMergeTime))
    // {
    mergeSegment = net.createMergeSegment(this, lastMergeTime);
    mergeSegment.adjustCommitTimes(commitTime);
    mergeSegment.adjustCommitTimes(lastMergeTime);
    // }
  }
}
