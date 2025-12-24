/*
 * Copyright (c) 2011-2013, 2015, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418452
 */
package org.eclipse.net4j.util.properties;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Describes a property of a receiver object and extracts its value.
 *
 * @author Eike Stepper
 * @since 3.2
 */
public abstract class Property<RECEIVER>
{
  private final String name;

  private final String label;

  private final String description;

  private final String category;

  public Property(String name, String label, String description, String category)
  {
    this.name = name;
    this.label = label;
    this.description = description;
    this.category = category;
  }

  public Property(String name, String label, String description)
  {
    this(name, label, description, null);
  }

  public Property(String name)
  {
    this(name, null, null);
  }

  public final String getName()
  {
    return name;
  }

  public final String getLabel()
  {
    return label;
  }

  public final String getDescription()
  {
    return description;
  }

  public final String getCategory()
  {
    return category;
  }

  public final Object getValue(RECEIVER receiver)
  {
    try
    {
      Object value = eval(receiver);
      return convertValue(value);
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (Throwable ex)
    {
      if (LifecycleUtil.isActive(receiver))
      {
        OM.LOG.error(ex);
      }
    }

    return null;
  }

  public boolean testValue(RECEIVER receiver, Object[] args, Object expectedValue)
  {
    Object value = getValue(receiver);
    return testValue(value, expectedValue);
  }

  boolean testValue(Object value, Object expectedValue)
  {
    if (value instanceof Boolean && expectedValue == null)
    {
      // Per the Expressions extension point specification, the 'value' attribute
      // is optional for boolean-valued properties, in which case its default is true.
      expectedValue = Boolean.TRUE;
    }

    return ObjectUtil.equals(value, expectedValue);
  }

  Object convertValue(Object value)
  {
    try
    {
      if (value == null)
      {
        return null;
      }

      Class<? extends Object> c = value.getClass();
      if (c == Boolean.class)
      {
        return value;
      }

      if (c == Boolean.class)
      {
        return value;
      }

      if (c == Character.class)
      {
        return value;
      }

      if (c == Byte.class)
      {
        return value;
      }

      if (c == Short.class)
      {
        return value;
      }

      if (c == Integer.class)
      {
        return value;
      }

      if (c == Long.class)
      {
        return value;
      }

      if (c == Float.class)
      {
        return value;
      }

      if (c == Double.class)
      {
        return value;
      }

      return value.toString();
    }
    catch (Throwable ex)
    {
      OM.LOG.error(ex);
    }

    return null;
  }

  /**
   * Returns the receiver's value for this property, either a {@link String} or a boxed primitive type. Return values of
   * all other types are converted with {@link #toString()} in {@link #eval(Object)}.
   */
  protected abstract Object eval(RECEIVER receiver);

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("Property[name=");
    builder.append(name);
    builder.append(", label=");
    builder.append(label);
    builder.append(", description=");
    builder.append(description);
    builder.append(", category=");
    builder.append(category);
    builder.append("]");
    return builder.toString();
  }

  /**
   * Describes a property of a receiver object and extracts its value with given arguments.
   *
   * @author Eike Stepper
   * @since 3.16
   */
  public static abstract class WithArguments<RECEIVER> extends Property<RECEIVER>
  {
    public WithArguments(String name, String label, String description, String category)
    {
      super(name, label, description, category);
    }

    public WithArguments(String name, String label, String description)
    {
      super(name, label, description);
    }

    public WithArguments(String name)
    {
      super(name);
    }

    @Override
    public boolean testValue(RECEIVER receiver, Object[] args, Object expectedValue)
    {
      try
      {
        Object value = eval(receiver, args);
        value = convertValue(value);
        return testValue(value, expectedValue);
      }
      catch (LifecycleException ex)
      {
        //$FALL-THROUGH$
      }
      catch (Throwable ex)
      {
        if (LifecycleUtil.isActive(receiver))
        {
          OM.LOG.error(ex);
        }
      }

      return false;
    }

    @Override
    protected final Object eval(RECEIVER receiver)
    {
      return eval(receiver, null);
    }

    protected abstract Object eval(RECEIVER receiver, Object[] args);
  }
}
