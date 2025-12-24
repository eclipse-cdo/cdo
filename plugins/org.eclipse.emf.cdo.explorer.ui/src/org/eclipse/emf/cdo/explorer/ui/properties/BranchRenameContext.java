/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDORenameContext;

import org.eclipse.net4j.util.StringUtil;

/**
 * @author Eike Stepper
 */
public final class BranchRenameContext implements CDORenameContext.WithElement
{
  private final CDOBranch branch;

  public BranchRenameContext(CDOBranch branch)
  {
    this.branch = branch;
  }

  @Override
  public Object getElement()
  {
    return branch;
  }

  @Override
  public String getType()
  {
    return "Branch";
  }

  @Override
  public String getName()
  {
    return branch.getName();
  }

  @Override
  public void setName(String name)
  {
    branch.setName(name);
  }

  @Override
  public String validateName(String name)
  {
    if (StringUtil.isEmpty(name))
    {
      return "Branch name is empty.";
    }

    if (name.equals(getName()))
    {
      return null;
    }

    CDOBranch baseBranch = branch.getBase().getBranch();
    if (baseBranch.getBranch(name) != null)
    {
      return "Branch name is not unique within the base branch.";
    }

    return null;
  }
}