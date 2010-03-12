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

import org.eclipse.emf.cdo.ui.internal.branch.item.AbstractBranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchTreeUtils;
import org.eclipse.emf.cdo.ui.internal.branch.item.RootNode;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.BendPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * A layout algorithm that builds a tree of branch nodes. Nodes on the same branch are centered horizontally below each
 * other. Nodes on a new branch are shifted to the left/right - they sprout to the left/right. The description above
 * applies to a vertical layout of the branch tree. A horizontal strategy is not implemented yet.
 * 
 * @author Andre Dietisheim
 */
public class BranchTreeLayoutAlgorithm extends AbstractLayoutAlgorithm
{
  private static final int LAYOUT_STEPS = 2;

  private RootNode rootNode;

  private DisplayIndependentRectangle layoutBounds = null;

  private double widthToHeightRatio;

  /**
   * Constructs a new TreeLayoutAlgorithm object.
   */
  public BranchTreeLayoutAlgorithm(int styles)
  {
    super(styles);
  }

  /**
   * Tree layout algorithm Constructor with NO Style
   */
  public BranchTreeLayoutAlgorithm()
  {
    this(LayoutStyles.NONE);
  }

  @Override
  public void setLayoutArea(double x, double y, double width, double height)
  {
  }

  @Override
  protected int getCurrentLayoutStep()
  {
    return 0;
  }

  @Override
  protected int getTotalNumberOfLayoutSteps()
  {
    return LAYOUT_STEPS;
  }

