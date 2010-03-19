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

/**
 * A strategy that layouts a branch. A branch centers its (sibling) nodes below each other while using their time stamp
 * to set the y coordinate. Sub-Branches are displaced to the right or to the left (left and right sub branches are
 * distributed equally).
 * <p>
 * The current implementation may only layout vertically.
 * 
 * @author Andre Dietisheim
 */
public class HorizontallyAlternatingSubBranches extends AbstractVerticalLayoutStrategy
{
  /**
   * Returns the strategy that layouts the next branch view. Starts with right, second call returns left, etc.
   * 
   * @return the current sub branch strategy
   * @see #LEFT
   * @see #RIGHT
   */
  @Override
  protected SubBranchViewTranslation getSubBranchTranslationStrategy(SubBranchViewTranslation currentTranslationStrategy)
  {
    if (currentTranslationStrategy == null || currentTranslationStrategy == LEFT)
    {
      return RIGHT;
    }

    return LEFT;
  }
}
