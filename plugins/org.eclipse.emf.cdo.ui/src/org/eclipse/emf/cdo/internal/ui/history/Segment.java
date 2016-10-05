/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.history;

/**
 * @author Eike Stepper
 */
public final class Segment
{
  private final Track track;

  private final Branch branch;

  private final Commit mergeSource;

  private boolean complete;

  private long firstVisualTime;

  private long firstCommitTime;

  private long lastCommitTime;

  private Segment previousInTrack;

  private Segment nextInTrack;

  private Segment previousInBranch;

  private Segment nextInBranch;

  public Segment(Track track, Branch branch, Commit mergeSource)
  {
    this.track = track;
    this.branch = branch;
    this.mergeSource = mergeSource;
  }

  public Net getNet()
  {
    return track.getNet();
  }

  public Track getTrack()
  {
    return track;
  }

  public Branch getBranch()
  {
    return branch;
  }

  public Commit getMergeSource()
  {
    return mergeSource;
  }

  public boolean isMerge()
  {
    return mergeSource != null;
  }

  public boolean isComplete()
  {
    return complete;
  }

  public long getFirstVisualTime()
  {
    return firstVisualTime;
  }

  public long getFirstCommitTime()
  {
    return firstCommitTime;
  }

  public long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public Segment getPreviousInTrack()
  {
    return previousInTrack;
  }

  public Segment getNextInTrack()
  {
    return nextInTrack;
  }

  public Segment getPreviousInBranch()
  {
    return previousInBranch;
  }

  public Segment getNextInBranch()
  {
    return nextInBranch;
  }

  public boolean containsCommitTime(long time)
  {
    return time >= firstCommitTime && time <= lastCommitTime;
  }

  public boolean containsVisualTime(long time)
  {
    return time >= getFirstVisualTime() && time <= lastCommitTime;
  }

  @Override
  public String toString()
  {
    return "Segment[" + branch + " --> " + track + "]";
  }

  void adjustVisualTime(long time, boolean complete)
  {
    if (isMerge())
    {
      firstVisualTime = firstCommitTime;
      this.complete = true;
    }
    else
    {
      if (firstVisualTime == 0 || time < firstVisualTime)
      {
        firstVisualTime = time;
      }
    }

    this.complete |= complete;
  }

  void adjustCommitTimes(long time)
  {
    adjustVisualTime(time, false);
    if (firstCommitTime == 0)
    {
      firstCommitTime = time;
      lastCommitTime = time;
    }
    else if (time < firstCommitTime)
    {
      firstCommitTime = time;
    }
    else if (time > lastCommitTime)
    {
      lastCommitTime = time;
    }
  }

  void setPreviousInTrack(Segment previousInTrack)
  {
    this.previousInTrack = previousInTrack;
  }

  void setNextInTrack(Segment nextInTrack)
  {
    this.nextInTrack = nextInTrack;
  }

  void setPreviousInBranch(Segment previousInBranch)
  {
    this.previousInBranch = previousInBranch;
  }

  void setNextInBranch(Segment nextInBranch)
  {
    this.nextInBranch = nextInBranch;
  }
}
