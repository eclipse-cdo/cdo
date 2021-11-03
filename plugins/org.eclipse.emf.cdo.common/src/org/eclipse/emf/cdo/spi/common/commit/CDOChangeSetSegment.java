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
package org.eclipse.emf.cdo.spi.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;

import java.text.MessageFormat;
import java.util.LinkedList;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @author Eike Stepper
 * @since 3.0
 */
public class CDOChangeSetSegment implements CDOBranchPoint
{
  private CDOBranchPoint branchPoint;

  private long endTime;

  public CDOChangeSetSegment(CDOBranch branch, long timeStamp, long endTime)
  {
    branchPoint = new CDOBranchPointImpl(branch, timeStamp);
    this.endTime = endTime;
  }

  @Override
  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  @Override
  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  public long getEndTime()
  {
    return endTime;
  }

  public CDOBranchPoint getEndPoint()
  {
    return getBranch().getPoint(endTime);
  }

  public boolean isOpenEnded()
  {
    return endTime == CDOBranchPoint.UNSPECIFIED_DATE;
  }

  /**
   * @since 4.6
   */
  public boolean contains(CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    if (branch != getBranch())
    {
      return false;
    }

    long timeStamp = branchPoint.getTimeStamp();
    if (timeStamp < getTimeStamp())
    {
      return false;
    }

    if (!isOpenEnded() && timeStamp > endTime)
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Segment[{0}, {1}, {2}]", getBranch(), getTimeStamp(), endTime); //$NON-NLS-1$
  }

  /**
   * @since 4.6
   */
  public static void handleSegments(CDOBranchPoint endPoint, Handler handler)
  {
    long creationTime = endPoint.getBranch().getBranchManager().getMainBranch().getBase().getTimeStamp();
    handleSegments(creationTime, endPoint, handler);
  }

  /**
   * @since 4.6
   */
  public static void handleSegments(long startTime, CDOBranchPoint endPoint, Handler handler)
  {
    CDOBranch endBranch = endPoint.getBranch();

    for (;;)
    {
      CDOBranchPoint base = endBranch.getBase();
      long timeStamp = base.getTimeStamp();
      long endTime = endPoint.getTimeStamp();

      if (timeStamp <= startTime)
      {
        handler.handleSegment(new CDOChangeSetSegment(endBranch, startTime, endTime));
        return;
      }

      if (!handler.handleSegment(new CDOChangeSetSegment(endBranch, timeStamp, endTime)))
      {
        return;
      }

      endPoint = base;
      endBranch = base.getBranch();
    }
  }

  /**
   * @since 4.6
   */
  public static void handleSegments(CDOBranchPoint startPoint, CDOBranchPoint endPoint, Handler handler)
  {
    CDOBranch startBranch = startPoint.getBranch();
    CDOBranch endBranch = endPoint.getBranch();

    while (startBranch != endBranch)
    {
      CDOBranchPoint base = endBranch.getBase();
      if (!handler.handleSegment(new CDOChangeSetSegment(endBranch, base.getTimeStamp(), endPoint.getTimeStamp())))
      {
        return;
      }

      endPoint = base;
      endBranch = base.getBranch();
    }

    handler.handleSegment(new CDOChangeSetSegment(startBranch, startPoint.getTimeStamp(), endPoint.getTimeStamp()));
  }

  /**
   * @since 4.6
   */
  public static CDOChangeSetSegment[] createFrom(long startTime, CDOBranchPoint endPoint)
  {
    LinkedList<CDOChangeSetSegment> result = new LinkedList<>();

    handleSegments(startTime, endPoint, (Handler)segment -> {
      result.addFirst(segment);
      return true;
    });

    return result.toArray(new CDOChangeSetSegment[result.size()]);
  }

  public static CDOChangeSetSegment[] createFrom(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    LinkedList<CDOChangeSetSegment> result = new LinkedList<>();

    handleSegments(startPoint, endPoint, (Handler)segment -> {
      result.addFirst(segment);
      return true;
    });

    return result.toArray(new CDOChangeSetSegment[result.size()]);
  }

  /**
   * @since 4.6
   */
  public static boolean contains(CDOChangeSetSegment[] segments, CDOBranchPoint branchPoint)
  {
    for (CDOChangeSetSegment segment : segments)
    {
      if (segment.contains(branchPoint))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   * @since 4.6
   */
  @FunctionalInterface
  public interface Handler
  {
    public boolean handleSegment(CDOChangeSetSegment segment);
  }
}
