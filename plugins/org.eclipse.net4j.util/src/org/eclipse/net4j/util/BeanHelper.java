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


import java.lang.reflect.Method;


public class BeanHelper
{
  public static Class<?> mostSpecificClass(Class[] classes)
  {
    int size = classes.length;

    for (int i = 0; i < size; i++)
    {
      Class<?> iClass = classes[i];

      for (int k = i + 1; k < size; k++)
      {
        Class<?> kClass = classes[k];

        if (iClass.isAssignableFrom(kClass))
        {
          // iClass is super class of kClass
          // iClass is less specific
          classes[i] = classes[--size];
          break;
        }
        else if (kClass.isAssignableFrom(iClass))
        {
          // kClass is super class of iClass
          // kClass is less specific
          classes[k] = classes[--size];
          break;
        }
      }
    }

    if (size > 1)
    {
      throw new RuntimeException("Ambiguous ViewFacories");
    }

    if (size < 1)
    {
      throw new RuntimeException("No ViewFacories");
    }

    return classes[0];
  }

  public static void dispatchChild(Object object, String name, Object value)
  {
    Method adder = findAdder(object.getClass(), name);
    if (adder == null) throw new BeanException("Child " + name + " not reognized");

    try
    {
      adder.invoke(object, new Object[] { value});
    }
    catch (Throwable t)
    {
      throw new BeanException("Child " + name + " not accessible", t);
    }
  }

  public static Object[] children(Object object, String name)
  {
    Method getter = findChildGetter(object.getClass(), name);
    if (getter == null) throw new BeanException("Child " + name + " not reognized");

    try
    {
      return (Object[]) getter.invoke(object, (Object[]) null);
    }
    catch (Throwable t)
    {
      throw new BeanException("Child " + name + " not accessible", t);
    }
  }

  public static void dispatchAttributeValue(Object object, String name, String value)
  {
    Method setter = findSetter(object.getClass(), name);
    if (setter == null) throw new BeanException("Attribute " + name + " not reognized");

    try
    {
      Class<?> type = setter.getParameterTypes()[0];

      if (type == Boolean.class)
      {
        Boolean bool = value == null ? null : Boolean.valueOf(value);
        setter.invoke(object, new Object[] { bool});
      }
      else
      {
        // if (type == String)
        setter.invoke(object, new Object[] { value});
      }
    }
    catch (Throwable t)
    {
      throw new BeanException("Attribute " + name + " not accessible", t);
    }
  }

  public static void dispatchAttributeValueBoolean(Object object, String name, Boolean value)
  {
    Method setter = findSetter(object.getClass(), name);
    if (setter == null) throw new BeanException("Attribute " + name + " not reognized");
    if (setter.getParameterTypes()[0] != Boolean.class)
      throw new BeanException("Attribute " + name + " is not Boolean");

    try
    {
      setter.invoke(object, new Object[] { value});
    }
    catch (Throwable t)
    {
      throw new BeanException("Attribute " + name + " not accessible", t);
    }
  }

  public static String attributeValue(Object object, String name)
  {
    Method getter = findGetter(object.getClass(), name);
    if (getter == null) throw new BeanException("Attribute " + name + " not reognized");

    try
    {
      Object returnValue = getter.invoke(object, (Object[]) null);
      if (returnValue == null) return null;
      if (returnValue instanceof String) return (String) returnValue;

      //if (returnValue instanceof Boolean) 
      return ((Boolean) returnValue).toString();

    }
    catch (Throwable t)
    {
      throw new BeanException("Attribute " + name + " not accessible", t);
    }
  }

  public static Boolean attributeValueBoolean(Object object, String name)
  {
    Method getter = findGetter(object.getClass(), name);
    if (getter == null) throw new BeanException("Attribute " + name + " not reognized");
    if (getter.getReturnType() != Boolean.class)
      throw new BeanException("Attribute " + name + " is not Boolean");

    try
    {
      return (Boolean) getter.invoke(object, (Object[]) null);
    }
    catch (Throwable t)
    {
      throw new BeanException("Attribute " + name + " accessible", t);
    }
  }

  public static Method findGetter(Class<? extends Object> clazz, String name)
  {
    String accessor = "is" + capitalize(name);
    Method method = findMethod(clazz, accessor);
    if (validGetter(method)) return method;

    accessor = "get" + capitalize(name);
    method = findMethod(clazz, accessor);
    if (validGetter(method)) return method;

    return null;
  }

  public static Method findChildGetter(Class<? extends Object> clazz, String name)
  {
    String accessor = "get" + capitalize(name) + "s";
    Method method = findMethod(clazz, accessor);
    if (validChildGetter(method)) return method;

    return null;
  }

  public static Method findSetter(Class<? extends Object> clazz, String name)
  {
    String accessor = "set" + capitalize(name);
    Method method = findMethod(clazz, accessor);
    return validSetter(method) ? method : null;
  }

  public static Method findAdder(Class<? extends Object> clazz, String name)
  {
    String accessor = "add" + capitalize(name);
    Method method = findMethod(clazz, accessor);
    return validAdder(method) ? method : null;
  }

  public static Method findMethod(Class<? extends Object> clazz, String name)
  {
    Method[] methods = clazz.getMethods();
    for (int i = 0; i < methods.length; i++)
    {
      Method method = methods[i];
      if (name.equals(method.getName())) return method;
    }
    return null;
  }

  private static boolean validGetter(Method method)
  {
    if (method == null) return false;

    Class[] paramTypes = method.getParameterTypes();
    if (paramTypes != null || paramTypes.length != 0) return false;

    Class<?> returnType = method.getReturnType();
    if (returnType == null || !validAttributeType(returnType)) return false;

    return true;
  }

  private static boolean validSetter(Method method)
  {
    if (method == null) return false;

    Class[] paramTypes = method.getParameterTypes();
    if (paramTypes == null || paramTypes.length != 1 || !validAttributeType(paramTypes[0]))
      return false;

    Class<?> returnType = method.getReturnType();
    if (returnType != null && returnType != void.class) return false;

    return true;
  }

  private static boolean validAdder(Method method)
  {
    if (method == null) return false;

    Class[] paramTypes = method.getParameterTypes();
    if (paramTypes == null || paramTypes.length != 1) return false;

    Class<?> returnType = method.getReturnType();
    if (returnType != null && returnType != void.class) return false;

    return true;
  }

  private static boolean validChildGetter(Method method)
  {
    if (method == null) return false;

    Class[] paramTypes = method.getParameterTypes();
    if (paramTypes != null || paramTypes.length != 0) return false;

    Class<?> returnType = method.getReturnType();
    if (returnType == null || !validChildType(returnType)) return false;

    return true;
  }

  private static boolean validAttributeType(Class<?> type)
  {
    return type == String.class || type == Boolean.class;
  }

  private static boolean validChildType(Class<?> type)
  {
    return type.isArray();
  }

  private static String capitalize(String name)
  {
    return StringHelper.firstToUpper(name);
  }
}
