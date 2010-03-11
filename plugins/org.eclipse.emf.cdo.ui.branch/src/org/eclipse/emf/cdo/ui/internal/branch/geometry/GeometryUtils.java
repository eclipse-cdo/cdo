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
package org.eclipse.emf.cdo.ui.internal.branch.geometry;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;

public class GeometryUtils
{
  /**
   * Unifies both given dimensions.
   * 
   * @param thisDimension
   *          the this dimension
   * @param thatDimension
   *          the that dimension
   * @return the display independent dimension
   */
  public static DisplayIndependentDimension union(DisplayIndependentDimension thisDimension,
      DisplayIndependentDimension thatDimension)
  {
    DisplayIndependentDimension union = new DisplayIndependentDimension(thisDimension);
    union.width += thatDimension.width;
    union.height += thatDimension.height;
    return union;
  }
}
