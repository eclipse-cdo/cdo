/*
 * Copyright (c) 2007-2013, 2015, 2016, 2018-2024 Eike Stepper (Loehne, Germany) and others.
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
import java.util.WeakHashMap;
import java.util.function.Predicate;

/**
 * Various static helper methods for dealing with Java reflection.
 *
 * @author Eike Stepper
 * @since 3.14
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

  /**
   * @since 3.12
   */
  @SuppressWarnings("all")
  public static <T> void makeAccessible(AccessibleObject accessibleObject)
  {
    try
    {
      if (!accessibleObject.isAccessible())
      {
        accessibleObject.setAccessible(true);
      }
    }
    catch (Throwable ex)
    {
      if (accessibleObject instanceof Field)
      {
        AccessUtil.setAccessible((Field)accessibleObject);
      }
      else if (accessibleObject instanceof Method)
      {
        AccessUtil.setAccessible((Method)accessibleObject);
      }
      else if (accessibleObject instanceof Constructor)
      {
        AccessUtil.setAccessible((Constructor)accessibleObject);
      }
    }
  }

  public static Method getMethod(Class<?> c, String methodName, Class<?>... parameterTypes)
  {
    try
    {
      return doGetMethod(c, methodName, parameterTypes);
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }
  }

  /**
   * @since 3.20
   */
  public static Method getMethodOrNull(Class<?> c, String methodName, Class<?>... parameterTypes)
  {
    try
    {
      return doGetMethod(c, methodName, parameterTypes);
    }
    catch (NoSuchMethodException ex)
    {
      return null;
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }
  }

  private static Method doGetMethod(Class<?> c, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException
  {
    try
    {
      Method method = c.getDeclaredMethod(methodName, parameterTypes);
      if (method != null)
      {
        makeAccessible(method);
      }

      return method;
    }
    catch (NoSuchMethodException ex)
    {
      Class<?> superclass = c.getSuperclass();
      if (superclass != null)
      {
        Method method = doGetMethod(superclass, methodName, parameterTypes);
        if (method != null)
        {
          makeAccessible(method);
        }

        return method;
      }

      throw ex;
    }
  }

  public static Object invokeMethod(Method method, Object target, Object... arguments)
  {
    try
    {
      return method.invoke(target, arguments);
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }
  }

  /**
   * @since 3.14
   */
  @SuppressWarnings("unchecked")
  public static <T> T invokeMethod(String methodName, Object target)
  {
    if (target instanceof Class)
    {
      return (T)invokeMethod(getMethod((Class<?>)target, methodName), null);
    }

    return (T)invokeMethod(getMethod(target.getClass(), methodName), target);
  }

  public static Field getField(Class<?> c, String fieldName)
  {
    try
    {
      try
      {
        Field field = c.getDeclaredField(fieldName);
        if (field != null)
        {
          makeAccessible(field);
        }

        return field;
      }
      catch (NoSuchFieldException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          Field field = getField(superclass, fieldName);
          if (field != null)
          {
            makeAccessible(field);
          }

          return field;
        }

        return null;
      }
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }
  }

  /**
   * @since 3.8
   * @deprecated As of 3.14 use {@link #getField(Class, String)}.
   */
  @Deprecated
  public static Field getAccessibleField(Class<?> c, String fieldName)
  {
    return getField(c, fieldName);
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

        try
        {
          makeAccessible(field);
          fields.add(field);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }
  }

  public static Object getValue(Field field, Object target)
  {
    try
    {
      return field.get(target);
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }
  }

  /**
   * @since 3.14
   */
  @SuppressWarnings("unchecked")
  public static <T> T getValue(String fieldName, Object target)
  {
    if (target instanceof Class)
    {
      Field field = getField((Class<?>)target, fieldName);
      if (field == null)
      {
        throw new ReflectionException("No such field: " + fieldName);
      }

      return (T)getValue(field, null);
    }

    Field field = getField(target.getClass(), fieldName);
    if (field == null)
    {
      throw new ReflectionException("No such field: " + fieldName);
    }

    return (T)getValue(field, target);
  }

  public static void setValue(Field field, Object target, Object value)
  {
    setValue(field, target, value, false);
  }

  /**
   * @since 3.14
   */
  public static void setValue(Field field, Object target, Object value, boolean force)
  {
    try
    {
      if (force && (field.getModifiers() & Modifier.FINAL) != 0)
      {
        AccessUtil.setNonFinal(field);
      }

      field.set(target, value);
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
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

  /**
   * @since 3.22
   */
  public static void dump(Object object, Predicate<Setting> consumer)
  {
    Class<?> c = object.getClass();
    dump(object, c, consumer);
  }

  private static boolean dump(Object object, Class<?> c, Predicate<Setting> consumer)
  {
    if (c == ROOT_CLASS)
    {
      return true;
    }

    // Recurse
    if (!dump(object, c.getSuperclass(), consumer))
    {
      return false;
    }

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

        try
        {
          makeAccessible(field);
          if (!consumer.test(new Setting(object, field)))
          {
            return false;
          }
        }
        catch (Throwable ex)
        {
          String getterName = StringUtil.cap(field.getName());

          Method getter = getMethodOrNull(c, getterName);
          if (getter == null)
          {
            getterName = StringUtil.cap(getterName);

            Class<?> fieldType = field.getType();
            if (fieldType == boolean.class || fieldType == Boolean.class)
            {
              getterName = "is" + getterName;
            }
            else
            {
              getterName = "is" + getterName;
            }

            getter = getMethodOrNull(c, getterName);
          }

          if (getter != null)
          {
            if (!consumer.test(new Setting(object, field, getter)))
            {
              return false;
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      throw ReflectionException.wrap(ex);
    }

    return true;
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
    for (Map.Entry<Object, Object> entry : properties.entrySet())
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

      makeAccessible(field);

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
      throw new AssertionError(ex);
    }

    makeAccessible(method);
    return method;
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

    /**
     * @since 3.14
     */
    public static PrimitiveType forClass(Class<?> clazz)
    {
      PrimitiveType result = INSTANCES.get(clazz);
      return result == null ? NONE : result;
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.14
   */
  public static class ReflectionException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public ReflectionException()
    {
    }

    public ReflectionException(String message, Exception cause)
    {
      super(message, cause);
    }

    public ReflectionException(String message)
    {
      super(message);
    }

    public ReflectionException(Exception cause)
    {
      super(cause);
    }

    /**
     * @since 3.20
     */
    public Exception unwrap()
    {
      return unwrap(this);
    }

    public static Exception unwrap(Exception exception)
    {
      if (exception instanceof ReflectionException)
      {
        return (Exception)exception.getCause();
      }

      return exception;
    }

    public static ReflectionException wrap(Exception exception)
    {
      if (exception instanceof ReflectionException)
      {
        return (ReflectionException)exception;
      }

      return new ReflectionException(exception);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.22
   */
  public static final class Setting
  {
    private final String name;

    private final Class<?> type;

    private final Object value;

    private Setting(String name, Class<?> type, Object value)
    {
      this.name = name;
      this.type = type;
      this.value = value;
    }

    private Setting(Object object, Field field) throws IllegalAccessException, IllegalArgumentException
    {
      this(field.getName(), field.getType(), field.get(object));
    }

    private Setting(Object object, Field field, Method getter) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
      this(field.getName(), field.getType(), getter.invoke(object));
    }

    public String getName()
    {
      return name;
    }

    public Class<?> getType()
    {
      return type;
    }

    public Object getValue()
    {
      return value;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("Setting[name=");
      builder.append(name);
      builder.append(", type=");
      builder.append(type);
      builder.append(", value=");
      builder.append(value);
      builder.append("]");
      return builder.toString();
    }
  }
}
