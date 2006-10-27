/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public final class ReflectUtil
{
  public static final Class<Object> ROOT_CLASS = Object.class;

  public static final Class[] NO_PARAMETERS = null;

  public static final Object[] NO_ARGUMENTS = null;

  private static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

  private static final Method HASH_CODE_METHOD = lookupHashCodeMethod();

  private static final Map<Object, Long> ids = new WeakHashMap();

  public static boolean DUMP_STATICS = false;

  private static long lastID;

  private ReflectUtil()
  {
  }

  public static Integer getHashCode(Object object)
  {
    try
    {
      return (Integer)HASH_CODE_METHOD.invoke(object, NO_ARGUMENTS);
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    return 0;
  }

  public static Long getID(Object object)
  {
    Long id = ids.get(object);
    if (id == null)
    {
      id = ++lastID;
      ids.put(object, id);
    }

    return id;
  }

  public static String getPackageName(Class<? extends Object> c)
  {
    return getPackageName(c.getName());
  }

  public static String getPackageName(String className)
  {
    int lastDot = className.lastIndexOf('.');
    if (lastDot != -1)
    {
      className = className.substring(0, lastDot);
    }

    return className;
  }

  public static String getSimpleName(Class<? extends Object> c)
  {
    return getSimpleClassName(c.getName());
  }

  public static String getSimpleClassName(String name)
  {
    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      name = name.substring(lastDot + 1);
    }

    return name.replace('$', '.');
  }

  public static String getSimpleClassName(Object object)
  {
    return getSimpleName(object.getClass());
  }

  public static String getLabel(Object object)
  {
    return getSimpleClassName(object) + "@" + getID(object); //$NON-NLS-1$
  }

  public static void dump(Object object)
  {
    dump(object, ""); //$NON-NLS-1$
  }

  public static void dump(Object object, String prefix)
  {
    dump(object, prefix, IOUtil.OUT());
  }

  public static void dump(Object object, String prefix, PrintStream out)
  {
    out.print(toString(object, prefix));
  }

  public static String toString(Object object)
  {
    return toString(object, ""); //$NON-NLS-1$
  }

  public static String toString(Object object, String prefix)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(prefix);
    builder.append(getLabel(object));
    builder.append(NL);
    toString(object.getClass(), object, prefix, builder);
    return builder.toString();
  }

  private static void toString(Class<? extends Object> segment, Object object, String prefix,
      StringBuilder builder)
  {
    if (segment == ROOT_CLASS || segment == AbstractLifecycle.class)
    {
      return;
    }

    // Recurse
    toString(segment.getSuperclass(), object, prefix, builder);

    String segmentPrefix = segment == object.getClass() ? "" : getSimpleName(segment) + "."; //$NON-NLS-1$ //$NON-NLS-2$
    Field[] fields = segment.getDeclaredFields();
    for (Field field : fields)
    {
      if (field.isSynthetic())
      {
        continue;
      }

      if ((field.getModifiers() & Modifier.STATIC) != 0 && !DUMP_STATICS)
      {
        continue;
      }

      builder.append(prefix);
      builder.append(segmentPrefix);
      builder.append(field.getName());
      builder.append(" = "); //$NON-NLS-1$
      builder.append(getValue(object, field));
      builder.append(NL);
    }
  }

  public static Object getValue(Object object, Field field)
  {
    try
    {
      return field.get(object);
    }
    catch (IllegalAccessException ex)
    {
      field.setAccessible(true);
      try
      {
        return field.get(object);
      }
      catch (IllegalAccessException ex1)
      {
        throw new RuntimeException(ex1);
      }
    }
  }

  private static Method lookupHashCodeMethod()
  {
    Method method;

    try
    {
      method = ROOT_CLASS.getMethod("hashCode", NO_PARAMETERS); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      // This can really not happen
      throw new AssertionError();
    }

    if (!method.isAccessible())
    {
      method.setAccessible(true);
    }

    return method;
  }
}
