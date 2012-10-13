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

/**
 * @author Eike Stepper
 */
public class Segment
{
  private final Track track;

  private final Branch branch;

  private boolean complete;

  private long firstVisualTime;

  private long firstCommitTime;

  private long lastCommitTime;

  private Segment previousInTrack;

  private Segment nextInTrack;

  private Segment previousInBranch;

  private Segment nextInBranch;

  public Segment(Track track, Branch branch)
  {
    this.track = track;
    this.branch = branch;
  }

  public final Net getNet()
  {
    return track.getNet();
  }

  public final Track getTrack()
  {
    return track;
  }

  public final Branch getBranch()
  {
    return branch;
  }

  public final boolean isComplete()
  {
    return complete;
  }

  public final long getFirstVisualTime()
  {
    return firstVisualTime;
  }

  public final long getFirstCommitTime()
  {
    return firstCommitTime;
  }

  public final long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public final Segment getPreviousInTrack()
  {
    return previousInTrack;
  }

  public final Segment getNextInTrack()
  {
    return nextInTrack;
  }

  public final Segment getPreviousInBranch()
  {
    return previousInBranch;
  }

  public final Segment getNextInBranch()
  {
    return nextInBranch;
  }

  public final boolean containsCommitTime(long time)
  {
    return time >= firstCommitTime && time <= lastCommitTime;
  }

  public final boolean containsVisualTime(long time)
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
    if (firstVisualTime == 0 || time < firstVisualTime)
    {
      firstVisualTime = time;
    }

    this.complete |= complete;
  }

  void adjustCommitTimes(long time)
  {
    if (firstVisualTime == 0)
    {
      firstVisualTime = time;
    }

    if (firstCommitTime == 0)
    {
      firstCommitTime = time;
      lastCommitTime = time;
    }
    else if (time < firstCommitTime)
    {
      firstCommitTime = time;
      if (time < firstVisualTime)
      {
        firstVisualTime = time;
      }
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
