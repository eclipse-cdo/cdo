/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;

/**
 * @author Eike Stepper
 */
public class MergeFromActionProvider extends AbstractBranchPointActionProvider
{
  private static final String ID = MergeFromActionProvider.class.getName();

  public MergeFromActionProvider()
  {
    super(ID, "Merge From");
  }

  @Override
  protected String getHistorizedBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Merge from this branch point" : "Merge from this branch";
  }

  @Override
  protected String getOtherBranchPointToolTip(boolean allowTimeStamp)
  {
    return allowTimeStamp ? "Select a branch point and merge from it" : "Select a branch and merge from it";
  }

  @Override
  protected String getCommitBranchPointToolTip()
  {
    return "Select a commit and merge from it";
  }

  @Override
  protected String getOtherCheckoutToolTip()
  {
    return "Merge from the branch point of this checkout";
  }

  @Override
  protected void execute(CDOCheckout checkout, CDOBranchPoint branchPoint) throws Exception
  {
    mergeFrom(checkout, branchPoint);
  }

  public static void mergeFrom(CDOCheckout checkout, CDOBranchPoint branchPoint)
  {
    CDORepository repository = checkout.getRepository();
    CDOBranchPoint left = branchPoint;
    CDOBranchPoint right = checkout.getBranchPoint();
    CDOCompareEditorUtil.openEditor(repository, repository, left, right, null, true);
  }
}
