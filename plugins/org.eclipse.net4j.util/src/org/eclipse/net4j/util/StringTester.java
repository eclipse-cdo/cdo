/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.SingletonFactory;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface StringTester extends BiPredicate<String, String>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.stringTesters";

  public static final StringTester EQ = (s1, s2) -> s1.equals(s2);

  public static final StringTester EQ_CI = EQ.caseInsensitive();

  public static final StringTester EQ_INT = asInt((v1, v2) -> v1 == v2);

  public static final StringTester EQ_FLOAT = asFloat((v1, v2) -> v1 == v2);

  public static final StringTester NEQ = EQ.negate();

  public static final StringTester NEQ_CI = NEQ.caseInsensitive();

  public static final StringTester NEQ_INT = asInt((v1, v2) -> v1 != v2);

  public static final StringTester NEQ_FLOAT = asFloat((v1, v2) -> v1 != v2);

  public static final StringTester LT = (s1, s2) -> StringUtil.compare(s1, s2) < 0;

  public static final StringTester LT_CI = LT.caseInsensitive();

  public static final StringTester LT_INT = asInt((v1, v2) -> v1 < v2);

  public static final StringTester LT_FLOAT = asFloat((v1, v2) -> v1 < v2);

  public static final StringTester LTE = (s1, s2) -> StringUtil.compare(s1, s2) <= 0;

  public static final StringTester LTE_CI = LTE.caseInsensitive();

  public static final StringTester LTE_INT = asInt((v1, v2) -> v1 <= v2);

  public static final StringTester LTE_FLOAT = asFloat((v1, v2) -> v1 <= v2);

  public static final StringTester GT = (s1, s2) -> StringUtil.compare(s1, s2) > 0;

  public static final StringTester GT_CI = GT.caseInsensitive();

  public static final StringTester GT_INT = asInt((v1, v2) -> v1 > v2);

  public static final StringTester GT_FLOAT = asFloat((v1, v2) -> v1 > v2);

  public static final StringTester GTE = (s1, s2) -> StringUtil.compare(s1, s2) >= 0;

  public static final StringTester GTE_CI = GTE.caseInsensitive();

  public static final StringTester GTE_INT = asInt((v1, v2) -> v1 >= v2);

  public static final StringTester GTE_FLOAT = asFloat((v1, v2) -> v1 >= v2);

  public static final StringTester CONTAINS = (s1, s2) -> s1.contains(s2);

  public static final StringTester CONTAINS_CI = CONTAINS.caseInsensitive();

  public static final StringTester REGEX = (s1, s2) -> Pattern.compile(s2).matcher(s1).matches();

  public static final StringTester REGEX_CI = (s1, s2) -> Pattern.compile(s2, Pattern.CASE_INSENSITIVE).matcher(s1).matches();

  public static final StringTester REGEX_FIND = (s1, s2) -> Pattern.compile(s2).matcher(s1).find();

  public static final StringTester REGEX_FIND_CI = (s1, s2) -> Pattern.compile(s2, Pattern.CASE_INSENSITIVE).matcher(s1).find();

  public static final StringTester GLOB = (s1, s2) -> StringUtil.glob(s2, s1);

  public static final StringTester GLOB_CI = GLOB.caseInsensitive();

  @Override
  public boolean test(String s1, String s2);

  @Override
  public default StringTester negate()
  {
    return (String s1, String s2) -> !test(s1, s2);
  }

  @Override
  public default StringTester and(BiPredicate<? super String, ? super String> other)
  {
    Objects.requireNonNull(other);
    return (String s1, String s2) -> test(s1, s2) && other.test(s1, s2);
  }

  @Override
  public default StringTester or(BiPredicate<? super String, ? super String> other)
  {
    Objects.requireNonNull(other);
    return (String s1, String s2) -> test(s1, s2) || other.test(s1, s2);
  }

  public default StringTester safe()
  {
    return (String s1, String s2) -> s1 == null || s2 == null ? false : test(s1, s2);
  }

  public default StringTester caseInsensitive()
  {
    return (String s1, String s2) -> test(s1.toLowerCase(), s2.toLowerCase());
  }

  public static StringTester safe(StringTester other)
  {
    Objects.requireNonNull(other);
    return other.safe();
  }

  public static StringTester caseInsensitive(StringTester other)
  {
    Objects.requireNonNull(other);
    return other.caseInsensitive();
  }

  public static StringTester asInt(LongBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (String s1, String s2) -> {
      long v1 = Long.parseLong(s1);
      long v2 = Long.parseLong(s2);
      return other.test(v1, v2);
    };
  }

  public static StringTester asFloat(DoubleBiPredicate other)
  {
    Objects.requireNonNull(other);
    return (String s1, String s2) -> {
      double v1 = Double.parseDouble(s1);
      double v2 = Double.parseDouble(s2);
      return other.test(v1, v2);
    };
  }

  /**
   * @author Eike Stepper
   */
  public static final class MetaFactory extends org.eclipse.net4j.util.factory.MetaFactory
  {
    private static final String PG = StringTester.PRODUCT_GROUP;

    private static final IFactory[] CHILDREN = { //
        new SingletonFactory(PG, "eq", EQ), //
        new SingletonFactory(PG, "eq_ci", EQ_CI), //
        new SingletonFactory(PG, "eq_int", EQ_INT), //
        new SingletonFactory(PG, "eq_float", EQ_FLOAT), //

        new SingletonFactory(PG, "neq", NEQ), //
        new SingletonFactory(PG, "neq_ci", NEQ_CI), //
        new SingletonFactory(PG, "neq_int", NEQ_INT), //
        new SingletonFactory(PG, "neq_float", NEQ_FLOAT), //

        new SingletonFactory(PG, "lt", LT), //
        new SingletonFactory(PG, "lt_ci", LT_CI), //
        new SingletonFactory(PG, "lt_int", LT_INT), //
        new SingletonFactory(PG, "lt_float", LT_FLOAT), //

        new SingletonFactory(PG, "lte", LTE), //
        new SingletonFactory(PG, "lte_ci", LTE_CI), //
        new SingletonFactory(PG, "lte_int", LTE_INT), //
        new SingletonFactory(PG, "lte_float", LTE_FLOAT), //

        new SingletonFactory(PG, "gt", GT), //
        new SingletonFactory(PG, "gt_ci", GT_CI), //
        new SingletonFactory(PG, "gt_int", GT_INT), //
        new SingletonFactory(PG, "gt_float", GT_FLOAT), //

        new SingletonFactory(PG, "gte", GTE), //
        new SingletonFactory(PG, "gte_ci", GTE_CI), //
        new SingletonFactory(PG, "gte_int", GTE_INT), //
        new SingletonFactory(PG, "gte_float", GTE_FLOAT), //

        new SingletonFactory(PG, "contains", CONTAINS), //
        new SingletonFactory(PG, "contains_ci", CONTAINS_CI), //

        new SingletonFactory(PG, "regex", REGEX), //
        new SingletonFactory(PG, "regex_ci", REGEX_CI), //
        new SingletonFactory(PG, "regex_find", REGEX_FIND), //
        new SingletonFactory(PG, "regex_find_ci", REGEX_FIND_CI), //

        new SingletonFactory(PG, "glob", GLOB), //
        new SingletonFactory(PG, "glob_ci", GLOB_CI), //
    };

    public MetaFactory()
    {
      super(PG);
    }

    @Override
    public IFactory[] create(String description) throws ProductCreationException
    {
      return CHILDREN;
    }
  }
}
