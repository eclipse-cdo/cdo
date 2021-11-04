/*
 * Copyright (c) 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.actions;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;

/**
 * @author Eike Stepper
 */
public class ReplaceWithActionProvider extends AbstractBranchPointActionProvider
{
  private static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.ReplaceWithActions";

  public ReplaceWithActionProvider()
  {
    super(ID, "Replace With");
  }

  @Override
  protected String getHistorizedBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Replace with this branch point" : "Replace with this branch";
  }

  @Override
  protected String getOtherBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Select a branch point and replace with it" : "Select a branch and replace with it";
  }

  @Override
  protected String getCommitBranchPointToolTip()
  {
    return "Select a commit and replace with it";
  }

  @Override
  protected String getOtherCheckoutToolTip()
  {
    return "Replace with the branch point of this checkout";
  }

  @Override
  protected void execute(CDOCheckout checkout, CDOBranchPoint branchPoint) throws Exception
  {
    replaceWith(checkout, branchPoint);
  }

  public static void replaceWith(CDOCheckout checkout, CDOBranchPoint branchPoint)
  {
    // TODO
  }
}
