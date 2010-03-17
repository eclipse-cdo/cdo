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
import org.eclipse.emf.cdo.ui.internal.branch.item.AbstractBranchPointNode;
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
public abstract class AbstractBranchViewLayoutStrategy implements BranchViewLayoutStrategy
{
  protected AbstractBranchViewLayoutStrategy()
  {
  }

  public void layoutBaselineNode(BranchView branchView, AbstractBranchPointNode node)
  {
    BranchPointNodeUtils.setInternalSize(node);
    setBaselineNodeLocation(node);
    initBranchBounds(branchView, node);
  }

  protected void initBranchBounds(BranchView branchView, AbstractBranchPointNode node)
  {
    InternalNode baselineInternalNode = BranchPointNodeUtils.getInternalNode(node);
    DisplayIndependentRectangle bounds = new DisplayIndependentRectangle(baselineInternalNode.getInternalX(),
        baselineInternalNode.getInternalY(), baselineInternalNode.getInternalWidth(), baselineInternalNode
            .getInternalHeight());
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
    BranchPointNodeUtils.setInternalSize(node);
    setSameBranchNodeLocation(node, previousNode);
    setBranchBounds(branchView, node);
  }

  /**
   * Sets the bounds of the given node. The node is centered horizontally to the given previous node.
   */
  protected void setBaselineNodeLocation(AbstractBranchPointNode node)
  {
    double y = node.getTimeStamp();
    BranchPointNodeUtils.centerHorizontally(node, y);
  }

  /**
   * Sets the location of the given node. The node is centered horizontally to the given previous (sibling) node.
   */
  protected void setSameBranchNodeLocation(AbstractBranchPointNode node, AbstractBranchPointNode previousNode)
  {
    double y = node.getTimeStamp();
    BranchPointNodeUtils.centerHorizontally(node, previousNode, y);
  }

  /**
   * Sets the bounds of the current branch for the given additional node. The bounds are expanded if the size of the
   * node requires it.
   * 
   * @param branchView
   *          the branch view to set the bounds
   * @param node
   *          the node that was added and shall be included in the given branch view
   */
  protected void setBranchBounds(BranchView branchView, AbstractBranchPointNode node)
  {
    InternalNode internalNode = BranchPointNodeUtils.getInternalNode(node);

    DisplayIndependentRectangle bounds = GeometryUtils.union(branchView.getBounds(), //
        internalNode.getInternalX() //
        , internalNode.getInternalY() //
        , internalNode.getInternalWidth() //
        , internalNode.getInternalHeight());
    branchView.setBounds(bounds);
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
  public void layoutSubBranchView(BranchView branchView, BranchView subBranchView, BranchPointNode branchPointNode)
  {
    if (subBranchView != null)
    {
      setSubBranchViewLocation(branchView, subBranchView, branchPointNode);
      branchView.setBounds(GeometryUtils.union(branchView.getBounds(), subBranchView.getBounds()));
    }
  }

  /**
   * Sets the new given sub branch view to the new location.
   * <p>
   * Subclasses have to provide an algorithm that calculates and sets this location.
   * 
   * @param branchView
   *          the branch view
   * @param subBranchView
   *          the sub branch view
   * @param branchPointNode
   *          the branch point node
   */
  protected abstract void setSubBranchViewLocation(BranchView branchView, BranchView subBranchView,
      BranchPointNode branchPointNode);

  public void translateBy(BranchView branchView, DisplayIndependentDimension offsets)
  {
    translateBranchNodesBy(branchView, offsets);
    translateSubBranchesBy(branchView, offsets);
    GeometryUtils.translateRectangle(offsets.width, offsets.height, branchView.getBounds());
  }

  /**
   * Translates all sub branches of the given branch.
   * 
   * @param dimension
   *          the dimension to translate this branch by
   */
  protected void translateSubBranchesBy(BranchView branchView, DisplayIndependentDimension dimension)
  {
    for (BranchView subBranch : branchView.getSubBranchViews())
    {
      translateBy(subBranch, dimension);
    }
  }

  /**
   * Translates all the sibling nodes in this branch view. Applies the given horizontal and vertical offset.
   * 
   * @param branchView
   *          the branch view to translate the nodes of
   * @param dimension
   *          the dimension to translate by
   */
  protected void translateBranchNodesBy(BranchView branchView, DisplayIndependentDimension dimension)
  {
    for (AbstractBranchPointNode node : branchView.getNodes())
    {
      BranchPointNodeUtils.translateInternalLocation(node, dimension.width, dimension.height);
    }
  }

  public void scale(BranchView branchView, DisplayIndependentDimension scaling)
  {
    scaleSameBranchNodes(branchView, scaling);
    scaleSubBranches(branchView, scaling);
  }

  public void scale(BranchView branchView, DisplayIndependentRectangle targetBounds)
  {
    DisplayIndependentRectangle bounds = branchView.getBounds();
    DisplayIndependentDimension scaling = new DisplayIndependentDimension(bounds.width / targetBounds.width,
        bounds.height / targetBounds.height);
    scale(branchView, scaling);
    translateBy(branchView, GeometryUtils.getTranslation(branchView.getBounds(), targetBounds.x, targetBounds.y));
  }

  /**
   * Scales the positions of the nodes on the same branch view and sets the new bounds of the whole branch view.
   * 
   * @param branchView
   *          the branch view to scale the nodes of
   * @param scaling
   *          the scaling factor (x- and y-axis)
   */
  protected void scaleSameBranchNodes(BranchView branchView, DisplayIndependentDimension scaling)
  {
    double centerX = BranchPointNodeUtils.getCenterX(branchView.getBaselineNode());
    branchView.resetBounds();
    for (AbstractBranchPointNode node : branchView.getNodes())
    {
      InternalNode internalNode = BranchPointNodeUtils.getInternalNode(node);
      double newX = centerX / scaling.width - internalNode.getInternalWidth() / 2;
      double newY = internalNode.getInternalY() / scaling.height;
      internalNode.setInternalLocation(newX, newY);
      if (!branchView.areBoundsSet())
      {
        initBranchBounds(branchView, node);
      }
      else
      {
        setBranchBounds(branchView, node);
      }
    }
  }

  /**
   * Scales the sub branches of the given branch view.
   * 
   * @param branchView
   *          the branch view to scale the sub branches of
   * @param scaling
   *          the scaling factor (x- and y-axis)
   */
  protected void scaleSubBranches(BranchView branchView, DisplayIndependentDimension scaling)
  {
    for (BranchView subBranch : branchView.getSubBranchViews())
    {
      scale(subBranch, scaling);
      branchView.setBounds(GeometryUtils.union(branchView.getBounds(), subBranch.getBounds()));
    }
  }
}
