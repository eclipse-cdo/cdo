/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class Net
{
  private static final RGB[] RGBS = new RGB[] { new RGB(133, 166, 214), new RGB(221, 205, 93), new RGB(199, 134, 57), new RGB(131, 150, 98),
      new RGB(197, 123, 127), new RGB(139, 136, 140), new RGB(48, 135, 144), new RGB(190, 93, 66), new RGB(143, 163, 54), new RGB(180, 148, 74),
      new RGB(101, 101, 217), new RGB(72, 153, 119), new RGB(23, 101, 160), new RGB(132, 164, 118), new RGB(255, 230, 59), new RGB(136, 176, 70),
      new RGB(255, 138, 1), new RGB(123, 187, 95), new RGB(233, 88, 98), new RGB(93, 158, 254), new RGB(175, 215, 0), new RGB(140, 134, 142),
      new RGB(232, 168, 21), new RGB(0, 172, 191), new RGB(251, 58, 4), new RGB(63, 64, 255), new RGB(27, 194, 130), new RGB(0, 104, 183) };

  private static final Segment[] NO_SEGMENTS = {};

  private final CDOSession session;

  private final CDOID objectID;

  private final ResourceManager resourceManager;

  private Color[] colors;

  private Track[] tracks = {};

  private final Map<CDOBranch, Branch> branches = new HashMap<>();

  private final Map<CDOCommitInfo, Commit> commits = new WeakHashMap<>();

  private final Map<CDOCommitInfo, List<Commit>> danglingMergeTargets = new WeakHashMap<>();

  private int commitCounter;

  private Commit firstCommit;

  private Commit lastCommit;

  private boolean hideExceptions;

  public Net(CDOSession session, CDOID objectID, ResourceManager resourceManager)
  {
    this.session = session;
    this.objectID = objectID;
    this.resourceManager = resourceManager;
  }

  public final CDOSession getSession()
  {
    return session;
  }

  public final CDOID getObjectID()
  {
    return objectID;
  }

  public final Track[] getTracks()
  {
    return tracks;
  }

  public final Commit getFirstCommit()
  {
    return firstCommit;
  }

  public final Commit getLastCommit()
  {
    return lastCommit;
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
    return commits.get(commitInfo);
  }

  /**
   * @deprecated
   */
  @Deprecated
  public final boolean hasBranchCommitBetween(Branch branch, long fromTime, long toTime)
  {
    for (Commit commit : commits.values())
    {
      if (commit.getBranch() == branch)
      {
        long time = commit.getTime();
        if (fromTime < time && time < toTime)
        {
          return true;
        }
      }
    }

    return false;
  }

  public final Commit getOrAddCommit(CDOCommitInfo commitInfo)
  {
    Commit commit = getCommit(commitInfo);
    if (commit == null)
    {
      commit = addCommit(commitInfo);
    }

    return commit;
  }

  public Color getColor(int number)
  {
    if (colors == null)
    {
      colors = new Color[RGBS.length];
    }

    int index = number % RGBS.length;
    if (colors[index] == null)
    {
      RGB rgb = RGBS[index];
      colors[index] = resourceManager.createColor(rgb);
    }

    return colors[index];
  }

  @Override
  public String toString()
  {
    return "Net[" + session + ", " + objectID + "]";
  }

  private final Segment getSegment(CDOCommitInfo commitInfo)
  {
    CDOBranch cdoBranch = commitInfo.getBranch();
    long time = commitInfo.getTimeStamp();

    Branch branch = getBranch(cdoBranch);
    Segment segment = branch.getSegment(time);

    if (segment == null)
    {
      // This must be a new commit after the last or before the first
      boolean afterLast = isAfterLast(time); // false means beforeFirst
      segment = getOrCreateSegment(branch, time, afterLast);
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
          if (lastSegment.getBranch() == branch && !lastSegment.isMerge())
          {
            // If the last segment of this track has the same branch, then just extend it
            return lastSegment;
          }

          if (branch.getBaseCommitBranch() == lastSegment.getBranch())
          {
            // Don't block the tracks with the base commit
            continue;
          }

          if (lastCommit != null && lastCommit.getTime() == lastSegment.getLastCommitTime())
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

          if (bestSegment == null && lastSegment.getLastCommitTime() < visualTime)
          {
            bestSegment = lastSegment;
          }
        }
      }
      else
      {
        Segment firstSegment = track.getFirstSegment();
        if (firstSegment != null)
        {
          Branch firstBranch = firstSegment.getBranch();
          if (firstBranch == branch && !firstSegment.isMerge())
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

    Segment segment = new Segment(track, branch, null);
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

  Segment[] createRowSegments(long time)
  {
    Segment[] segments = NO_SEGMENTS;
    for (int i = tracks.length - 1; i >= 0; --i)
    {
      Track track = tracks[i];
      Segment segment = track.getSegment(time);

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

  Segment createMergeSegment(Commit mergeSource, long toTime)
  {
    Branch branch = mergeSource.getBranch();
    long fromTime = mergeSource.getTime();

    for (int i = 0; i < tracks.length; i++)
    {
      Track track = tracks[i];

      Segment segment = track.getFirstSegment();
      long previousLastTime = 0;

      while (segment != null)
      {
        if (previousLastTime < fromTime && toTime < segment.getFirstVisualTime())
        {
          Segment mergeSegment = new Segment(track, branch, mergeSource);

          Segment previousInTrack = segment.getPreviousInTrack();
          if (previousInTrack != null)
          {
            mergeSegment.setNextInTrack(segment);
            segment.setPreviousInTrack(mergeSegment);

            mergeSegment.setPreviousInTrack(previousInTrack);
            previousInTrack.setNextInTrack(mergeSegment);
          }
          else
          {
            track.addSegment(mergeSegment, false);
          }

          return mergeSegment;
        }

        previousLastTime = segment.getLastCommitTime();
        if (previousLastTime > fromTime)
        {
          break;
        }

        segment = segment.getNextInTrack();
      }

      if (previousLastTime < fromTime && toTime < lastCommit.getTime())
      {
        Segment mergeSegment = new Segment(track, branch, mergeSource);
        track.addSegment(mergeSegment, true);
        return mergeSegment;
      }
    }

    Track track = createTrack();
    Segment mergeSegment = new Segment(track, branch, mergeSource);
    track.addSegment(mergeSegment, true);
    return mergeSegment;
  }

  Commit addCommit(CDOCommitInfo commitInfo)
  {
    Segment segment = getSegment(commitInfo);
    if (segment == null)
    {
      throw new IllegalStateException("No segment");
    }

    ++commitCounter;
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

    commits.put(commitInfo, commit);

    List<Commit> mergeTargets = danglingMergeTargets.remove(commitInfo);
    if (mergeTargets != null)
    {
      for (Commit mergeTarget : mergeTargets)
      {
        mergeTarget.setMergeSource(commit);
      }

      commit.addMergeTargets(mergeTargets);
    }

    return commit;
  }

  void addDanglingMergeTarget(CDOCommitInfo mergedCommitInfo, Commit mergeTarget)
  {
    List<Commit> mergeTargets = danglingMergeTargets.get(mergedCommitInfo);
    if (mergeTargets == null)
    {
      mergeTargets = new ArrayList<>(1);
      danglingMergeTargets.put(mergedCommitInfo, mergeTargets);
    }

    mergeTargets.add(mergeTarget);
  }

  int getCommitCounter()
  {
    return commitCounter;
  }

  boolean isHideExceptions()
  {
    return hideExceptions;
  }

  void hideExceptions()
  {
    hideExceptions = true;
  }
}
