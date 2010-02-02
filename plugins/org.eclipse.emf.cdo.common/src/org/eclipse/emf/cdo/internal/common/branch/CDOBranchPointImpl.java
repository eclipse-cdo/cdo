/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOBranchPointImpl implements CDOBranchPoint
{
  private CDOBranch branch;

  private long timeStamp;

  public CDOBranchPointImpl(CDOBranch branch, long timeStamp)
  {
    this.branch = branch;
    this.timeStamp = timeStamp;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public int compareTo(CDOBranchPoint o)
  {
    int result = branch.compareTo(o.getBranch());
    if (result == 0)
    {
      long timeStamp2 = o.getTimeStamp();
      result = timeStamp < timeStamp2 ? -1 : timeStamp == timeStamp2 ? 0 : 1;
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    return branch.hashCode() ^ new Long(timeStamp).hashCode();
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
      return branch.equals(that.getBranch()) && timeStamp == that.getTimeStamp();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BranchPoint[{0}, {1,date} {1,time,HH:mm:ss:SSS}]", branch, timeStamp);
  }
}
