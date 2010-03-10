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

import org.eclipse.emf.cdo.ui.internal.branch.model.AbstractBranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.model.BranchPointNode;

import java.util.Collection;

/**
 * A Branch is a structure that holds the root node of a branch. Its main purpose is to climb through the branch tree
 * and call the layout strategy on all nodes in an appropriate manner.
 * <p>
 * The strategy is to first lay out all (sibling) nodes in the order of their time stamp. Sub-branches are skipped. In a
 * second step all branches are positioned while beginning with the latest one (in terms of time stamp).
 * 
 * @author Andre Dietisheim
 * @see BranchLayoutStrategy
 */
public class Branch
{
  private AbstractBranchPointNode baselineNode;

  private BranchLayoutStrategy layoutStrategy;

  public Branch(AbstractBranchPointNode branchRootNode)
  {
    layoutStrategy = new BranchLayoutStrategy();
    baselineNode = branchRootNode;
    layoutStrategy.setRootNode(branchRootNode);

    addNode(branchRootNode.getNextSibling());

    if (branchRootNode instanceof BranchPointNode)
    {
      // add a branch to this node
      BranchPointNode branchpointNode = (BranchPointNode)branchRootNode;
      addBranch(branchpointNode.getNextChild(), branchpointNode);
    }
  }

  public AbstractBranchPointNode getBaselineNode()
  {
    return baselineNode;
  }

  public Collection<AbstractBranchPointNode> getNodes()
  {
    return layoutStrategy.nodeDeque;
  }

  public BranchLayoutStrategy getLayoutStrategy()
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
   * @see #addBranch(AbstractBranchPointNode, BranchPointNode)
   */
  private void addNode(AbstractBranchPointNode node)
  {
    if (node != null)
    {
      layoutStrategy.addNode(node);
      // recursively navigate to sibling
      addNode(node.getNextSibling());

      if (node instanceof BranchPointNode)
      {
        // add a branch to this node
        BranchPointNode branchpointNode = (BranchPointNode)node;
        addBranch(branchpointNode.getNextChild(), branchpointNode);
      }
    }
  }

  /**
   * Adds a sub-branch to the given branch point node with the given root node.
   */
  private void addBranch(AbstractBranchPointNode rootNode, BranchPointNode branchPointNode)
  {
    if (rootNode != null)
    {
      Branch subBranch = new Branch(rootNode);
      // System.err.println("-----------------------------");
      // System.err.println("branch point node: " + branchPointNode.getTimeStamp());
      // System.err.println("subbranch: " + subBranch.getRootNode().getTimeStamp());
      // System.err.println("subbranch.x = " + subBranch.getLayoutStrategy().getBounds().x);
      // System.err.println("subbranch.width = " + subBranch.getLayoutStrategy().getBounds().width);
      // System.err.println("-----------------------------");
      layoutStrategy.addBranch(subBranch, branchPointNode);
    }
  }
}
