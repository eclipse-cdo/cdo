/*
 * Copyright (c) 2013, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public final class Predicates
{
  private Predicates()
  {
  }

  /**
   * @since 3.4
   */
  public static <T> Predicate<T> included(Set<T> inclusions)
  {
    return new IncludedPredicate<T>(inclusions);
  }

  /**
   * @since 3.4
   */
  public static <T> Predicate<T> excluded(Set<T> exclusions)
  {
    return new ExcludedPredicate<T>(exclusions);
  }

  /**
   * @since 3.4
   */
  public static <T> Predicate<T> unique()
  {
    return new UniquePredicate<T>();
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysFalse()
  {
    return (Predicate<T>)FalsePredicate.INSTANCE;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> alwaysTrue()
  {
    return (Predicate<T>)TruePredicate.INSTANCE;
  }

  /**
   * @author Eike Stepper
   * @since 3.4
   */
  public static final class IncludedPredicate<T> implements Predicate<T>
  {
    private final Set<T> inclusions;

    public IncludedPredicate(Set<T> inclusions)
    {
      this.inclusions = inclusions;
    }

    @Override
    public boolean apply(T element)
    {
      return inclusions.contains(element);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.4
   */
  public static final class ExcludedPredicate<T> implements Predicate<T>
  {
    private final Set<T> exclusions;

    public ExcludedPredicate(Set<T> exclusions)
    {
      this.exclusions = exclusions;
    }

    @Override
    public boolean apply(T element)
    {
      return !exclusions.contains(element);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.4
   */
  public static final class UniquePredicate<T> implements Predicate<T>
  {
    private final Set<T> applied = new HashSet<T>();

    @Override
    public boolean apply(T element)
    {
      return applied.add(element);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FalsePredicate implements Predicate<Object>
  {
    private static final Predicate<Object> INSTANCE = new FalsePredicate();

    @Override
    public boolean apply(Object element)
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TruePredicate implements Predicate<Object>
  {
    private static final Predicate<Object> INSTANCE = new TruePredicate();

    @Override
    public boolean apply(Object element)
    {
      return true;
    }
  }
}
