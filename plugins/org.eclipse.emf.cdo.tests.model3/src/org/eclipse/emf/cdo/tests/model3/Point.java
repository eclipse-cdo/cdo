/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

/**
 * @author Eike Stepper
 */
public final class Point
{
  private int x;

  private int y;

  public Point(int x, int y)
  {
    this.x = x;
    this.y = y;
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  @Override
  public int hashCode()
  {
    return x ^ y;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof Point)
    {
      Point that = (Point)obj;
      return x == that.getX() && y == that.getY();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return Integer.toString(x) + "," + Integer.toString(y);
  }

  public static Point parse(String str)
  {
    String[] coordinates = str.split(",");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    return new Point(x, y);
  }
}
