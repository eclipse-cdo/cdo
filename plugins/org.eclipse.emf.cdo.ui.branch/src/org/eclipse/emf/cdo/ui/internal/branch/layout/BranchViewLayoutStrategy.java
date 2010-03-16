/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.branch.layout;

import org.eclipse.emf.cdo.ui.internal.branch.item.AbstractBranchPointNode;
import org.eclipse.emf.cdo.ui.internal.branch.item.BranchPointNode;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

/**
 * @author Eike Stepper
 */
public interface BranchViewLayoutStrategy
{

  public abstract void layoutBaselineNode(BranchView branchView, AbstractBranchPointNode node);

  /**
   * Layout the given node as sibling node to the baseline node (and its siblings).
   * 
   * @param branchView
   *          the branch view to layout the node to
   * @param node
   *          the node to layout
   */
  public abstract void layoutNode(BranchView branchView, AbstractBranchPointNode node,
      AbstractBranchPointNode previousNode);

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
  public abstract void layoutSubBranchView(BranchView branchView, BranchView subBranchView,
      BranchPointNode branchPointNode);

  /**
   * Translates the given branch view by the given dimension.
   * 
   * @param offsets
   *          the dimension the x- and y-offset
   * @param branchView
   *          the branch view to translate
   */
  public void translateBy(BranchView branchView, DisplayIndependentDimension offsets);

  /**
   * Scales the given branch view by the given factor on the x- and on the y-axis and translates it to the given x and y
   * coordinates.
   * 
   * @param branchView
   *          the branch view
   * @param targetBounds
   *          the target bounds
   */
  public void scale(BranchView branchView, DisplayIndependentRectangle targetBounds);

  /**
   * Scales the given branch view by the given factor on the x- and on the y-axis.
   * 
   * @param branchView
   *          the branch view to scale
   * @param scaling
   *          the scaling
   */
  public void scale(BranchView branchView, DisplayIndependentDimension scaling);

}
