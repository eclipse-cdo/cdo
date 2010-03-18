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
public class HorizontallyAlternatingSubBranches extends AbstractVerticalLayoutStrategy
{
  protected HorizontallyAlternatingSubBranches currentSubBranchStrategy = null;

  /**
   * Sets either a location to the left or to the right of the current branch. Delegates for this to appropriate
   * strategies
   * 
   * @param subBranchView
   *          the sub branch to layout in the current branch
   * @param branchPointNode
   *          the branch point node the given sub branch is attached to
   * @param branchView
   *          the branch view
   * @see #LEFT
   * @see #RIGHT
   */
  @Override
  protected void setSubBranchViewLocation(BranchView branchView, BranchView subBranchView,
      BranchPointNode branchPointNode)
  {
    currentSubBranchStrategy = getSubBranchStrategy(currentSubBranchStrategy);
    currentSubBranchStrategy.setSubBranchLocation(branchView, subBranchView, branchPointNode);
  }

  /**
   * Returns the strategy that layouts the next branch view. Starts with right, second call returns left, etc.
   * 
   * @return the current sub branch strategy
   * @see #LEFT
   * @see #RIGHT
   */
  private HorizontallyAlternatingSubBranches getSubBranchStrategy(HorizontallyAlternatingSubBranches currentStrategy)
  {
    if (currentStrategy == null || currentStrategy == LEFT)
    {
      return RIGHT;
    }

    return LEFT;
  }

  @Override
  protected DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
      BranchPointNode branchPointNode)
  {
    return currentSubBranchStrategy.getTranslationToBranchPoint(subBranch, branchPointNode);
  }

  @Override
  protected DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranch)
  {
    return currentSubBranchStrategy.getTranslationToLatterBranch(subBranch, latterBranch);
  }
}
