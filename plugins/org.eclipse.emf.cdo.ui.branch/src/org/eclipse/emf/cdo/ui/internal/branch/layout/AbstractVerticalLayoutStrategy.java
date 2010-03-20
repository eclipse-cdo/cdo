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
 * A base strategy that layouts a branch view vertically. It centers its (sibling) nodes below each other while using
 * their time stamp to set the y coordinate.
 * <p>
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

    public DisplayIndependentDimension getTranslationToLaterBranch(BranchView subBranchView, BranchView laterBranch,
        double branchPadding)
    {
      DisplayIndependentRectangle laterBranchBounds = laterBranch.getBounds();
      return new DisplayIndependentDimension(//
          GeometryUtils.getTranslation(subBranchView.getBounds().x, laterBranchBounds.x) //
              + laterBranchBounds.width //
              + branchPadding, 0);
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

    public DisplayIndependentDimension getTranslationToLaterBranch(BranchView subBranchView,
        BranchView laterBranchView, double branchPadding)
    {
      return new DisplayIndependentDimension( //
          GeometryUtils.getTranslation(subBranchView.getBounds().x, laterBranchView.getBounds().x) //
              - branchPadding, 0);
    }
  };

  protected static interface SubBranchViewTranslation
  {
    /**
     * Returns the offset that's needed to translate the given branch view so that it does not overlap with the branch
     * point.
     * 
     * @param branchPointNode
     *          the branch point node
     * @param subBranchView
     *          the sub branch view to translate
     * @param branchPadding
     *          the padding between branches
     * @return the translation that avoids the overlapping
     */
    public DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranchView,
        BranchPointNode branchPointNode, double branchPadding);

    /**
     * Returns the offset that's needed to translate the given branch view so that it does not overlap with the given
     * later branch view.
     * 
     * @param subBranchView
     *          the sub branch view to translate
     * @param latterBranchView
     *          the later branch view that the sub branch overlaps
     * @param branchPadding
     *          the padding between branches
     * @return the translation that avoids the overlapping
     */
    public DisplayIndependentDimension getTranslationToLaterBranch(BranchView subBranchView,
        BranchView latterBranchView, double branchPadding);
  }

  /**
   * Sets the location of the given sub branch in the current branch. Branches are created and located with the center
   * of the baseline node at x == 0, y == 0. The bounds of the sub branch (and its sub sub-branches) reach from negative
   * x-coordinates up to positive x-coordinates. The purpose of this method is to translate the whole sub branch to a
   * location where it does not overlap the branch point. This method either applies a translation off the branch point
   * or a translation off the later branch, it would overlap.
   * 
   * @param subBranchView
   *          the sub branch view to layout in the current branch
   * @param branchPointNode
   *          the branch point node the sub branch is attached to
   * @param branchView
   *          the branch view the sub branch shall be attached to
   */
  @Override
  public void setSubBranchViewLocation(BranchView branchView, BranchView subBranchView, BranchPointNode branchPointNode)
  {
    currentTranslationStrategy = getSubBranchTranslationStrategy(branchView, currentTranslationStrategy);
    BranchView laterBranch = getLaterOverlapingBranch(branchView, subBranchView);
    DisplayIndependentDimension translation = new DisplayIndependentDimension(0, 0);
    if (laterBranch != null)
    {
      // overlaps with later sub-branch -> translate from later branch (to the right or to
      // the left)
      translation = currentTranslationStrategy.getTranslationToLaterBranch(subBranchView, laterBranch,
          getBranchPadding());
    }
    else
    {
      // translate branch away from branchPointNode (to the right or to the left)
      translation = currentTranslationStrategy.getTranslationToBranchPoint(subBranchView, branchPointNode,
          getBranchPadding());
    }
    translateBy(subBranchView, translation);
  }

  /**
   * Returns the strategy that translates the next branch view. It's called for each sub branch view.
   * 
   * @param branchView
   *          the branch view to layout
   * @param currentTranslationStrategy
   *          the current translation strategy
   * @return the current sub branch strategy
   * @see #LEFT
   * @see #RIGHT
   */
  protected abstract SubBranchViewTranslation getSubBranchTranslationStrategy(BranchView branchView,
      SubBranchViewTranslation currentTranslationStrategy);

  /**
   * Gets the later sub branch view, that possibly overlaps with the given sub branch view. Returns <tt>null</tt> if
   * there's none that overlaps.
   * 
   * @param subBranchView
   *          the sub branch view that shall be checked for collisions
   * @param branchView
   *          the branch view the sub branch view is attached to
   * @return the colliding later branch or <tt>null</tt> if there's none
   */
  public abstract BranchView getLaterOverlapingBranch(BranchView branchView, BranchView subBranchView);
}
