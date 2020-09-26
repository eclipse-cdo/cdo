/*
 * Copyright (c) 2007-2013, 2015, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - bug 376620: switch on primitive types
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.beans.Introspector;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * Various static helper methods for dealing with Java reflection.
 *
 * @author Eike Stepper
 */
public final class ReflectUtil
{
  public static final Class<Object> ROOT_CLASS = Object.class;

  public static final Class<?>[] NO_PARAMETERS = null;

  public static final Object[] NO_ARGUMENTS = null;

  private static final String NAMESPACE_SEPARATOR = "."; //$NON-NLS-1$

  private static final Method HASH_CODE_METHOD = lookupHashCodeMethod();

  private static final Map<Object, Long> ids = new WeakHashMap<>();

  private static final Long FAKE_ID = 0L;

  /**
   * @since 3.3
   */
  public static boolean REMEMBER_IDS = false;

  public static boolean DUMP_STATICS = false;

  private static long lastID;

  private ReflectUtil()
  {
  }

  public static Method getMethod(Class<?> c, String methodName, Class<?>... parameterTypes)
  {
    try
    {
      try
      {
        return c.getDeclaredMethod(methodName, parameterTypes);
      }
      catch (NoSuchMethodException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          return getMethod(superclass, methodName, parameterTypes);
        }

        throw ex;
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Object invokeMethod(Method method, Object target, Object... arguments)
  {
    makeAccessible(method);

    try
    {
      return method.invoke(target, arguments);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Field getField(Class<?> c, String fieldName)
  {
    try
    {
      try
      {
        return c.getDeclaredField(fieldName);
      }
      catch (NoSuchFieldException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          return getField(superclass, fieldName);
        }

        return null;
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @since 3.8
   */
  public static Field getAccessibleField(Class<?> c, String fieldName)
  {
    Field field = getField(c, fieldName);
    field.setAccessible(true);
    return field;
  }

  public static void collectFields(Class<?> c, List<Field> fields)
  {
    if (c == ROOT_CLASS)
    {
      return;
    }

    // Recurse
    collectFields(c.getSuperclass(), fields);

    try
    {
      Field[] declaredFields = c.getDeclaredFields();
      for (Field field : declaredFields)
      {
        if (field.isSynthetic())
        {
          continue;
        }

        if ((field.getModifiers() & Modifier.STATIC) != 0 && !DUMP_STATICS)
        {
          continue;
        }

        if (field.getAnnotation(ExcludeFromDump.class) != null)
        {
          continue;
        }

        fields.add(field);
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Object getValue(Field field, Object target)
  {
    makeAccessible(field);

    try
    {
      return field.get(target);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void setValue(Field field, Object target, Object value)
  {
    makeAccessible(field);

    try
    {
      field.set(target, value);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @since 3.8
   */
  public static String dumpThread()
  {
    StringBuilder builder = new StringBuilder();

    Thread thread = Thread.currentThread();
    builder.append(thread);
    builder.append(StringUtil.NL);

    StackTraceElement[] stackTrace = thread.getStackTrace();
    appendStackTrace(builder, stackTrace);
    return builder.toString();
  }

  /**
   * @since 3.11
   */
  public static void appendStackTrace(StringBuilder builder, StackTraceElement[] stackTrace)
  {
    for (int i = 2; i < stackTrace.length; i++)
    {
      StackTraceElement stackTraceElement = stackTrace[i];
      builder.append("\tat " + stackTraceElement); //$NON-NLS-1$
      builder.append(StringUtil.NL);
    }
  }

  public static void printStackTrace(PrintStream out, StackTraceElement[] stackTrace)
  {
    for (int i = 2; i < stackTrace.length; i++)
    {
      StackTraceElement stackTraceElement = stackTrace[i];
      out.println("\tat " + stackTraceElement); //$NON-NLS-1$
    }
  }

  public static void printStackTrace(StackTraceElement[] stackTrace)
  {
    printStackTrace(System.err, stackTrace);
  }

  /**
   * Prints the stack trace of the current thread to {@link System#err}.
   *
   * @since 3.4
   */
  public static void printStackTrace()
  {
    printStackTrace(Thread.currentThread().getStackTrace());
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

  public static synchronized Long getID(Object object)
  {
    if (REMEMBER_IDS)
    {
      Long id = ids.get(object);
      if (id == null)
      {
        id = ++lastID;
        ids.put(object, id);
      }

      return id;
    }

    return FAKE_ID;
  }

  public static String getPackageName(Class<? extends Object> c)
  {
    if (c == null)
    {
      return null;
    }

    return getPackageName(c.getName());
  }

  public static String getPackageName(String className)
  {
    if (className == null)
    {
      return null;
    }

    int lastDot = className.lastIndexOf('.');
    if (lastDot != -1)
    {
      className = className.substring(0, lastDot);
    }

    return className;
  }

  public static String getSimpleName(Class<? extends Object> c)
  {
    if (c == null)
    {
      return null;
    }

    return c.getSimpleName();
  }

  public static String getSimpleClassName(String name)
  {
    if (name == null)
    {
      return null;
    }

    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      name = name.substring(lastDot + 1);
    }

    return name.replace('$', '.');
  }

  public static String getSimpleClassName(Object object)
  {
    if (object == null)
    {
      return null;
    }

    String name = object.getClass().getName();
    return getSimpleClassName(name);
  }

  public static String getLabel(Object object)
  {
    if (object == null)
    {
      return null;
    }

    String name = object.getClass().getSimpleName();
    if (name.length() == 0)
    {
      name = "anonymous"; //$NON-NLS-1$
    }

    if (REMEMBER_IDS)
    {
      return name + "@" + getID(object); //$NON-NLS-1$
    }

    return name;
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

  @SuppressWarnings("unchecked")
  public static Pair<Field, Object>[] dumpToArray(Object object)
  {
    List<Field> fields = new ArrayList<>();
    collectFields(object.getClass(), fields);
    Pair<Field, Object>[] result = new Pair[fields.size()];
    int i = 0;
    for (Field field : fields)
    {
      Object value = getValue(field, object);
      result[i++] = Pair.create(field, value);
    }

    return result;
  }

  public static Object instantiate(Map<Object, Object> properties, String namespace, String classKey, ClassLoader classLoader)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    if (namespace != null)
    {
      if (namespace.length() == 0)
      {
        namespace = null;
      }
      else if (!namespace.endsWith(NAMESPACE_SEPARATOR))
      {
        namespace += NAMESPACE_SEPARATOR;
      }
    }

    String className = null;
    Map<String, Object> values = new HashMap<>();
    for (Entry<Object, Object> entry : properties.entrySet())
    {
      if (entry.getKey() instanceof String)
      {
        String key = (String)entry.getKey();
        if (namespace != null)
        {
          if (key.startsWith(namespace))
          {
            key = key.substring(namespace.length());
          }
          else
          {
            continue;
          }
        }

        if (classKey.equals(key))
        {
          Object classValue = entry.getValue();
          if (classValue instanceof String)
          {
            className = (String)classValue;
          }
          else
          {
            OM.LOG.warn("Value of classKey " + classKey + " is not a String"); //$NON-NLS-1$ //$NON-NLS-2$
          }
        }
        else
        {
          values.put(key, entry.getValue());
        }
      }
    }

    if (className == null)
    {
      throw new IllegalArgumentException("Properties do not contain a valid class name for key " + classKey); //$NON-NLS-1$
    }

    Class<?> c = classLoader.loadClass(className);
    Constructor<?> constructor;

    try
    {
      constructor = c.getConstructor();
    }
    catch (NoSuchMethodException | SecurityException ex)
    {
      throw new RuntimeException(ex);
    }

    Object instance = constructor.newInstance();
    Method[] methods = c.getMethods();
    for (Method method : methods)
    {
      if (isSetter(method))
      {
        String property = method.getName().substring(3);
        String name = Introspector.decapitalize(property);
        Object value = values.get(name);
        if (value == null)
        {
          name = StringUtil.uncap(property);
          value = values.get(name);
        }

        if (value != null)
        {
          Class<?> type = method.getParameterTypes()[0];
          if (!type.isAssignableFrom(value.getClass()))
          {
            if (value instanceof String)
            {
              String str = (String)value;
              value = null;
              if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class))
              {
                value = Boolean.parseBoolean(str);
              }
              else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class))
              {
                value = Byte.parseByte(str);
              }
              else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(short.class))
              {
                value = Short.parseShort(str);
              }
              else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class))
              {
                value = Integer.parseInt(str);
              }
              else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class))
              {
                value = Long.parseLong(str);
              }
              else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class))
              {
                value = Float.parseFloat(str);
              }
              else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class))
              {
                value = Double.parseDouble(str);
              }
            }
            else
            {
              value = null;
            }
          }

          if (value == null)
          {
            throw new IllegalArgumentException("Value of property " + name + " can not be assigned to type " //$NON-NLS-1$ //$NON-NLS-2$
                + type.getName());
          }

          method.invoke(instance, value);
        }
      }
    }

    return instance;
  }

