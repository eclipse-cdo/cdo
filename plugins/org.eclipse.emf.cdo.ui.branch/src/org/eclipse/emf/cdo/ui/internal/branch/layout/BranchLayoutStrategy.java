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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
  private final SubBranchSproutingStrategy SPROUT_RIGHT = new SubBranchSproutingStrategy()
  {
    @Override
    protected void addSubBranchReference(BranchView branch)
    {
      rightSproutingBranches.addFirst(branch);
    }

    @Override
    protected BranchView getLatestSubBranch()
    {
      return rightSproutingBranches.peekFirst();
    }

    @Override
    protected DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode)
    {
      InternalNode branchPointInternalNode = BranchTreeUtils.getInternalNode(branchPointNode);
      return new DisplayIndependentDimension( //
          // translate branch completely to visible area
          Math.abs(subBranch.getLayoutStrategy().getBounds().x)
          // add branch point position and its width
              + branchPointInternalNode.getInternalX() + branchPointInternalNode.getInternalWidth() //
              // add padding
              + getBranchPadding(), 0);
    }

    @Override
    protected DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranch)
    {
      ExtendedDisplayIndependentRectangle latterBranchBounds = latterBranch.getLayoutStrategy().getBounds();
      return new DisplayIndependentDimension(//
          latterBranchBounds.x + latterBranchBounds.width, 0);
    }
  };

  private final SubBranchSproutingStrategy SPROUT_LEFT = new SubBranchSproutingStrategy()
  {
    @Override
    protected void addSubBranchReference(BranchView branch)
    {
      leftSproutingBranches.addFirst(branch);
    }

    @Override
    protected BranchView getLatestSubBranch()
    {
      return leftSproutingBranches.peekFirst();
    }

    @Override
    protected DisplayIndependentDimension getTranslationToBranchPoint(BranchView subBranch,
        BranchPointNode branchPointNode)
    {
      InternalNode branchPointInternalNode = BranchTreeUtils.getInternalNode(branchPointNode);
      DisplayIndependentRectangle subBranchBounds = subBranch.getLayoutStrategy().getBounds();
      return new DisplayIndependentDimension(-( //
          // translate completely to invisible area
          subBranchBounds.width + subBranchBounds.x
          // add branch point position
              + branchPointInternalNode.getInternalX()
          // add branch padding
          + getBranchPadding()), 0);
    }

    @Override
    protected DisplayIndependentDimension getTranslationToLatterBranch(BranchView subBranch, BranchView latterBranch)
    {
      DisplayIndependentRectangle latterBranchBounds = latterBranch.getLayoutStrategy().getBounds();
      return new DisplayIndependentDimension( //
          latterBranchBounds.x - getBranchPadding(), 0);
    }
  };

  private SubBranchSproutingStrategy currentSproutingStrategy = SPROUT_RIGHT;

  // protected Deque<AbstractBranchPointNode> nodeDeque = new Deque<AbstractBranchPointNode>();
  //
  // private Deque<BranchView> leftSproutingBranches = new Deque<BranchView>();
  //
  // private Deque<BranchView> rightSproutingBranches = new Deque<BranchView>();
  //
  // private ExtendedDisplayIndependentRectangle bounds;
  //
  // private ExtendedDisplayIndependentRectangle siblingBounds;

  protected BranchLayoutStrategy()
  {
  }

  public void setRootNode(AbstractBranchPointNode node)
  {
    nodeDeque.add(node);
    BranchTreeUtils.setInternalSize(node);
    setRootNodeLocation(node);
    initBranchBounds(node);
  }

  protected void initBranchBounds(AbstractBranchPointNode node)
  {
    InternalNode rootInternalNode = BranchTreeUtils.getInternalNode(node);
    bounds = new ExtendedDisplayIndependentRectangle(rootInternalNode.getInternalX(), rootInternalNode.getInternalY(),
        rootInternalNode.getInternalWidth(), rootInternalNode.getInternalHeight());
    siblingBounds = new ExtendedDisplayIndependentRectangle(bounds);
  }

  /**
   * Layout the given node as sibling node of the root (and its siblings) node.
   */
  public void addNode(AbstractBranchPointNode node)
  {
    AbstractBranchPointNode previousNode = nodeDeque.peekLast();
    nodeDeque.add(node);
    BranchTreeUtils.setInternalSize(node);
    setSiblingNodeLocation(node, previousNode);
    setBranchBounds(node);
  }

  /**
   * Sets the bounds of the given node. The node is centered horizontally to the given previous node.
   */
  protected void setRootNodeLocation(AbstractBranchPointNode node)
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
  protected void setBranchBounds(AbstractBranchPointNode node)
  {
    InternalNode internalNode = BranchTreeUtils.getInternalNode(node);

    bounds.union( //
        internalNode.getInternalX() //
        , internalNode.getInternalY() //
        , internalNode.getInternalWidth() //
        , internalNode.getInternalHeight());

    siblingBounds.union(//
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
   *          the node on the current branch, the sub-branch's attached to
   * @param subBranch
   *          the sub branch to add
   * @return the branch that precedes the given branch
   */
  public void layoutBranch(BranchView subBranch, BranchPointNode branchPointNode)
  {
    if (subBranch != null)
    {
      currentSproutingStrategy.setSubBranchLocation(subBranch, branchPointNode);
      bounds.union(subBranch.getLayoutStrategy().getBounds());
      currentSproutingStrategy.switchSproutingStrategy();
    }
  }

  /**
   * Gets all nodes within this branch.
   * 
   * @see AbstractBranchPointNode
   */
  public Collection<AbstractBranchPointNode> getNodes()
  {
    return nodeDeque;
  }

  /**
   * Translates this branch by the given dimension.
   */
  public void translate(DisplayIndependentDimension dimension)
  {
    translateSiblingNodes(dimension);
    translateSubBranches(dimension);
    bounds.translate(dimension.width, dimension.height);
  }

  /**
   * Translates all sub branches of the given branch.
   * 
   * @param dimension
   *          the dimension to translate this branch by
   */
  private void translateSubBranches(DisplayIndependentDimension dimension)
  {
    for (BranchView branch : getSubBranches())
    {
      branch.getLayoutStrategy().translate(dimension);
    }
  }

  /**
   * Returns all sub branches of this branch.
   * 
   * @return the sub branches
   */
  Collection<BranchView> getSubBranches()
  {
    List<BranchView> branchList = new ArrayList<BranchView>(leftSproutingBranches);
    branchList.addAll(rightSproutingBranches);
    return branchList;
  }

  /**
   * Translates all the sibling nodes of this branch by the given horizontal and vertical offset.
   */
  private void translateSiblingNodes(DisplayIndependentDimension dimension)
  {
    for (AbstractBranchPointNode node : getNodes())
    {
      BranchTreeUtils.translateInternalLocation(node, dimension.width, dimension.height);
    }
  }

  /**
   * Returns whether the bounds of the given branch intersects the bounds of this branch.
   * 
   * @return true, if the given branch intersects this branch
   */
  boolean collidesWith(BranchView branch)
  {
    return bounds.bottomEndsBefore(branch.getLayoutStrategy().bounds);
  }

  /**
   * Gets the bounds.
   */
  ExtendedDisplayIndependentRectangle getBounds()
  {
    return bounds;
  }

  /**
   * A layout strategy that handles the layout of sub branches in this branch
   */
  protected abstract class SubBranchSproutingStrategy
  {
    /**
     * Adds the reference to the sub branch.
     * 
     * @param branch
     *          the branch
     */
    protected abstract void addSubBranchReference(BranchView branch);

    /**
     * Gets the latest sub branch.
     * 
     * @return the latest sub branch
     */
    protected abstract BranchView getLatestSubBranch();

    /**
     * Sets the location of the given sub branch in the current branch. Branches are created and located with their root
     * node at x == 0, y == 0. The bounds of the sub branch (and its sub sub-branches) are from negative x-coordinates
     * up to positive x-coordinates. The purpose of this method is to translate the whole sub branch to the correct
     * location to the right or to the left of its branch point.
     * 
     * @param subBranch
     *          the sub branch to layout in the current branch
     * @param branchPointNode
     *          the branch point node the given sub branch is attached to
     */
    void setSubBranchLocation(BranchView subBranch, BranchPointNode branchPointNode)
    {
      // translate branch off the branchPointNode (to the right or to the left)
      DisplayIndependentDimension translation = getTranslationToBranchPoint(subBranch, branchPointNode);
      ExtendedDisplayIndependentRectangle subBranchBounds = subBranch.getLayoutStrategy().getBounds();
      BranchView latterBranch = currentSproutingStrategy.getLatestSubBranch();
      if (latterBranch != null && !subBranchBounds.bottomEndsBefore(latterBranch.getLayoutStrategy().getBounds()))
      {
        // collides vertically with latter sub-branch -> additionally translate off latter branch (to the right or to
        // the left)
        translation = GeometryUtils.union(translation, getTranslationToLatterBranch(subBranch, latterBranch));
      }
      subBranch.getLayoutStrategy().translate(translation);
      addSubBranchReference(subBranch);
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
      if (currentSproutingStrategy == SPROUT_RIGHT)
      {
        currentSproutingStrategy = SPROUT_LEFT;
      }
      else
      {
        currentSproutingStrategy = SPROUT_RIGHT;
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
