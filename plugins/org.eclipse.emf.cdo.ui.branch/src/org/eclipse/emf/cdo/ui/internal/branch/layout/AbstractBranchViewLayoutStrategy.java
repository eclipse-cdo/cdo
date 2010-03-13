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
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchTreeUtils;

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
    BranchTreeUtils.setInternalSize(node);
    setBaselineNodeLocation(node);
    initBranchBounds(branchView, node);
  }

  protected void initBranchBounds(BranchView branchView, AbstractBranchPointNode node)
  {
    InternalNode baselineInternalNode = BranchTreeUtils.getInternalNode(node);
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
    BranchTreeUtils.setInternalSize(node);
    setSameBranchNodeLocation(node, previousNode);
    setBranchBounds(branchView, node);
  }

  /**
   * Sets the bounds of the given node. The node is centered horizontally to the given previous node.
   */
  protected void setBaselineNodeLocation(AbstractBranchPointNode node)
  {
    double y = node.getTimeStamp();
    double x = 0;
    BranchTreeUtils.setInternalLocation(node, x, y);
  }

  /**
   * Sets the location of the given node. The node is centered horizontally to the given previous (sibling) node.
   */
  protected void setSameBranchNodeLocation(AbstractBranchPointNode node, AbstractBranchPointNode previousNode)
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

    GeometryUtils.union(branchView.getBounds(), //
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
  public void layoutSubBranchView(BranchView branchView, BranchView subBranchView, BranchPointNode branchPointNode)
  {
    if (subBranchView != null)
    {
      setBranchViewLocation(branchView, subBranchView, branchPointNode);
      GeometryUtils.union(branchView.getBounds(), subBranchView.getBounds());
    }
  }

  protected abstract void setBranchViewLocation(BranchView branchView, BranchView subBranchView,
      BranchPointNode branchPointNode);
}
