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

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

/**
 * Various utility methods that help to deal with the manipulations needed to build a BranchTree.
 * 
 * @author Andre Dietisheim
 */
public class BranchPointNodeUtils
{

  /**
   * returns a BranchGraphNode for a given internal Node.
   * 
   * @param internalNode
   *          the internal node
   * @return the branch graph node
   * @see InternalNode
   * @see AbstractBranchPointNode
   */
  public static AbstractBranchPointNode getBranchPointNode(InternalNode internalNode)
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
   * @param branchPointNode
   *          the branch graph node
   * @return the internal node
   * @see AbstractBranchPointNode
   * @see InternalNode
   */
  public static InternalNode getInternalNode(AbstractBranchPointNode branchPointNode)
  {
    InternalNode internalNode = null;
    Object layoutInformation = branchPointNode.getLayoutEntity().getLayoutInformation();
    if (layoutInformation instanceof InternalNode)
    {
      internalNode = (InternalNode)layoutInformation;
    }
    return internalNode;
  }

  /**
   * Returns the center of the given node on the x-axis.
   * 
   * @param node
   *          the node to get the x-center from
   * @return the center of the given node on the x-axis
   */
  public static double getCenterX(AbstractBranchPointNode node)
  {
    InternalNode internalNode = getInternalNode(node);
    return internalNode.getInternalX() + internalNode.getInternalWidth() / 2;
  }

  /**
   * Centers the x coordinate of the given target node compared to the given source node and returns the result. If
   * source node is <tt>null</tt>, the target node is translated to the right by the half of its own width (centered on
   * its own location).
   * 
   * @param nodeToCenter
   *          the node to center
   * @param nodeToCenterOn
   *          the node to center on
   * @return the centered x
   */
  public static double getCenteredX(InternalNode nodeToCenter, InternalNode nodeToCenterOn)
  {
    if (nodeToCenterOn == null)
    {
      return nodeToCenter.getInternalX() - nodeToCenter.getInternalWidth() / 2;
    }

    return nodeToCenterOn.getInternalX() + (nodeToCenterOn.getInternalWidth() - nodeToCenter.getInternalWidth()) / 2;
  }

  /**
   * Centers the x coordinate of the given target node.
   * 
   * @param sourceInternalNode
   *          the internal node to center on the x-axis
   * @return the centered x
   */
  public static double getCenteredX(InternalNode sourceInternalNode)
  {
    return getCenteredX(sourceInternalNode, null);
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
   * Translates the internal location of the internal node that displays the given branch tree node.
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
   * @param sourcePositionNode
   *          the source node
   * @param y
   *          the y coordinate to apply
   */
  public static void centerHorizontally(AbstractBranchPointNode nodeToBeCentered,
      AbstractBranchPointNode sourcePositionNode, double y)
  {

    InternalNode internalNode = getInternalNode(nodeToBeCentered);
    double x = getCenteredX(internalNode, getInternalNode(sourcePositionNode));
    internalNode.setInternalLocation(x, y);
  }

  /**
   * Centers the internal node (which displays the given branch tree node) horizontally. It gets shifted to the right by
   * the half of its own width.
   * 
   * @param nodeToBeCentered
   *          the node to be centered
   * @param y
   *          the y coordinate to apply
   */
  public static void centerHorizontally(AbstractBranchPointNode nodeToBeCentered, double y)
  {
    InternalNode internalNode = getInternalNode(nodeToBeCentered);
    double x = getCenteredX(internalNode);
    internalNode.setInternalLocation(x, y);
  }

  /**
   * Returns the bounds of the given node.
   * 
   * @param node
   *          the node to get the bounds of
   * @return the bounds of the given node
   */
  public static DisplayIndependentRectangle getBounds(AbstractBranchPointNode node)
  {
    return getBounds(getInternalNode(node));
  }

  /**
   * Returns the bounds of the given internal node.
   * 
   * @param node
   *          the node to retrieve the bounds of
   * @return the bounds
   */
  private static DisplayIndependentRectangle getBounds(InternalNode node)
  {
    return new DisplayIndependentRectangle(node.getInternalX(), node.getInternalY(), node.getInternalWidth(), node
        .getInternalHeight());
  }
}
