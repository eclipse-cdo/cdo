/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
