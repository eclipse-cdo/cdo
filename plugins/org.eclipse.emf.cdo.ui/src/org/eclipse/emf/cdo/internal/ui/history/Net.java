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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class Net
{
  private static final Segment[] NO_SEGMENTS = {};

  private final CDOSession session;

  private final CDOID objectID;

  private final ResourceManager resourceManager;

  private Color[] colors;

  private int nextColorIndex;

  private Track[] tracks = {};

  private Map<CDOBranch, Branch> branches = new HashMap<CDOBranch, Branch>();

  private Map<CDOCommitInfo, Commit> commits = new WeakHashMap<CDOCommitInfo, Commit>();

  private Commit firstCommit;

  private Commit lastCommit;

  public Net(CDOSession session, CDOID objectID, ResourceManager resourceManager)
  {
    this.session = session;
    this.objectID = objectID;
    this.resourceManager = resourceManager;
    System.out.println("New: " + this);
  }

  public final CDOSession getSession()
  {
    return session;
  }

  public final CDOID getObjectID()
  {
    return objectID;
  }

  public final Color getNextColor()
  {
    if (colors == null)
    {
      final RGB[] rgbs = new RGB[] { new RGB(133, 166, 214), new RGB(221, 205, 93), new RGB(199, 134, 57),
          new RGB(131, 150, 98), new RGB(197, 123, 127), new RGB(139, 136, 140), new RGB(48, 135, 144),
          new RGB(190, 93, 66), new RGB(143, 163, 54), new RGB(180, 148, 74), new RGB(101, 101, 217),
          new RGB(72, 153, 119), new RGB(23, 101, 160), new RGB(132, 164, 118), new RGB(255, 230, 59),
          new RGB(136, 176, 70), new RGB(255, 138, 1), new RGB(123, 187, 95), new RGB(233, 88, 98),
          new RGB(93, 158, 254), new RGB(175, 215, 0), new RGB(140, 134, 142), new RGB(232, 168, 21),
          new RGB(0, 172, 191), new RGB(251, 58, 4), new RGB(63, 64, 255), new RGB(27, 194, 130), new RGB(0, 104, 183) };

      colors = new Color[rgbs.length];
      for (int i = 0; i < rgbs.length; i++)
      {
        RGB rgb = rgbs[i];
        colors[i] = resourceManager.createColor(rgb);
      }
    }

    if (nextColorIndex >= colors.length)
    {
      nextColorIndex = 0;
    }

    return colors[nextColorIndex++];
  }

  public final Track[] getTracks()
  {
    return tracks;
  }

  public final Segment[] getRowSegments(long time)
  {
    Segment[] segments = NO_SEGMENTS;
    for (int i = tracks.length - 1; i >= 0; --i)
    {
      Track track = tracks[i];
      Segment segment = track.getSegment(time, true);

      if (segments == NO_SEGMENTS)
      {
        if (segment == null)
        {
          continue;
        }

        segments = new Segment[i + 1];
      }

      segments[i] = segment;
    }

    return segments;
  }

  public final Commit getFirstCommit()
  {
    return firstCommit;
  }

  public final Commit getLastCommit()
  {
    return lastCommit;
  }

  @Override
  public String toString()
  {
    return "Net[" + session + ", " + objectID + "]";
  }

  public final Branch getBranch(CDOBranch cdoBranch)
  {
    Branch branch = branches.get(cdoBranch);
    if (branch == null)
    {
      branch = new Branch(this, cdoBranch);
      branches.put(cdoBranch, branch);
    }

    return branch;
  }

  public final Commit getCommit(CDOCommitInfo commitInfo)
  {
    Commit commit = commits.get(commitInfo);
    if (commit == null)
    {
      commit = createCommit(commitInfo);
      commits.put(commitInfo, commit);
    }

    return commit;
  }

  private Commit createCommit(CDOCommitInfo commitInfo)
  {
    System.out.println("Enter: Commit[" + commitInfo.getTimeStamp() + "]");

    Segment segment = getSegment(commitInfo);
    if (segment == null)
    {
      throw new IllegalStateException("No segment");
    }

    Commit commit = new Commit(commitInfo, segment);

    if (firstCommit == null)
    {
      firstCommit = commit;
      lastCommit = commit;
    }
    else
    {
      long time = commit.getTime();
      if (time < firstCommit.getTime())
      {
        firstCommit = commit;
      }
      else if (time > lastCommit.getTime())
      {
        lastCommit = commit;
      }
    }

    return commit;
  }

  private final Segment getSegment(CDOCommitInfo commitInfo)
  {
    CDOBranch cdoBranch = commitInfo.getBranch();
    long time = commitInfo.getTimeStamp();

    Branch branch = getBranch(cdoBranch);
    Segment segment = branch.getSegment(time, false);

    if (segment == null)
    {
      // This must be a new commit after the last or before the first
      boolean afterLast = isAfterLast(time); // false means beforeFirst
      segment = getOrCreateSegment(branch, time, afterLast);
    }
    else
    {
      System.out.println("Match: " + segment);
    }

    branch.adjustCommitTimes(time);
    segment.adjustCommitTimes(time);
    return segment;
  }

  private Segment getOrCreateSegment(Branch branch, long time, boolean afterLast)
  {
    if (!afterLast && firstCommit != null)
    {
      Segment[] rowSegments = firstCommit.getRowSegments();
      for (int i = 0; i < rowSegments.length; i++)
      {
        Segment rowSegment = rowSegments[i];
        if (rowSegment != null && !rowSegment.isComplete())
        {
          Branch rowBranch = rowSegment.getBranch();
          boolean complete = rowBranch.getBaseCommitTime() == time;

          System.out.println("Extend: " + rowSegment + " --> " + time + (complete ? " COMPLETE" : ""));
          rowSegment.adjustVisualTime(time, complete);
        }
      }
    }

    Segment bestSegment = null;
    long visualTime = 0;

    for (int i = 0; i < tracks.length; i++)
    {
      Track track = tracks[i];

      if (afterLast)
      {
        Segment lastSegment = track.getLastSegment();
        if (lastSegment != null)
        {
          if (lastSegment.getBranch() == branch)
          {
            // If the last segment of this track has the same branch, then just extend it
            return lastSegment;
          }

          if (branch.getBaseCommitBranch() == lastSegment.getBranch())
          {
            // Don't block the tracks with the base commit
            continue;
          }

          if (lastSegment.getLastCommitTime() == lastCommit.getTime())
          {
            // Don't block the track of the last commit
            continue;
          }

          if (visualTime == 0)
          {
            Segment lastBranchSegment = branch.getLastSegment();
            if (lastBranchSegment != null)
            {
              visualTime = lastBranchSegment.getLastCommitTime();
            }
            else
            {
              visualTime = branch.getBaseCommitTime();
            }
          }

          if (lastSegment.getLastCommitTime() < visualTime)
          {
            if (bestSegment == null)
            {
              bestSegment = lastSegment;
            }

            // // If the last segment of this track ends before the branch's last commit there's enough room for a new
            // // segment in this track
            // if (bestSegment == null || lastSegment.getLastCommitTime() < bestSegment.getLastCommitTime())
            // {
            // bestSegment = lastSegment;
            // }
          }
        }
      }
      else
      {
        Segment firstSegment = track.getFirstSegment();
        if (firstSegment != null)
        {
          Branch firstBranch = firstSegment.getBranch();
          if (firstBranch == branch)
          {
            // If the first segment of this track has the same branch, then just extend it
            return firstSegment;
          }

          if (bestSegment == null)
          {
            if (!firstSegment.isComplete())
            {
              // Don't block the tracks with incomplete segments
              continue;
            }

            if (firstBranch.getBaseCommitBranch() == branch)
            {
              // Don't block the tracks with the base commit
              continue;
            }

            if (firstSegment.getFirstVisualTime() > branch.getFirstCommitTime())
            {
              bestSegment = firstSegment;

              // // If the first segment of this track starts after the branch's first commit there's enough room for a
              // new
              // // segment in this track
              // if (bestSegment == null || firstSegment.getFirstVisualTime() > bestSegment.getFirstVisualTime())
              // {
              // bestSegment = firstSegment;
              // }
            }
          }
        }
      }
    }

    Track track;
    if (bestSegment != null)
    {
      track = bestSegment.getTrack();
    }
    else
    {
      track = createTrack();
    }

    Segment segment = new Segment(track, branch);
    track.addSegment(segment, afterLast);
    branch.addSegment(segment, afterLast);

    if (visualTime != 0)
    {
      segment.adjustVisualTime(visualTime, true);
    }

    return segment;
  }

  private Track createTrack()
  {
    int length = tracks.length;
    Track[] newArray = new Track[length + 1];
    if (length != 0)
    {
      System.arraycopy(tracks, 0, newArray, 0, length);
    }

    Track track = new Track(this, length);
    newArray[length] = track;
    tracks = newArray;
    return track;
  }

  private boolean isAfterLast(long time)
  {
    if (lastCommit == null)
    {
      return true;
    }

    if (time > lastCommit.getTime())
    {
      return true;
    }

    if (time < firstCommit.getTime())
    {
      return false;
    }

    throw new IllegalArgumentException("New commits must not be added between the first and last commits");
  }
}
