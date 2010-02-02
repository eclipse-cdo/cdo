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
package org.eclipse.emf.cdo.spi.common.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchVersionImpl;

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

  public static CDOBranchPoint createBranchPoint(CDOBranch branch, long timeStamp)
  {
    return new CDOBranchPointImpl(branch, timeStamp);
  }

  public static CDOBranchPoint createBranchPoint(CDOBranchPoint source)
  {
    return createBranchPoint(source.getBranch(), source.getTimeStamp());
  }

  public static CDOBranchVersion createBranchVersion(CDOBranch branch, int version)
  {
    return new CDOBranchVersionImpl(branch, version);
  }

  public static CDOBranchVersion createBranchVersion(CDOBranchVersion source)
  {
    return createBranchVersion(source.getBranch(), source.getVersion());
  }
}
