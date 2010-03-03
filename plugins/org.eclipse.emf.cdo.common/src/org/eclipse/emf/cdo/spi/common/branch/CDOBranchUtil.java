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

  public static CDOBranchPoint getAncestor(CDOBranch branch1, CDOBranch branch2)
  {
    if (branch1 == branch2)
    {
      return branch1.getBase();
    }
  
    for (CDOBranchPoint branchPoint : branch1.getBasePath())
    {
      if (branchPoint.getBranch() == branch2)
      {
        return branchPoint;
      }
    }
  
    for (CDOBranchPoint branchPoint : branch2.getBasePath())
    {
      if (branchPoint.getBranch() == branch1)
      {
        return branchPoint;
      }
    }
  
    return null;
  }
}