  @Override
  protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
      double x, double y, double width, double height)
  {
    if (entitiesToLayout.length > 0)
    {
      initRootNode(entitiesToLayout);
    }
    layoutBounds = new DisplayIndependentRectangle(x, y, width, height);
  }

  /**
   * Searches the given entities and stores the root node
   * 
   * @param entitiesToLayout
   *          the entities to layout
   * @see RootNode
   * @see AbstractBranchPointNode
   */
  private void initRootNode(InternalNode[] entitiesToLayout)
  {
    for (InternalNode internalNode : entitiesToLayout)
    {
      AbstractBranchPointNode node = BranchTreeUtils.getBranchTreeNode(internalNode);
      if (node != null)
      {
        if (node instanceof RootNode)
        {
          rootNode = (RootNode)node.getLatter(rootNode);
        }
      }
    }
  }

  @Override
  protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
      double boundsX, double boundsY, double boundsWidth, double boundsHeight)
  {

    if (entitiesToLayout.length > 0)
    {
      BranchView branchView = buildBranch(rootNode);
      fireProgressEvent(1, LAYOUT_STEPS);
      defaultFitWithinBounds(entitiesToLayout, layoutBounds);
    }
  }

  private BranchView buildBranch(AbstractBranchPointNode branchRootNode)
  {
    return new BranchView(branchRootNode, new VerticallyDistributedSubBranches());
  }

  @Override
  protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider)
  {
    updateLayoutLocations(entitiesToLayout);
    fireProgressEvent(LAYOUT_STEPS, LAYOUT_STEPS);
  }

  @Override
  protected boolean isValidConfiguration(boolean asynchronous, boolean continueous)
  {
    if (asynchronous && continueous)
    {
      return false;
    }
    else if (asynchronous && !continueous)
    {
      return true;
    }
    else if (!asynchronous && continueous)
    {
      return false;
    }
    else if (!asynchronous && !continueous)
    {
      return true;
    }

    return false;
  }

  /**
   * @COPIED methods
   */

  /**
   * Find an appropriate size for the given nodes, then fit them into the given bounds. The relative locations of the
   * nodes to each other must be preserved. Child classes should set flag reresizeEntitiesAfterLayout to false if they
   * want to preserve node sizes.
   */
  @Override
  protected void defaultFitWithinBounds(InternalNode[] entitiesToLayout, InternalRelationship[] relationships,
      DisplayIndependentRectangle realBounds)
  {

    DisplayIndependentRectangle layoutBounds;

    if (resizeEntitiesAfterLayout)
    {
      layoutBounds = getLayoutBounds(entitiesToLayout, false);

      // Convert node x,y to be in percent rather than absolute coords
      convertPositionsToPercentage(entitiesToLayout, relationships, layoutBounds, false /* do not update size */);

      // Resize and shift nodes
      resizeAndShiftNodes(entitiesToLayout);
    }

    // Recalculate layout, allowing for the node width, which we now know
    layoutBounds = getLayoutBounds(entitiesToLayout, true);

    // adjust node positions again, to the new coordinate system (still as a percentage)
    convertPositionsToPercentage(entitiesToLayout, relationships, layoutBounds, true /* update node size */);

    DisplayIndependentRectangle screenBounds = calcScreenBounds(realBounds, layoutBounds);

    // Now convert to real screen coordinates
    convertPositionsToCoords(entitiesToLayout, relationships, screenBounds);
  }

  /**
   * Convert all node positions into a percentage of the screen. If includeNodeSize is true then this also updates the
   * node's internal size.
   * 
   * @param entitiesToLayout
   */
  private void convertPositionsToPercentage(InternalNode[] entitiesToLayout, InternalRelationship[] relationships,
      DisplayIndependentRectangle layoutBounds, boolean includeNodeSize)
  {

    // Adjust node positions and sizes
    for (int i = 0; i < entitiesToLayout.length; i++)
    {
      InternalNode node = entitiesToLayout[i];
      DisplayIndependentPoint location = node.getInternalLocation().convertToPercent(layoutBounds);
      node.setInternalLocation(location.x, location.y);
      if (includeNodeSize)
      { // adjust node sizes
        double width = node.getInternalWidth() / layoutBounds.width;
        double height = node.getInternalHeight() / layoutBounds.height;
        node.setInternalSize(width, height);
      }
    }

    // Adjust bendpoint positions
    for (int i = 0; i < relationships.length; i++)
    {
      InternalRelationship rel = relationships[i];
      for (int j = 0; j < rel.getBendPoints().size(); j++)
      {
        BendPoint bp = (BendPoint)rel.getBendPoints().get(j);
        DisplayIndependentPoint toPercent = bp.convertToPercent(layoutBounds);
        bp.setX(toPercent.x);
        bp.setY(toPercent.y);
      }
    }
  }

  /**
   * Convert the positions from a percentage of bounds area to fixed coordinates. NOTE: ALL OF THE POSITIONS OF NODES
   * UNTIL NOW WERE FOR THE CENTER OF THE NODE - Convert it to the left top corner.
   * 
   * @param entitiesToLayout
   * @param relationships
   * @param realBounds
   */
  private void convertPositionsToCoords(InternalNode[] entitiesToLayout, InternalRelationship[] relationships,
      DisplayIndependentRectangle screenBounds)
  {

    // Adjust node positions and sizes
    for (int i = 0; i < entitiesToLayout.length; i++)
    {
      InternalNode node = entitiesToLayout[i];
      double width = node.getInternalWidth() * screenBounds.width;
      double height = node.getInternalHeight() * screenBounds.height;
      DisplayIndependentPoint location = node.getInternalLocation().convertFromPercent(screenBounds);
      node.setInternalLocation(location.x - width / 2, location.y - height / 2);
      if (resizeEntitiesAfterLayout)
      {
        adjustNodeSizeAndPos(node, height, width);
      }
      else
      {
        node.setInternalSize(width, height);
      }
    }

    // Adjust bendpoint positions and shift based on source node size
    for (int i = 0; i < relationships.length; i++)
    {
      InternalRelationship rel = relationships[i];
      for (int j = 0; j < rel.getBendPoints().size(); j++)
      {
        BendPoint bp = (BendPoint)rel.getBendPoints().get(j);
        DisplayIndependentPoint fromPercent = bp.convertFromPercent(screenBounds);
        bp.setX(fromPercent.x);
        bp.setY(fromPercent.y);
      }
    }
  }

  /**
   * Adjust node size to take advantage of space. Reset position to top left corner of node.
   * 
   * @param node
   * @param height
   * @param width
   */
  private void adjustNodeSizeAndPos(InternalNode node, double height, double width)
  {
    double widthUsingHeight = height * widthToHeightRatio;
    if (widthToHeightRatio <= 1.0 && widthUsingHeight <= width)
    {
      double widthToUse = height * widthToHeightRatio;
      double leftOut = width - widthToUse;
      node.setInternalSize(Math.max(height * widthToHeightRatio, MIN_ENTITY_SIZE), Math.max(height, MIN_ENTITY_SIZE));
      node.setInternalLocation(node.getInternalX() + leftOut / 2, node.getInternalY());

    }
    else
    {
      double heightToUse = height / widthToHeightRatio;
      double leftOut = height - heightToUse;

      node.setInternalSize(Math.max(width, MIN_ENTITY_SIZE), Math.max(width / widthToHeightRatio, MIN_ENTITY_SIZE));
      node.setInternalLocation(node.getInternalX(), node.getInternalY() + leftOut / 2);
    }
  }

  /**
   * Find and set the node size - shift the nodes to the right and down to make room for the width and height.
   * 
   * @param entitiesToLayout
   * @param relationships
   */
  private void resizeAndShiftNodes(InternalNode[] entitiesToLayout)
  {
    // get maximum node size as percent of screen dimmensions
    double nodeSize = getNodeSize(entitiesToLayout);
    double halfNodeSize = nodeSize / 2;

    // Resize and shift nodes
    for (int i = 0; i < entitiesToLayout.length; i++)
    {
      InternalNode node = entitiesToLayout[i];
      node.setInternalSize(nodeSize, nodeSize);
      node.setInternalLocation(node.getInternalX() + halfNodeSize, node.getInternalY() + halfNodeSize);
    }
  }

  /**
   * Returns the maximum possible node size as a percentage of the width or height in current coord system.
   */
  private double getNodeSize(InternalNode[] entitiesToLayout)
  {
    double width, height;
    if (entitiesToLayout.length == 1)
    {
      width = 0.8;
      height = 0.8;
    }
    else
    {
      DisplayIndependentDimension minimumDistance = getMinimumDistance(entitiesToLayout);
      width = 0.8 * minimumDistance.width;
      height = 0.8 * minimumDistance.height;
    }
    return Math.max(width, height);
  }

  /**
   * minDistance is the closest that any two points are together. These two points become the center points for the two
   * closest nodes, which we wish to make them as big as possible without overlapping. This will be the maximum of
   * minDistanceX and minDistanceY minus a bit, lets say 20% We make the recommended node size a square for convenience.
   * _______ | | | | | + | | |\ | |___|_\_|_____ | | \ | | | \ | +-|---+ | | | |_______|
   */
  private DisplayIndependentDimension getMinimumDistance(InternalNode[] entitiesToLayout)
  {
    DisplayIndependentDimension horAndVertdistance = new DisplayIndependentDimension(Double.MAX_VALUE, Double.MAX_VALUE);
    double minDistance = Double.MAX_VALUE; // the minimum distance between all the nodes
    // TODO: Very Slow!
    for (int i = 0; i < entitiesToLayout.length; i++)
    {
      InternalNode layoutEntity1 = entitiesToLayout[i];
      double x1 = layoutEntity1.getInternalX();
      double y1 = layoutEntity1.getInternalY();
      for (int j = i + 1; j < entitiesToLayout.length; j++)
      {
        InternalNode layoutEntity2 = entitiesToLayout[j];
        double x2 = layoutEntity2.getInternalX();
        double y2 = layoutEntity2.getInternalY();
        double distanceX = Math.abs(x1 - x2);
        double distanceY = Math.abs(y1 - y2);
        double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        if (distance < minDistance)
        {
          minDistance = distance;
          horAndVertdistance.width = distanceX;
          horAndVertdistance.height = distanceY;
        }
      }
    }
    return horAndVertdistance;
  }

  /**
   * Calculate the screen bounds, maintaining the
   * 
   * @param realBounds
   * @return
   */
  private DisplayIndependentRectangle calcScreenBounds(DisplayIndependentRectangle realBounds,
      DisplayIndependentRectangle layoutBounds)
  {
    if (resizeEntitiesAfterLayout)
    { // OK to alter aspect ratio
      double borderWidth = Math.min(realBounds.width, realBounds.height) / 10.0; // use 10% for the border - 5% on each
      // side
      return new DisplayIndependentRectangle(realBounds.x + borderWidth / 2.0, realBounds.y + borderWidth / 2.0,
          realBounds.width - borderWidth, realBounds.height - borderWidth);
    }
    else
    { // retain layout aspect ratio
      double heightAdjustment = realBounds.height / layoutBounds.height;
      double widthAdjustment = realBounds.width / layoutBounds.width;
      double ratio = Math.min(heightAdjustment, widthAdjustment);
      double adjustedHeight = layoutBounds.height * ratio;
      double adjustedWidth = layoutBounds.width * ratio;
      double adjustedX = realBounds.x + (realBounds.width - adjustedWidth) / 2.0;
      double adjustedY = realBounds.y + (realBounds.height - adjustedHeight) / 2.0;
      double borderWidth = Math.min(adjustedWidth, adjustedHeight) / 10.0; // use 10% for the border - 5% on each side
      return new DisplayIndependentRectangle(adjustedX + borderWidth / 2.0, adjustedY + borderWidth / 2.0,
          adjustedWidth - borderWidth, adjustedHeight - borderWidth);
    }
  }
}
