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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.ui.internal.branch.item.AbstractBranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchPointNode;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

import java.util.Collection;

/**
 * A branch view is a logical structure that holds all nodes and sub branches. Its main purpose is to climb through the
 * branch tree and call the layout strategy on all nodes in an appropriate manner.
 * <p>
 * The strategy applied is to first lay out all (sibling) nodes in the order of their time stamp. Sub-branches are
 * skipped. In a second step all branches are positioned within the branch. The strategy starts with the latest one (in
 * terms of time stamp).
 * 
 * @author Andre Dietisheim
 * @see HorizontallyAlternatingSubBranches
 */
public class BranchView
{
  private CDOBranch branch;

  private AbstractBranchPointNode baselineNode;

  protected Deque<AbstractBranchPointNode> nodes = new Deque<AbstractBranchPointNode>();

  private Deque<BranchView> subBranchViews = new Deque<BranchView>();

  private DisplayIndependentRectangle bounds;

  private BranchViewLayoutStrategy layoutStrategy;

  public BranchView(AbstractBranchPointNode baselineNode, BranchViewLayoutStrategy layoutStrategy)
  {
    branch = baselineNode.getBranch();
    this.baselineNode = baselineNode;
    this.layoutStrategy = layoutStrategy;
    nodes.addLast(baselineNode);
    layoutStrategy.layoutBaselineNode(this, baselineNode);
    addNode(baselineNode.getNextOnSameBranch());

    if (baselineNode instanceof BranchPointNode)
    {
      // add a branch to this node
      BranchPointNode branchpointNode = (BranchPointNode)baselineNode;
      addBranchView(branchpointNode.getNextOnNewBranch(), branchpointNode);
    }
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public AbstractBranchPointNode getBaselineNode()
  {
    return baselineNode;
  }

  public Collection<AbstractBranchPointNode> getNodes()
  {
    return nodes;
  }

  public BranchViewLayoutStrategy getLayoutStrategy()
  {
    return layoutStrategy;
  }

  /**
   * Adds the given node to this branch. Climbs recursively up to all (sibling) nodes on the same branch. When it gets
   * back from recursion it builds and attaches branches to those nodes.
   * <p>
   * The strategy is to add all sibling nodes in the order of their time stamp and to add the branches in the reverse
   * (in terms of time stamp) order
   * 
   * @see #addBranchView(AbstractBranchPointNode, BranchPointNode)
   */
  private void addNode(AbstractBranchPointNode node)
  {
    if (node != null)
    {
      AbstractBranchPointNode previousNode = nodes.peekLast();
      nodes.addLast(node);
      layoutStrategy.layoutNode(this, node, previousNode);
      // recursively navigate to sibling
      addNode(node.getNextOnSameBranch());

      if (node instanceof BranchPointNode)
      {
        // add a branch to this node
        BranchPointNode branchPointNode = (BranchPointNode)node;
        addBranchView(branchPointNode.getNextOnNewBranch(), branchPointNode);
      }
    }
  }

  /**
   * Adds a sub-branch to the given branch point node with the given baseline node.
   */
  private void addBranchView(AbstractBranchPointNode baselineNode, BranchPointNode branchPointNode)
  {
    if (baselineNode != null)
    {
      BranchView subBranchView = new BranchView(baselineNode, getLayoutStrategy());
      layoutStrategy.layoutSubBranchView(this, subBranchView, branchPointNode);
      subBranchViews.add(subBranchView);
    }
  }

  /**
   * Returns the bounds of this branch view. The bounds returned contain all sub-branches (and their nodes)
   * 
   * @return the bounds
   */
  public DisplayIndependentRectangle getBounds()
  {
    return bounds;
  }

  /**
   * Sets the bounds of this branch view. The bounds must contain this branch and all its sub branch views (and all
   * their nodes)
   * 
   * @param bounds
   *          the new bounds
   */
  public void setBounds(DisplayIndependentRectangle bounds)
  {
    this.bounds = bounds;
  }

  /**
   * Returns not the last, but the one before the last sub branch view. If none is present, <tt>null<tt> is returned.
   * 
   * @return the second to last sub branch view or <tt>null<tt>
   */
  public BranchView getSecondToLastSubBranchView()
  {
    if (subBranchViews.isEmpty() || subBranchViews.size() < 2)
    {
      return null;
    }

    return subBranchViews.get(subBranchViews.size() - 1);
  }

  /**
   * Returns all sub branch views present in this branch view.
   * 
   * @return all sub branch views in this branch view
   */
  public Collection<BranchView> getSubBranchViews()
  {
    return subBranchViews;
  }

  public void resetBounds()
  {
    bounds = null;
  }

  public boolean areBoundsSet()
  {
    return bounds != null;
  }
}
