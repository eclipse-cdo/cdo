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
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchTreeUtils;

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
public abstract class VerticallyDistributingLayoutStrategy extends AbstractBranchViewLayoutStrategy
{
  public static final SubBranchSproutingStrategy SPROUT_RIGHT = new SubBranchSproutingStrategy()
  {
    @Override
    protected DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode)
    {
      InternalNode branchPointInternalNode = BranchTreeUtils.getInternalNode(branchPointNode);
      return new DisplayIndependentDimension( //
          // translate branch completely to visible area
          Math.abs(subBranch.getBounds().x)
          // add branch point position and its width
              + branchPointInternalNode.getInternalX() + branchPointInternalNode.getInternalWidth() //
              // add padding
              + getBranchPadding(), 0);
    }

    @Override
    protected DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranch)
    {
      DisplayIndependentRectangle latterBranchBounds = latterBranch.getBounds();
      return new DisplayIndependentDimension(//
          latterBranchBounds.x + latterBranchBounds.width, 0);
    }

    @Override
    protected void switchSproutingStrategy()
    {
      currentSproutingStrategy = SPROUT_LEFT;
    }
  };

  public static final SubBranchSproutingStrategy SPROUT_LEFT = new SubBranchSproutingStrategy()
  {
    @Override
    protected DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode)
    {
      InternalNode branchPointInternalNode = BranchTreeUtils.getInternalNode(branchPointNode);
      DisplayIndependentRectangle subBranchBounds = subBranch.getBounds();
      return new DisplayIndependentDimension(-( //
          // translate completely to invisible area
          subBranchBounds.width + subBranchBounds.x
          // add branch point position
              + branchPointInternalNode.getInternalX()
          // add branch padding
          + getBranchPadding()), 0);
    }

    @Override
    protected DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranchView)
    {
      DisplayIndependentRectangle latterBranchBounds = latterBranchView.getBounds();
      return new DisplayIndependentDimension( //
          latterBranchBounds.x - getBranchPadding(), 0);
    }

    @Override
    protected void switchSproutingStrategy()
    {
      currentSproutingStrategy = SPROUT_RIGHT;
    }
  };

  private static SubBranchSproutingStrategy currentSproutingStrategy = null;

  @Override
  protected void doSetBranchViewLocation(BranchView branchView, BranchView subBranchView,
      BranchPointNode branchPointNode)
  {
    currentSproutingStrategy.setSubBranchLocation(branchView, subBranchView, branchPointNode);
    currentSproutingStrategy.switchSproutingStrategy();
  }

  /**
   * A layout strategy that handles the layout of sub branches in this branch
   */
  protected static abstract class SubBranchSproutingStrategy extends VerticallyDistributingLayoutStrategy
  {
    /**
     * Sets the location of the given sub branch in the current branch. Branches are created and located with their
     * baseline node at x == 0, y == 0. The bounds of the sub branch (and its sub sub-branches) are from negative
     * x-coordinates up to positive x-coordinates. The purpose of this method is to translate the whole sub branch to
     * the correct location to the right or to the left of its branch point.
     * 
     * @param subBranchView
     *          the sub branch to layout in the current branch
     * @param subBranchView2
     * @param branchPointNode
     *          the branch point node the given sub branch is attached to
     */
    void setSubBranchLocation(BranchView subBranchView, BranchView branchView, BranchPointNode branchPointNode)
    {
      // translate branch off the branchPointNode (to the right or to the left)
      DisplayIndependentDimension translation = getTranslationToBranchPoint(subBranchView, branchPointNode);
      BranchView latterBranch = branchView.getSecondToLastSubBranchView();
      if (latterBranch != null && !GeometryUtils.bottomEndsBefore(subBranchView.getBounds(), latterBranch.getBounds()))
      {
        // collides vertically with latter sub-branch -> additionally translate off latter branch (to the right or to
        // the left)
        translation = GeometryUtils.union(translation, getTranslationToLatterBranch(subBranchView, latterBranch));
      }
      translate(subBranchView, translation);
      branchView.addSubBranchView(subBranchView);
    }

    /**
     * Returns the offset that's needed to translate the given branch so that it does not collide with the branch point.
     * 
     * @param branchPointNode
     *          the branch point node
     * @param subBranch
     *          the sub branch
     * @return the branch point translation
     */
    protected abstract DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode);

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
    protected abstract DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch,
        BranchView latterBranch);

    /**
     * Switches the current sprouting strategy to the next strategy to apply after the current one .
     */
    protected abstract void switchSproutingStrategy();

    /**
     * Returns the branch padding that shall be applied betweend branches.
     * 
     * @return the branch padding
     */
    protected double getBranchPadding()
    {
      return 60;
    }
  }
}
