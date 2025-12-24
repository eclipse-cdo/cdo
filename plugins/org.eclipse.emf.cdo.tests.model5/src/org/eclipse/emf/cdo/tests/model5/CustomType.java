/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5;

/**
 * @author Eike Stepper
 */
public final class CustomType
{
  private static final String PREFIX = "CustomType:";

  private final int x;

  public CustomType(int x)
  {
    this.x = x;
  }

  public int getX()
  {
    return x;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    CustomType other = (CustomType)obj;
    if (x != other.x)
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    return Integer.toString(x);
  }

  public static CustomType createFromString(String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    if (!initialValue.startsWith(PREFIX))
    {
      throw new IllegalArgumentException();
    }

    int x = Integer.parseInt(initialValue.substring(PREFIX.length()));
    return new CustomType(x);
  }

  public static String convertToString(Object instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }

    return PREFIX + ((CustomType)instanceValue).getX();
  }
}
