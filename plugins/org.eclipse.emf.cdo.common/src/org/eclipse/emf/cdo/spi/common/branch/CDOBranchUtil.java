/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointRangeImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOBranchUtil
{
  private static final char PATH_SEPARATOR_CHAR = CDOBranch.PATH_SEPARATOR.charAt(0);

  /**
   * @since 4.6
   */
  public static final CDOBranchPoint AUTO_BRANCH_POINT = new AutoBranchPoint();

  private CDOBranchUtil()
  {
  }

  public static InternalCDOBranchManager createBranchManager()
  {
    return new CDOBranchManagerImpl();
  }

  public static CDOBranchPointRange createRange(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    return new CDOBranchPointRangeImpl(startPoint, endPoint);
  }

  /**
   * @since 4.6
   */
  public static void writeRange(CDODataOutput out, CDOBranchPointRange range) throws IOException
  {
    out.writeCDOBranchPoint(range.getStartPoint());
    out.writeCDOBranchPoint(range.getEndPoint());
  }

  /**
   * @since 4.6
   */
  public static CDOBranchPointRange readRange(CDODataInput in) throws IOException
  {
    CDOBranchPoint startPoint = in.readCDOBranchPoint();
    CDOBranchPoint endPoint = in.readCDOBranchPoint();
    return createRange(startPoint, endPoint);
  }

  /**
   * @since 4.6
   */
  public static void writeRangeOrNull(CDODataOutput out, CDOBranchPointRange range) throws IOException
  {
    if (range != null)
    {
      out.writeBoolean(true);
      writeRange(out, range);
    }
    else
    {
      out.writeBoolean(false);
    }
  }

  /**
   * @since 4.6
   */
  public static CDOBranchPointRange readRangeOrNull(CDODataInput in) throws IOException
  {
    if (in.readBoolean())
    {
      return readRange(in);
    }

    return null;
  }

  /**
   * @since 4.6
   */
  public static void writeBranchPointOrNull(CDODataOutput out, CDOBranchPoint point) throws IOException
  {
    if (point != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranchPoint(point);
    }
    else
    {
      out.writeBoolean(false);
    }
  }

  /**
   * @since 4.6
   */
  public static CDOBranchPoint readBranchPointOrNull(CDODataInput in) throws IOException
  {
    if (in.readBoolean())
    {
      return in.readCDOBranchPoint();
    }

    return null;
  }

  public static CDOBranchPoint copyBranchPoint(CDOBranchPoint source)
  {
    return source.getBranch().getPoint(source.getTimeStamp());
  }

  public static CDOBranchVersion copyBranchVersion(CDOBranchVersion source)
  {
    return source.getBranch().getVersion(source.getVersion());
  }

  /**
   * @since 4.14
   */
  public static CDOBranch adjustBranch(CDOBranch branch, CDOBranchManager branchManager)
  {
    if (branch.getBranchManager() != branchManager)
    {
      branch = branchManager.getBranch(branch.getID());
    }

    return branch;
  }

  /**
   * @since 4.4
   */
  public static CDOBranchPoint adjustBranchPoint(CDOBranchPoint branchPoint, CDOBranchManager branchManager)
  {
    CDOBranch branch = branchPoint.getBranch();
    CDOBranch adjustedBranch = adjustBranch(branch, branchManager);
    if (adjustedBranch != branch)
    {
      branchPoint = adjustedBranch.getPoint(branchPoint.getTimeStamp());
    }

    return branchPoint;
  }

  /**
   * @since 4.4
   */
  public static CDOBranchPoint normalizeBranchPoint(CDOBranchPoint branchPoint)
  {
    long timeStamp = branchPoint.getTimeStamp();
    if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return branchPoint;
    }

    CDOBranch branch = branchPoint.getBranch();
    return doNormalizeBranchPoint(branch, timeStamp);
  }

  /**
   * @since 4.2
   */
  public static CDOBranchPoint normalizeBranchPoint(CDOBranch branch, long timeStamp)
  {
    if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return branch.getHead();
    }

    return doNormalizeBranchPoint(branch, timeStamp);
  }

  private static CDOBranchPoint doNormalizeBranchPoint(CDOBranch branch, long timeStamp)
  {
    for (;;)
    {
      long baseTime = branch.getBase().getTimeStamp();
      if (timeStamp < baseTime)
      {
        branch = branch.getBase().getBranch();
        if (branch == null)
        {
          throw new IllegalArgumentException(
              "Time " + CDOCommonUtil.formatTimeStamp(timeStamp) + " is before the repository creation time " + CDOCommonUtil.formatTimeStamp(baseTime));
        }
      }
      else
      {
        return branch.getPoint(timeStamp);
      }
    }
  }

  /**
   * Returns <code>true</code> if the first given branch point "contained" is reachable from the second given branch point "container"
   * by following the {@link CDOBranch#getBase() branch bases} <i>upwards</i>, <code>false</code> otherwise.
   * <p>
   *
   */
  public static boolean isContainedBy(CDOBranchPoint contained, CDOBranchPoint container)
  {
    CDOBranch containerBranch = container.getBranch();
    if (containerBranch == contained.getBranch())
    {
      return CDOCommonUtil.compareTimeStamps(contained.getTimeStamp(), container.getTimeStamp()) <= 0;
    }

    if (containerBranch == null)
    {
      return false;
    }

    return isContainedBy(contained, containerBranch.getBase());
  }

  public static CDOBranchPoint getAncestor(CDOBranchPoint point1, CDOBranchPoint point2)
  {
    if (point1.getBranch() == null)
    {
      // Must be the main branch base
      return point1;
    }

    if (point2.getBranch() == null)
    {
      // Must be the main branch base
      return point2;
    }

    CDOBranchPoint[] path1 = getPath(point1);
    CDOBranchPoint[] path2 = getPath(point2);
    for (CDOBranchPoint pathPoint1 : path1)
    {
      for (CDOBranchPoint pathPoint2 : path2)
      {
        if (pathPoint1.getBranch() == pathPoint2.getBranch())
        {
          if (CDOCommonUtil.compareTimeStamps(pathPoint1.getTimeStamp(), pathPoint2.getTimeStamp()) < 0)
          {
            return pathPoint1;
          }

          return pathPoint2;
        }
      }
    }

    // Can not happen because any two branches meet on the main branch
    return null;
  }

  /**
   * @since 4.15
   */
  public static void forEachBranchPoint(CDOBranchPoint branchPoint, boolean considerBranchBases, Predicate<CDOBranchPoint> consumer)
  {
    if (!consumer.test(branchPoint))
    {
      return;
    }

    if (considerBranchBases)
    {
      for (;;)
      {
        CDOBranch branch = branchPoint.getBranch();
        if (branch.isMainBranch())
        {
          break;
        }

        branchPoint = branch.getBase();
        if (!consumer.test(branchPoint))
        {
          return;
        }
      }
    }
  }

  /**
   * @since 4.15
   */
  public static void forEachBranchInTree(CDOBranch root, Consumer<CDOBranch> consumer)
  {
    consumer.accept(root);

    for (CDOBranch branch : root.getBranches())
    {
      forEachBranchInTree(branch, consumer);
    }
  }

  public static CDOBranchPoint[] getPath(CDOBranchPoint point)
  {
    List<CDOBranchPoint> result = new ArrayList<>();
    getPath(point, result);
    return result.toArray(new CDOBranchPoint[result.size()]);
  }

  private static void getPath(CDOBranchPoint point, List<CDOBranchPoint> result)
  {
    CDOBranch branch = point.getBranch();
    if (branch != null)
    {
      result.add(point);
      getPath(branch.getBase(), result);
    }
  }

  /**
   * @since 4.12
   */
  public static String sanitizePathName(String pathName)
  {
    pathName = pathName.trim();
    int length = pathName.length();

    int i = 0;
    while (i < length && pathName.charAt(i) == PATH_SEPARATOR_CHAR)
    {
      ++i;
    }

    return i == 0 ? pathName : pathName.substring(i);
  }

  /**
   * @author Eike Stepper
   */
  private static final class AutoBranchPoint implements CDOBranchPoint
  {
    @Override
    public long getTimeStamp()
    {
      return Long.MIN_VALUE;
    }

    @Override
    public CDOBranch getBranch()
    {
      return null;
    }

    @Override
    public String toString()
    {
      return "BranchPoint[AUTO]";
    }
  }
}
