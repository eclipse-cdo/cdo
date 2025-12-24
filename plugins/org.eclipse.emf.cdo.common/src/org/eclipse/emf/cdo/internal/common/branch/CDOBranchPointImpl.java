/*
 * Copyright (c) 2010-2013, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOBranchPointImpl implements CDOBranchPoint, Comparable<CDOBranchPoint>
{
  private final CDOBranch branch;

  private final long timeStamp;

  public CDOBranchPointImpl(CDOBranch branch, long timeStamp)
  {
    this.branch = branch;
    this.timeStamp = timeStamp;
  }

  @Override
  public final CDOBranch getBranch()
  {
    return branch;
  }

  @Override
  public final long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public int compareTo(CDOBranchPoint o)
  {
    int result = branch.compareTo(o.getBranch());
    if (result == 0)
    {
      result = CDOCommonUtil.compareTimeStamps(timeStamp, o.getTimeStamp());
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    return branch.hashCode() ^ Long.valueOf(timeStamp).hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOBranchPoint)
    {
      CDOBranchPoint that = (CDOBranchPoint)obj;
      return branch == that.getBranch() && timeStamp == that.getTimeStamp();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BranchPoint[{0}, {1}]", String.valueOf(branch.getPathName()), CDOCommonUtil.formatTimeStamp(timeStamp)); //$NON-NLS-1$
  }
}
