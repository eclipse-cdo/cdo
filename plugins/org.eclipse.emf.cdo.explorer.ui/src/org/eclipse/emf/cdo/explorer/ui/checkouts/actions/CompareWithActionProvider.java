/*
 * Copyright (c) 2015, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;

/**
 * @author Eike Stepper
 */
public class CompareWithActionProvider extends AbstractBranchPointActionProvider
{
  private static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.CompareWithActions";

  public CompareWithActionProvider()
  {
    super(ID, "Compare With");
  }

  @Override
  protected String getHistorizedBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Compare with this branch point" : "Compare with this branch";
  }

  @Override
  protected String getOtherBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Select a branch point and compare with it" : "Select a branch and compare with it";
  }

  @Override
  protected String getCommitBranchPointToolTip()
  {
    return "Select a commit and compare with it";
  }

  @Override
  protected String getOtherCheckoutToolTip()
  {
    return "Compare with the branch point of this checkout";
  }

  @Override
  protected void execute(CDOCheckout checkout, CDOBranchPoint branchPoint) throws Exception
  {
    compareWith(checkout, branchPoint);
  }

  public static void compareWith(CDOCheckout checkout, CDOBranchPoint branchPoint)
  {
    if (Support.UI_COMPARE.isAvailable())
    {
      CDORepository repository = checkout.getRepository();
      CDOBranchPoint left = branchPoint;
      CDOBranchPoint right = checkout.getBranchPoint();
      CDOCompareEditorUtil.openEditor(repository, left, right, null, true);
    }
  }
}
