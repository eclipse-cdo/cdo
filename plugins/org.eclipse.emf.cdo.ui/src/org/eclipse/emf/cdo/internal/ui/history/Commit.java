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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

/**
 * @author Eike Stepper
 */
public class Commit
{
  private final CDOCommitInfo commitInfo;

  private final Segment segment;

  private Segment[] rowSegments;

  private int commitCounter = -1;

  public Commit(CDOCommitInfo commitInfo, Segment segment)
  {
    this.segment = segment;
    this.commitInfo = commitInfo;
  }

  public final CDOCommitInfo getCommitInfo()
  {
    return commitInfo;
  }

  public final Net getNet()
  {
    return segment.getNet();
  }

  public final Branch getBranch()
  {
    return segment.getBranch();
  }

  public final long getTime()
  {
    return commitInfo.getTimeStamp();
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
}
