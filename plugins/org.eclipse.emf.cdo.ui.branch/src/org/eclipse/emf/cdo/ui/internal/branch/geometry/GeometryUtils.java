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
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

/**
 * Holds various utiliy method that help to deal with gemoetry classes in zest .
 */
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

  /**
   * Moves the given Rectangle horizontally by dx and vertically by dy, then returns this Rectangle for convenience.
   * 
   * @param xOffset
   *          the offset on the x axis to move the rectangle
   * @param yOffset
   *          the offset on the y axis to move the rectangle
   * @param rectangle
   *          the rectangle to translate
   */
  public static void translateRectangle(double xOffset, double yOffset, DisplayIndependentRectangle rectangle)
  {
    rectangle.x += xOffset;
    rectangle.y += yOffset;
  }

  /**
   * Answers whether the bottom of the first rectangle ends before the second rectangle's top starts (y coordinate).
   * 
   * @param theseBounds
   *          the first rectangle that shall end before the first one
   * @param thoseBounds
   *          the second rectangle that shall start after the first one ends
   * @return true, if successful
   */
  public static boolean bottomEndsBefore(DisplayIndependentRectangle theseBounds,
      DisplayIndependentRectangle thoseBounds)
  {
    return theseBounds.y + theseBounds.height < thoseBounds.y;
  }

  /**
   * Expands the given rectangle to to the minimum size which can hold both this Rectangle and the rectangle (x, y, w,
   * h).
   * 
   * @param x
   *          X coordinate to expand the rectangle to.
   * @param y
   *          Y coordinate to expand the rectangle to.
   * @param rectangle
   *          the rectangle to expand
   * @param width
   *          the width (starting at the x coordinate) to expand the rectangle to
   * @param height
   *          the height (starting at the y coordinate) to expand the rectangle to
   * @return <code>this</code> for convenience
   */
  public static DisplayIndependentRectangle union(DisplayIndependentRectangle rectangle, double x, double y,
      double width, double height)
  {
    double right = Math.max(rectangle.x + rectangle.width, x + width);
    double bottom = Math.max(rectangle.y + rectangle.height, y + height);
    rectangle.x = Math.min(rectangle.x, x);
    rectangle.y = Math.min(rectangle.y, y);
    rectangle.width = right - rectangle.x;
    rectangle.height = bottom - rectangle.y;
    return rectangle;
  }

  /**
   * Expands the given first rectangle to the minimum size which can hold both this Rectangle and the second rectangle
   * 
   * @param thisRectangle
   *          the rectangle to expand
   * @param thatRectangle
   *          the rectangle to include in the first one
   * @return the display independent rectangle
   */
  public static DisplayIndependentRectangle union(DisplayIndependentRectangle thisRectangle,
      DisplayIndependentRectangle thatRectangle)
  {
    return union(thisRectangle, thatRectangle);
  }
}
