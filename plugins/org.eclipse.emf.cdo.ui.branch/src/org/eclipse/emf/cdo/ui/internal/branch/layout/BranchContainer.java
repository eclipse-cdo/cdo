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

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class BranchTreeContainer.
 */
public class BranchContainer
{

  /** The bounds of this container. */
  private DisplayIndependentRectangle bounds = new DisplayIndependentRectangle();

  /** The node list. */
  private List<InternalNode> nodeList = new ArrayList<InternalNode>();

  /**
   * Instantiates a new branch tree container with a new instance of a {@link DisplayIndependentRectangle}.
   */
  public BranchContainer()
  {
    this(new DisplayIndependentRectangle());
  }

  /**
   * Instantiates a new branch tree container with the given instance of a {@link DisplayIndependentRectangle}.
   * 
   * @param bounds
   *          the bounds
   */
  public BranchContainer(DisplayIndependentRectangle bounds)
  {
    this.bounds.x = bounds.x;
    this.bounds.y = bounds.y;
    this.bounds.width = bounds.width;
    this.bounds.height = bounds.height;
  }

  /**
   * Unifies the bounds of this container with the bounds of the given node.
   * 
   * @param node
   *          the node
   */
  public void union(InternalNode node)
  {
    nodeList.add(node);
    bounds = GeometryUtils.union(bounds, node.getInternalX(), node.getInternalY(), node.getInternalWidth(), node
        .getInternalHeight());
  }
}
