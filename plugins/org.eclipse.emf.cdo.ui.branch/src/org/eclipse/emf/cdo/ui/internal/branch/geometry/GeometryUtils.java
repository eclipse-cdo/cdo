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
   */
  public static void union(DisplayIndependentDimension thisDimension, DisplayIndependentDimension thatDimension)
  {
    DisplayIndependentDimension union = new DisplayIndependentDimension(thisDimension);
    union.width += thatDimension.width;
    union.height += thatDimension.height;
  }

  /**
   * Moves the given Rectangle horizontally by dx and vertically by dy and returns a new translated rectangle instance.
   * 
   * @param xOffset
   *          the offset on the x axis to move the rectangle
   * @param yOffset
   *          the offset on the y axis to move the rectangle
   * @param rectangle
   *          the rectangle to translate
   * @return a new translated rectangle instance
   */
  public static DisplayIndependentRectangle translateRectangle(double xOffset, double yOffset,
      DisplayIndependentRectangle rectangle)
  {
    DisplayIndependentRectangle newRectangle = new DisplayIndependentRectangle(rectangle);
    newRectangle.x += xOffset;
    newRectangle.y += yOffset;
    return newRectangle;
  }

  /**
   * Returns the dimension needed to translate the given rectangle to the given location .
   * 
   * @param rectangleToTranslate
   *          the rectangle to translate
   * @param x
   *          the x coordinate to translate the rectangle to
   * @param y
   *          the y coordinate to translate the rectangle to
   * @return the translation
   */
  public static DisplayIndependentDimension getTranslation(DisplayIndependentRectangle rectangleToTranslate, double x,
      double y)
  {
    return new DisplayIndependentDimension(x - rectangleToTranslate.x, y - rectangleToTranslate.y);
  }

  /**
   * Gets the translation necessary to move the source coordinate to the target coordinate.
   * 
   * @param sourceCoordinate
   *          the source x
   * @param targetCoordinate
   *          the target x
   * @return the translation
   */
  public static double getTranslation(double sourceCoordinate, double targetCoordinate)
  {
    return targetCoordinate - sourceCoordinate;
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
   */
  public static DisplayIndependentRectangle union(DisplayIndependentRectangle rectangle, double x, double y,
      double width, double height)
  {
    DisplayIndependentRectangle bounds = new DisplayIndependentRectangle();
    double right = Math.max(rectangle.x + rectangle.width, x + width);
    double bottom = Math.max(rectangle.y + rectangle.height, y + height);
    bounds.x = Math.min(rectangle.x, x);
    bounds.y = Math.min(rectangle.y, y);
    bounds.width = right - bounds.x;
    bounds.height = bottom - bounds.y;
    return bounds;
  }

  /**
   * Expands the given first rectangle to the minimum size which can hold both this Rectangle and the second rectangle.
   * 
   * @param thisRectangle
   *          the rectangle to expand
   * @param thatRectangle
   *          the rectangle to include in the first one
   * @return the new bounds
   */
  public static DisplayIndependentRectangle union(DisplayIndependentRectangle thisRectangle,
      DisplayIndependentRectangle thatRectangle)
  {
    return union(thisRectangle, thatRectangle.x, thatRectangle.y, thatRectangle.width, thatRectangle.height);
  }

  /**
   * Scales the given rectangle by the given factors.
   * 
   * @param width
   *          the width
   * @param height
   *          the height
   * @param bounds
   *          the bounds
   */
  public static void scaleRectangle(double width, double height, DisplayIndependentRectangle bounds)
  {
    bounds.width *= width;
    bounds.height *= height;
  }

  /**
   * Subtracts the given height and width from the given rectangle and move its origin by the half of the given values.
   * 
   * @param dimension
   *          the dimension
   * @param rectangle
   *          the rectangle
   */
  public static DisplayIndependentRectangle substractBorder(DisplayIndependentDimension dimension,
      DisplayIndependentRectangle rectangle)
  {
    DisplayIndependentRectangle newRectangle = new DisplayIndependentRectangle();
    newRectangle.x = rectangle.x + dimension.width / 2;
    newRectangle.y = rectangle.y + dimension.height / 2;
    newRectangle.width = rectangle.width - dimension.width;
    newRectangle.height = rectangle.height - dimension.height;
    return newRectangle;
  }

  /**
   * Centers the given rectangle on the bounds of the given other rectangle.
   * 
   * @param rectangleToCenter
   *          the rectangle to center
   * @param rectangle
   *          the rectangle
   */
  public static void center(DisplayIndependentRectangle rectangleToCenter, DisplayIndependentRectangle rectangle)
  {
    if (rectangle == null)
    {
      rectangleToCenter.x -= rectangleToCenter.width / 2;
      rectangleToCenter.y -= rectangleToCenter.height / 2;
    }
    else
    {
      rectangleToCenter.x = rectangle.x + (rectangleToCenter.width - rectangle.width) / 2;
      rectangleToCenter.y = rectangle.y + (rectangleToCenter.height - rectangle.height) / 2;
    }
  }
}
