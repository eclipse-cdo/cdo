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

import org.eclipse.emf.cdo.ui.internal.branch.geometry.GeometryUtils;

/**
 * A strategy that layouts a branch vertically and displaces sub branch view to the right or to the left. Left and right
 * sub branches alternate equally, they're distributed equally.
 * 
 * @author Andre Dietisheim
 */
public class HorizontallyAlternatingSubBranches extends AbstractVerticalLayoutStrategy
{
  /**
   * Returns the strategy that layouts the next branch view. Starts with right, second call returns left, etc.
   * 
   * @param branchView
   *          the branch view
   * @param currentTranslationStrategy
   *          the current translation strategy
   * @return the current sub branch strategy
   * @see #LEFT
   * @see #RIGHT
   */
  @Override
  protected SubBranchViewTranslation getSubBranchTranslationStrategy(BranchView branchView,
      SubBranchViewTranslation currentTranslationStrategy)
  {
    boolean isPairSubBranch = branchView.getSubBranchViews().size() % 2 == 0;
    if (isPairSubBranch)
    {
      return RIGHT;
    }

    return LEFT;
  }

  @Override
  public BranchView getLaterOverlapingBranch(BranchView branchView, BranchView subBranchView)
  {
    BranchView overlapingBranch = branchView.getSecondToLastSubBranchView();
    if (overlapingBranch != null
        && GeometryUtils.bottomEndsBefore(subBranchView.getBounds(), overlapingBranch.getBounds()))
    {
      return null;
    }
    return overlapingBranch;
  }
}
