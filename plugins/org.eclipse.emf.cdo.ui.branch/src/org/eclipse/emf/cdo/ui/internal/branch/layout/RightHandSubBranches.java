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
 * A strategy that layouts a branch. A branch centers its (sibling) nodes below each other while using their time stamp
 * to set the y coordinate. Sub-Branches are displaced to the right or to the left (left and right sub branches are
 * distributed equally).
 * 
 * @author Andre Dietisheim
 */
public class RightHandSubBranches extends AbstractVerticalLayoutStrategy
{
  @Override
  protected SubBranchViewTranslation getSubBranchTranslationStrategy(BranchView branchView,
      SubBranchViewTranslation currentTranslationStrategy)
  {
    return RIGHT;
  }

  @Override
  public BranchView getLaterOverlapingBranch(BranchView branchView, BranchView subBranchView)
  {
    BranchView overlapingBranch = branchView.getLastSubBranchView();
    if (overlapingBranch != null
        && GeometryUtils.bottomEndsBefore(subBranchView.getBounds(), overlapingBranch.getBounds()))
    {
      return null;
    }
    return overlapingBranch;
  }
}
