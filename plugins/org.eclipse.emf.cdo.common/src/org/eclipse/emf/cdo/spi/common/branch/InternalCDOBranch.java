/*
 * Copyright (c) 2010-2013, 2015, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDODuplicateBranchException;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOBranch extends CDOBranch
{
  public boolean isProxy();

  @Override
  public InternalCDOBranchManager getBranchManager();

  @Override
  public InternalCDOBranch[] getBranches();

  /**
   * @since 4.0
   */
  public InternalCDOBranch[] getBranches(boolean loadOnDemand);

  @Override
  public InternalCDOBranch getBranch(String path);

  @Override
  public InternalCDOBranch createBranch(String name, long timeStamp) throws CDODuplicateBranchException;

  @Override
  public InternalCDOBranch createBranch(String name) throws CDODuplicateBranchException;

  public BranchInfo getBranchInfo();

  public void setBranchInfo(String name, InternalCDOBranch baseBranch, long baseTimeStamp);

  public void addChild(InternalCDOBranch branch);

  /**
   * @since 4.15
   */
  public void removeChild(InternalCDOBranch branch);

  /**
   * @since 4.15
   */
  public void setDeleted();

  /**
   * @since 4.4
   */
  public void basicSetName(String name);

}
