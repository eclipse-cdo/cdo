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
package org.eclipse.emf.cdo.ui.internal.branch.item;


import org.eclipse.zest.layouts.dataStructures.InternalNode;

/**
 * Various utility methods used by the branch graph
 * 
 * @author Andre Dietisheim
 */
public class BranchTreeUtils
{
  /**
   * returns a BranchGraphNode for a given internal Node
   * 
   * @param internalNode
   *          the internal node
   * @return the branch graph node
   * @see InternalNode
   * @see AbstractBranchPointNode
   */
  public static AbstractBranchPointNode getBranchTreeNode(InternalNode internalNode)
  {
    AbstractBranchPointNode branchGraphNode = null;
    Object graphData = internalNode.getLayoutEntity().getGraphData();
    if (graphData != null && graphData instanceof AbstractBranchPointNode)
    {
      branchGraphNode = (AbstractBranchPointNode)graphData;
    }
    return branchGraphNode;
  }

  /**
   * Returns an internal node for a given branch graph node.
   * 
   * @param branchGraphNode
   *          the branch graph node
   * @return the internal node
   * @see AbstractBranchPointNode
   * @see InternalNode
   */
  public static InternalNode getInternalNode(AbstractBranchPointNode branchGraphNode)
  {
    InternalNode internalNode = null;
    Object layoutInformation = branchGraphNode.getLayoutEntity().getLayoutInformation();
    if (layoutInformation instanceof InternalNode)
    {
      internalNode = (InternalNode)layoutInformation;
    }
    return internalNode;
  }

  /**
   * Centers the x coordinate of the given target node compared to the given source node
   * 
   * @param targetNode
   *          the target node
   * @param sourceNode
   *          the source node
   * @return the centered x
   */
  public static double getCenteredX(AbstractBranchPointNode targetNode, AbstractBranchPointNode sourceNode)
  {
    InternalNode sourceInternalNode = getInternalNode(sourceNode);
    return sourceInternalNode.getInternalX()
        + ((sourceInternalNode.getInternalWidth() - getInternalNode(targetNode).getInternalWidth()) / 2);
  }

  /**
   * Sets the internal size of the internal node that displays the given branch tree node.
   * 
   * @param node
   *          the branch tree nodes whose internal node shall be set in (internal) size
   * @see InternalNode
   * @see InternalNode#setInternalSize(double, double)
   * @see AbstractBranchPointNode#getSize()
   */
  public static void setInternalSize(AbstractBranchPointNode node)
  {
    InternalNode internalNode = getInternalNode(node);
    internalNode.setInternalSize(node.getSize().preciseWidth(), node.getSize().preciseHeight());
  }

  /**
   * Translates the internal location of the internal node that displays the given branch tree node
   * 
   * @param node
   *          the node whose internal node shall be translated
   * @param deltaX
   *          the delta x
   * @param deltaY
   *          the delta y
   * @see InternalNode
   * @see InternalNode#getInternalX()
   * @see InternalNode#getInternalY()
   * @see InternalNode#setInternalLocation(double, double)
   */
  public static void translateInternalLocation(AbstractBranchPointNode node, double deltaX, double deltaY)
  {
    InternalNode internalNode = getInternalNode(node);
    internalNode.setInternalLocation(internalNode.getInternalX() + deltaX, internalNode.getInternalY() + deltaY);
  }

  /**
   * Sets the internal location of the internal node which displays the given branch tree node.
   * 
   * @param node
   *          the node whose internal node's internal location shall be set
   * @param x
   *          the x
   * @param y
   *          the y
   */
  public static void setInternalLocation(AbstractBranchPointNode node, double x, double y)
  {
    InternalNode internalNode = getInternalNode(node);
    internalNode.setInternalLocation(x, y);
  }

  /**
   * Centers the internal node (which displays the given branch tree node) horizontally relatively to the (internal node
   * that displays) the given source node.
   * 
   * @param nodeToBeCentered
   *          the node to be centered
   * @param sourceNode
   *          the source node
   * @param y
   *          the y coordinate to apply
   */
  public static void centerHorizontally(AbstractBranchPointNode nodeToBeCentered, AbstractBranchPointNode sourceNode, double y)
  {
    double x = getCenteredX(nodeToBeCentered, sourceNode);
    InternalNode internalNode = getInternalNode(nodeToBeCentered);
    internalNode.setInternalLocation(x, y);
  }
}
