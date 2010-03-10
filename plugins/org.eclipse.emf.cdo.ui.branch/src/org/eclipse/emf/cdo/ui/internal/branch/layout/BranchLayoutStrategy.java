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

import org.eclipse.emf.cdo.ui.internal.branch.geometry.ExtendedDisplayIndependentRectangle;
import org.eclipse.emf.cdo.ui.internal.branch.geometry.GeometryUtils;
import org.eclipse.emf.cdo.ui.internal.branch.item.AbstractBranchPointNode;
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
public class BranchLayoutStrategy
{
  private final SubBranchSproutingStrategy sproutRight = new SubBranchSproutingStrategy()
  {
    @Override
    protected void addSubBranchReference(BranchView branchView, BranchView subBranchView)
    {
      branchView.addRightSproutingBranch(subBranchView);
    }

    @Override
    protected BranchView getLatestSubBranch(BranchView branchView)
    {
      return branchView.getLatestRightSubBranchView();
    }

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
      ExtendedDisplayIndependentRectangle latterBranchBounds = latterBranch.getBounds();
      return new DisplayIndependentDimension(//
          latterBranchBounds.x + latterBranchBounds.width, 0);
    }
  };

  private final SubBranchSproutingStrategy sproutLeft = new SubBranchSproutingStrategy()
  {
    @Override
    protected void addSubBranchReference(BranchView branchView, BranchView subBranchView)
    {
      branchView.addLeftSproutingBranch(subBranchView);
    }

    @Override
    protected BranchView getLatestSubBranch(BranchView branchView)
    {
      return branchView.getLatestLeftSubBranchView();
    }

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
  };

  private SubBranchSproutingStrategy currentSproutingStrategy = sproutRight;

  protected BranchLayoutStrategy()
  {
  }

  public void layoutBaselineNode(BranchView branchView, AbstractBranchPointNode node)
  {
    BranchTreeUtils.setInternalSize(node);
    setBaselineNodeLocation(node);
    initBranchBounds(branchView, node);
  }

  protected void initBranchBounds(BranchView branchView, AbstractBranchPointNode node)
  {
    InternalNode baselineInternalNode = BranchTreeUtils.getInternalNode(node);
    ExtendedDisplayIndependentRectangle bounds = new ExtendedDisplayIndependentRectangle(baselineInternalNode
        .getInternalX(), baselineInternalNode.getInternalY(), baselineInternalNode.getInternalWidth(),
        baselineInternalNode.getInternalHeight());
    branchView.setBounds(bounds);
  }

  /**
   * Layout the given node as sibling node to the baseline node (and its siblings).
   * 
   * @param branchView
   *          the branch view to layout the node to
   * @param node
   *          the node to layout
   */
  public void layoutNode(BranchView branchView, AbstractBranchPointNode node, AbstractBranchPointNode previousNode)
  {
    BranchTreeUtils.setInternalSize(node);
    setSiblingNodeLocation(node, previousNode);
    setBranchBounds(branchView, node);
  }

  /**
   * Sets the bounds of the given node. The node is centered horizontally to the given previous node.
   */
  protected void setBaselineNodeLocation(AbstractBranchPointNode node)
  {
    BranchTreeUtils.setInternalSize(node);

    double y = node.getTimeStamp();
    double x = 0;
    BranchTreeUtils.setInternalLocation(node, x, y);
  }

  /**
   * Sets the location of the given node. The node is centered horizontally to the given previous (sibling) node.
   */
  protected void setSiblingNodeLocation(AbstractBranchPointNode node, AbstractBranchPointNode previousNode)
  {
    double y = node.getTimeStamp();
    BranchTreeUtils.centerHorizontally(node, previousNode, y);
  }

  /**
   * Sets the bounds of the current branch for the given additional node. The bounds are expanded if the size of the
   * node requires it.
   */
  protected void setBranchBounds(BranchView branchView, AbstractBranchPointNode node)
  {
    InternalNode internalNode = BranchTreeUtils.getInternalNode(node);

    branchView.getBounds().union( //
        internalNode.getInternalX() //
        , internalNode.getInternalY() //
        , internalNode.getInternalWidth() //
        , internalNode.getInternalHeight());
  }

  /**
   * Adds the given sub branch to this branch. This strategy distributes the sub-branches equally to the left and to the
   * right of this branch. It starts by putting the last sub branch to the right of the current branch and puts the
   * previous one to the left etc. .
   * 
   * @param branchPointNode
   *          the node on the current branch view that the sub-branch view shall be attached to
   * @param subBranchView
   *          the sub branch view to add
   * @param branchView
   *          the branch view
   */
  public void layoutBranch(BranchView branchView, BranchView subBranchView, BranchPointNode branchPointNode)
  {
    if (subBranchView != null)
    {
      currentSproutingStrategy.setSubBranchLocation(branchView, subBranchView, branchPointNode);
      branchView.getBounds().union(subBranchView.getBounds());
      currentSproutingStrategy.switchSproutingStrategy();
    }
  }

  /**
   * Translates this branch by the given dimension.
   */
  public void translate(BranchView branchView, DisplayIndependentDimension dimension)
  {
    translateSiblingNodes(branchView, dimension);
    translateSubBranches(branchView, dimension);
    branchView.getBounds().translate(dimension.width, dimension.height);
  }

  /**
   * Translates all sub branches of the given branch.
   * 
   * @param dimension
   *          the dimension to translate this branch by
   */
  private void translateSubBranches(BranchView branchView, DisplayIndependentDimension dimension)
  {
    for (BranchView branch : branchView.getSubBranches())
    {
      branch.getLayoutStrategy().translate(branchView, dimension);
    }
  }

  /**
   * Translates all the sibling nodes of this branch by the given horizontal and vertical offset.
   */
  private void translateSiblingNodes(BranchView branchView, DisplayIndependentDimension dimension)
  {
    for (AbstractBranchPointNode node : branchView.getNodes())
    {
      BranchTreeUtils.translateInternalLocation(node, dimension.width, dimension.height);
    }
  }

  /**
   * A layout strategy that handles the layout of sub branches in this branch
   */
  protected abstract class SubBranchSproutingStrategy
  {
    /**
     * Adds the given sub branch view reference to the given branch view.
     * 
     * @param branchView
     *          the branch view to add the sub branch to
     * @param subBranchView
     *          the sub branch view to add
     */
    protected abstract void addSubBranchReference(BranchView branchView, BranchView subBranchView);

    /**
     * Gets the latest sub branch of the given sub branch view.
     * 
     * @param branchView
     *          the branch view to retrieve the latest sub branch from
     * @return the latest sub branch
     */
    protected abstract BranchView getLatestSubBranch(BranchView branchView);

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
      BranchView latterBranch = currentSproutingStrategy.getLatestSubBranch(branchView);
      if (latterBranch != null && !subBranchView.getBounds().bottomEndsBefore(latterBranch.getBounds()))
      {
        // collides vertically with latter sub-branch -> additionally translate off latter branch (to the right or to
        // the left)
        translation = GeometryUtils.union(translation, getTranslationToLatterBranch(subBranchView, latterBranch));
      }
      translate(subBranchView, translation);
      addSubBranchReference(branchView, subBranchView);
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
     * 
     * @return the sub branch sprouting strategy
     */
    protected void switchSproutingStrategy()
    {
      if (currentSproutingStrategy == sproutRight)
      {
        currentSproutingStrategy = sproutLeft;
      }
      else
      {
        currentSproutingStrategy = sproutRight;
      }
    }

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
