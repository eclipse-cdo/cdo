/*
 * Copyright (c) 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

import org.eclipse.net4j.util.ObjectUtil;

import java.io.IOException;
import java.io.Serializable;

/**
 * References a {@link CDOBranch}.
 *
 * @author Eike Stepper
 * @since 4.10
 */
public final class CDOBranchRef implements Serializable
{
  /**
   * @since 4.12
   */
  public static final CDOBranchRef MAIN = new CDOBranchRef(CDOBranch.MAIN_BRANCH_NAME);

  private static final long serialVersionUID = 1L;

  private final String branchPath;

  public CDOBranchRef(CDOBranch branch)
  {
    this(branch.getPathName());
  }

  public CDOBranchRef(String branchPath)
  {
    this.branchPath = CDOBranchUtil.sanitizePathName(branchPath).intern();
  }

  public CDOBranchRef(CDODataInput in) throws IOException
  {
    this(in.readCDOPackageURI());
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOPackageURI(branchPath);
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public boolean isMainBranch()
  {
    return CDOBranch.MAIN_BRANCH_NAME.equals(branchPath);
  }

  /**
   * @since 4.12
   */
  public CDOBranchPointRef getPointRef(long timeStamp)
  {
    return new CDOBranchPointRef(this, timeStamp);
  }

  /**
   * @since 4.12
   */
  public CDOBranchPointRef getHeadRef()
  {
    return getPointRef(CDOBranchPoint.UNSPECIFIED_DATE);
  }

  /**
   * @since 4.23
   */
  public CDOBranchRef getBaseBranchRef()
  {
    if (!isMainBranch())
    {
      int lastSlash = branchPath.lastIndexOf(CDOBranch.PATH_SEPARATOR);
      if (lastSlash != -1)
      {
        return new CDOBranchRef(branchPath.substring(0, lastSlash));
      }
    }

    return null;
  }

  public CDOBranch resolve(CDOBranchManager branchManager)
  {
    return branchManager.getBranch(branchPath);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == CDOBranchRef.class)
    {
      CDOBranchRef that = (CDOBranchRef)obj;
      return ObjectUtil.equals(branchPath, that.branchPath);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return branchPath.hashCode();
  }

  @Override
  public String toString()
  {
    return branchPath;
  }

  /**
   * Provides {@link CDOBranchRef branch references}.
   *
   * @author Eike Stepper
   */
  public interface Provider
  {
    public CDOBranchRef getBranchRef();
  }
}
