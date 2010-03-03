/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOBranchUtil
{
  private CDOBranchUtil()
  {
  }

  public static InternalCDOBranchManager createBranchManager()
  {
    return new CDOBranchManagerImpl();
  }

  public static CDOBranchPoint copy(CDOBranchPoint source)
  {
    return source.getBranch().getPoint(source.getTimeStamp());
  }

  public static CDOBranchVersion copy(CDOBranchVersion source)
  {
    return source.getBranch().getVersion(source.getVersion());
  }

  public static CDOBranchPoint getAncestor(CDOBranchPoint point1, CDOBranchPoint point2)
  {
    CDOBranch branch1 = point1.getBranch();
    CDOBranch branch2 = point2.getBranch();
    if (branch1 == branch2)
    {
      if (point1.compareTo(point2) < 0)
      {
        return point1;
      }

      return point2;
    }

    CDOBranchPoint[] basePath1 = branch1.getBasePath();
    for (int i = basePath1.length - 1; i >= 0; --i)
    {
      CDOBranchPoint pathPoint1 = basePath1[i];
      CDOBranchPoint ancestor = getAncestor(point2, pathPoint1);
      if (ancestor != null)
      {
        return ancestor;
      }
    }

    return null;
  }
}
