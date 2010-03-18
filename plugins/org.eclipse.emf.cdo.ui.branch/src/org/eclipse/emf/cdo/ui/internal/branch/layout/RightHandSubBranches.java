/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.branch.layout;

import org.eclipse.emf.cdo.ui.internal.branch.item.BranchPointNode;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;

/**
 * A strategy that layouts a branch. A branch centers its (sibling) nodes below each other while using their time stamp
 * to set the y coordinate. Sub-Branches are displaced to the right or to the left (left and right sub branches are
 * distributed equally).
 * <p>
 * The current implementation may only layout vertically.
 * 
 * @author Andre Dietisheim
 */
public class RightHandSubBranches extends AbstractVerticalLayoutStrategy
{
  /**
   * Sets the location to the right of the current branch. Delegates for this to appropriate strategy
   * 
   * @param subBranchView
   *          the sub branch to layout in the current branch
   * @param branchPointNode
   *          the branch point node the given sub branch is attached to
   * @param branchView
   *          the branch view
   * @see #RIGHT
   */
  @Override
  protected void setSubBranchViewLocation(BranchView branchView, BranchView subBranchView,
      BranchPointNode branchPointNode)
  {
    RIGHT.setSubBranchLocation(branchView, subBranchView, branchPointNode);
  }

  @Override
  protected DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
      BranchPointNode branchPointNode)
  {
    return RIGHT.getTranslationToBranchPoint(subBranch, branchPointNode);
  }

  @Override
  protected DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranch)
  {
    return RIGHT.getTranslationToLatterBranch(subBranch, latterBranch);
  }
}
