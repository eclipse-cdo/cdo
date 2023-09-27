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
import java.util.function.Function;

/**
 * @author Eike Stepper
 * @since 3.23
 */
@FunctionalInterface
public interface StringConverter extends Function<String, String>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.stringConverters";

  public static final StringConverter IDENTITY = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return str;
    }
  };

  public static final StringConverter SAFE = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.safe(str);
    }
  };

  public static final StringConverter UPPER_CASE = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return str == null ? null : str.toUpperCase();
    }
  };

  public static final StringConverter LOWER_CASE = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return str == null ? null : str.toLowerCase();
    }
  };

  public static final StringConverter CAP = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.cap(str);
    }
  };

  public static final StringConverter CAP_ALL = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.capAll(str);
    }
  };

  public static final StringConverter UNCAP = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.uncap(str);
    }
  };

  public static final StringConverter UNCAP_ALL = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.uncapAll(str);
    }
  };

  public static final StringConverter ESCAPE = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.escape(str);
    }
  };

  public static final StringConverter UNESCAPE = new StringConverter()
  {
    @Override
    public String apply(String str)
    {
      return StringUtil.unescape(str);
    }
  };

  @Override
  public String apply(String str);

  public default StringConverter compose(StringConverter before)
  {
    Objects.requireNonNull(before);
    return (str) -> apply(before.apply(str));
  }

  public default StringConverter andThen(StringConverter after)
  {
    Objects.requireNonNull(after);
    return (str) -> after.apply(apply(str));
  }

  /**
   * @author Eike Stepper
   */
  public static final class MetaFactory extends org.eclipse.net4j.util.factory.MetaFactory
  {
    private static final IFactory[] CHILDREN = { //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "identity", IDENTITY), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "safe", SAFE), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "upper_case", UPPER_CASE), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "lower_case", LOWER_CASE), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "cap", CAP), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "cap_all", CAP_ALL), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "uncap", UNCAP), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "uncap_all", UNCAP_ALL), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "escape", ESCAPE), //
        new SingletonFactory(StringConverter.PRODUCT_GROUP, "unescape", UNESCAPE), //
    };

    public MetaFactory()
    {
      super(PRODUCT_GROUP);
    }

    @Override
    public IFactory[] create(String description) throws ProductCreationException
    {
      return CHILDREN;
    }
  }
}
