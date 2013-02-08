/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public final class Predicates
{
  private static final Predicate<Object> ALWAYS_FALSE = new Predicate<Object>()
  {
    public boolean apply(Object element)
    {
      return false;
    }
  };

  private static final Predicate<Object> ALWAYS_TRUE = new Predicate<Object>()
  {
    public boolean apply(Object element)
    {
      return true;
    }
  };

  private Predicates()
  {
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysFalse()
  {
    return (Predicate<T>)ALWAYS_FALSE;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysTrue()
  {
    return (Predicate<T>)ALWAYS_TRUE;
  }
}
