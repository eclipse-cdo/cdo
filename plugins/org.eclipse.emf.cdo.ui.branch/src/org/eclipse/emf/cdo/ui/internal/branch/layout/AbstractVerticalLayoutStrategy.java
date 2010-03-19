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
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchPointNodeUtils;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

/**
 * A strategy that layouts a branch. A branch centers its (sibling) nodes below each other while using their time stamp
 * to set the y coordinate. Sub-Branches are displaced to the right or to the left (left and right sub branches are
 * distributed equally).
 * <p>
 * The current implementation may only layout vertically.
 * 
 * @author Andre Dietisheim
 */
public abstract class AbstractVerticalLayoutStrategy extends AbstractBranchViewLayoutStrategy
{
  /**
   * The strategy that's currently applied to layout sub branches @see
   * {@link #getSubBranchTranslationStrategy(AbstractVerticalLayoutStrategy)}
   */
  protected SubBranchViewTranslation currentTranslationStrategy = null;

  protected static final SubBranchViewTranslation RIGHT = new SubBranchViewTranslation()
  {
    public DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranchView,
        BranchPointNode branchPointNode, double branchPadding)
    {
      InternalNode branchPointInternalNode = BranchPointNodeUtils.getInternalNode(branchPointNode);
      return new DisplayIndependentDimension( //
          GeometryUtils.getTranslation(subBranchView.getBounds().x, branchPointInternalNode.getInternalX()) //
              + branchPointInternalNode.getInternalWidth() //
              + branchPadding, 0);
    }

    public DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranch,
        double branchPadding)
    {
      DisplayIndependentRectangle latterBranchBounds = latterBranch.getBounds();
      return new DisplayIndependentDimension(//
          latterBranchBounds.x + latterBranchBounds.width + branchPadding, 0);
    }
  };

  protected static final SubBranchViewTranslation LEFT = new SubBranchViewTranslation()
  {
    public DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode, double branchPadding)
    {
      InternalNode branchPointInternalNode = BranchPointNodeUtils.getInternalNode(branchPointNode);
      DisplayIndependentRectangle subBranchBounds = subBranch.getBounds();
      return new DisplayIndependentDimension( //
          GeometryUtils.getTranslation(subBranchBounds.x, branchPointInternalNode.getInternalX()) //
              - subBranchBounds.width //
              - branchPadding, 0);
    }

    public DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranchView,
        double branchPadding)
    {
      DisplayIndependentRectangle latterBranchBounds = latterBranchView.getBounds();
      return new DisplayIndependentDimension( //
          latterBranchBounds.x - branchPadding, 0);
    }
  };

  protected static interface SubBranchViewTranslation
  {

    /**
     * Returns the offset that's needed to translate the given branch so that it does not collide with the branch point.
     * 
     * @param branchPointNode
     *          the branch point node
     * @param subBranch
     *          the sub branch
     * @return the branch point translation
     */
    public DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode, double branchPadding);

    /**
     * Returns the offset that's needed to translate the given branch so that it does not collide with the latter
     * branch.
     * 
     * @param subBranch
     *          the sub branch
     * @param latterBranch
     *          the latter branch
     * @return the latter branch translation
     */
    public DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranchView,
        double branchPadding);
  }

  /**
   * Sets the location of the given sub branch in the current branch. Branches are created and located with their
   * baseline node at x == 0, y == 0. The bounds of the sub branch (and its sub sub-branches) are from negative
   * x-coordinates up to positive x-coordinates. The purpose of this method is to translate the whole sub branch to the
   * correct location to the right or to the left of its branch point.
   * 
   * @param subBranchView
   *          the sub branch to layout in the current branch
   * @param branchPointNode
   *          the branch point node the given sub branch is attached to
   * @param branchView
   *          the branch view
   */
  public void setSubBranchViewLocation(BranchView branchView, BranchView subBranchView, BranchPointNode branchPointNode)
  {
    currentTranslationStrategy = getSubBranchTranslationStrategy(currentTranslationStrategy);
    // translate branch off the branchPointNode (to the right or to the left)
    DisplayIndependentDimension translation = currentTranslationStrategy.getTranslationToBranchPoint(
        subBranchView, branchPointNode, getBranchPadding());
    BranchView latterBranch = branchView.getSecondToLastSubBranchView();
    if (latterBranch != null && !GeometryUtils.bottomEndsBefore(subBranchView.getBounds(), latterBranch.getBounds()))
    {
      // collides vertically with latter sub-branch -> additionally translate off latter branch (to the right or to
      // the left)
      GeometryUtils.union(translation, currentTranslationStrategy.getTranslationToLatterBranch(subBranchView,
          latterBranch, getBranchPadding()));
    }
    translateBy(subBranchView, translation);
  }

  /**
   * Returns the strategy that translates the next branch view. It's called for each sub branch view.
   * 
   * @param subBranchViewTranslation
   *          the current translation strategy for sub branch views
   * @return the current sub branch strategy
   * @see #LEFT
   * @see #RIGHT
   */
  protected abstract SubBranchViewTranslation getSubBranchTranslationStrategy(SubBranchViewTranslation subBranchViewTranslation);
}
