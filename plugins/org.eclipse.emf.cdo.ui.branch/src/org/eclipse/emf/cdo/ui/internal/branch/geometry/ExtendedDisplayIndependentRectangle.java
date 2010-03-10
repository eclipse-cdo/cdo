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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

/**
 * A rectangle that does not depend on any display toolkit (swt, awt, etc.). It furthermore implements transitions and
 * unions.
 * 
 * @see DisplayIndependentRectangle
 * @see Rectangle
 */
public class ExtendedDisplayIndependentRectangle extends DisplayIndependentRectangle
{
  public ExtendedDisplayIndependentRectangle()
  {
    this(0, 0, 0, 0);
  }

  public ExtendedDisplayIndependentRectangle(DisplayIndependentRectangle rectangle)
  {
    super(rectangle);
  }

  public ExtendedDisplayIndependentRectangle(double x, double y, double width, double height)
  {
    super(x, y, width, height);
  }

  /**
   * Updates this Rectangle's dimensions to the minimum size which can hold both this Rectangle and the rectangle (x, y,
   * w, h).
   * 
   * @param x
   *          X coordiante of desired union.
   * @param y
   *          Y coordiante of desired union.
   * @param w
   *          Width of desired union.
   * @param h
   *          Height of desired union.
   * @return <code>this</code> for convenience
   */
  public ExtendedDisplayIndependentRectangle union(double x, double y, double width, double height)
  {
    double right = Math.max(this.x + this.width, x + width);
    double bottom = Math.max(this.y + this.height, y + height);
    this.x = Math.min(this.x, x);
    this.y = Math.min(this.y, y);
    this.width = right - this.x;
    this.height = bottom - this.y;
    return this;
  }

  /**
   * Updates this Rectangle's dimensions to the minimum size which can hold both this Rectangle and the rectangle (x, y,
   * w, h).
   * 
   * @param x
   *          X coordiante of desired union.
   * @param y
   *          Y coordiante of desired union.
   * @param w
   *          Width of desired union.
   * @param h
   *          Height of desired union.
   * @return <code>this</code> for convenience
   */
  public ExtendedDisplayIndependentRectangle union(DisplayIndependentRectangle rectangle)
  {
    return union(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }

  /**
   * Moves this Rectangle horizontally by dx and vertically by dy, then returns this Rectangle for convenience.
   * 
   * @param dx
   *          Shift along X axis
   * @param dy
   *          Shift along Y axis
   * @return <code>this</code> for convenience
   */
  public ExtendedDisplayIndependentRectangle translate(double dx, double dy)
  {
    x += dx;
    y += dy;
    return this;
  }

  /**
   * Answers whether the bottom of this rectangle ends before the given rectangle's top starts (y coordinate).
   * 
   * @param rectangle
   *          the rectangle
   * @return true, if successful
   */
  public boolean bottomEndsBefore(DisplayIndependentRectangle rectangle)
  {
    return (y + height) < rectangle.y;
  }

  public Rectangle toRectangle()
  {
    return new Rectangle((int)x, (int)y, (int)width, (int)height);
  }

  /**
   * Answers whether the right side of this rectangle ends before the given rectangle starts (x coordinate).
   * 
   * @param rectangle
   *          the rectangle to check against
   * @return true, if successful
   */
  public boolean endsBefore(ExtendedDisplayIndependentRectangle rectangle)
  {
    return rectangle.x > x;
  }
}