  public static boolean isSetter(Method method)
  {
    return method.getParameterTypes().length == 1 && isSetterName(method.getName());
  }

  public static boolean isSetterName(String name)
  {
    return name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3)); //$NON-NLS-1$
  }

  public static String toString(Object object)
  {
    return toString(object, "  "); //$NON-NLS-1$
  }

  public static String toString(Object object, String prefix)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(prefix);
    builder.append(getLabel(object));
    builder.append(StringUtil.NL);
    toString(object.getClass(), object, prefix, builder);
    return builder.toString();
  }

  private static void toString(Class<? extends Object> segment, Object object, String prefix, StringBuilder builder)
  {
    if (segment == ROOT_CLASS || segment == Lifecycle.class)
    {
      return;
    }

    // Recurse
    toString(segment.getSuperclass(), object, prefix, builder);

    String segmentPrefix = segment == object.getClass() ? "" : getSimpleName(segment) + NAMESPACE_SEPARATOR; //$NON-NLS-1$
    for (Field field : segment.getDeclaredFields())
    {
      if (field.isSynthetic())
      {
        continue;
      }

      if ((field.getModifiers() & Modifier.STATIC) != 0 && !DUMP_STATICS)
      {
        continue;
      }

      if (field.getAnnotation(ExcludeFromDump.class) != null)
      {
        continue;
      }

      builder.append(prefix);
      builder.append(segmentPrefix);
      builder.append(field.getName());
      builder.append(" = "); //$NON-NLS-1$

      Object value = getValue(field, object);
      if (value instanceof Map<?, ?>)
      {
        value = ((Map<?, ?>)value).entrySet();
      }

      if (value instanceof Collection<?>)
      {
        builder.append(StringUtil.NL);
        Collection<?> collection = (Collection<?>)value;
        Object[] array = collection.toArray(new Object[collection.size()]);
        for (Object element : array)
        {
          builder.append("    "); //$NON-NLS-1$
          builder.append(element);
          builder.append(StringUtil.NL);
        }
      }
      else
      {
        builder.append(value);
        builder.append(StringUtil.NL);
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

    makeAccessible(method);
    return method;
  }

  /**
   * @since 3.12
   */
  @SuppressWarnings("deprecation")
  public static <T> void makeAccessible(AccessibleObject accessibleObject)
  {
    if (!accessibleObject.isAccessible())
    {
      accessibleObject.setAccessible(true);
    }
  }

  /**
   * Annotates fields that are to be skipped in {@link ReflectUtil#collectFields(Class, List)
   * ReflectUtil.collectFields()} and {@link ReflectUtil#toString(Object) ReflectUtil.toString()}.
   *
   * @author Eike Stepper
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ExcludeFromDump
  {
  }

  /**
   * @author Christian W. Damus (CEA)
   * @since 3.3
   */
  public static enum PrimitiveType
  {
    BOOLEAN(boolean.class), //
    BYTE(byte.class), //
    CHAR(char.class), //
    SHORT(short.class), //
    INT(int.class), //
    LONG(long.class), //
    FLOAT(float.class), //
    DOUBLE(double.class), //
    VOID(void.class), //
    NONE(null);

    private static final Map<Class<?>, PrimitiveType> INSTANCES = new HashMap<>();

    private final Class<?> type;

    static
    {
      for (PrimitiveType primitiveType : values())
      {
        if (primitiveType.type != null)
        {
          INSTANCES.put(primitiveType.type, primitiveType);
        }
      }
    }

    private PrimitiveType(Class<?> type)
    {
      this.type = type;
    }

    public Class<?> type()
    {
      return type;
    }

    public static PrimitiveType forClass(Class<?> clazz)
    {
      PrimitiveType result = INSTANCES.get(clazz);
      return result == null ? NONE : result;
    }
  }
}
