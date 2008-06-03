/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public final class LifecycleUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_LIFECYCLE, LifecycleUtil.class);

  private LifecycleUtil()
  {
  }

  public static ILifecycleState getLifecycleState(Object object)
  {
    if (object instanceof ILifecycle.Introspection)
    {
      return ((ILifecycle.Introspection)object).getLifecycleState();
    }

    return ILifecycleState.ACTIVE;
  }

  public static boolean isActive(Object object)
  {
    if (object instanceof ILifecycle.Introspection)
    {
      return ((ILifecycle.Introspection)object).isActive();
    }

    return object != null;
  }

  public static void activate(Object object) throws LifecycleException
  {
    activate(object, false);
  }

  /**
   * @see Activator
   */
  public static void activate(Object object, boolean useAnnotation) throws LifecycleException
  {
    if (object instanceof ILifecycle)
    {
      ((ILifecycle)object).activate();
    }
    else if (object != null && useAnnotation)
    {
      invokeAnnotation(object, Activator.class);
    }
  }

  public static Exception activateSilent(Object object)
  {
    return activateSilent(object, false);
  }

  /**
   * @see Activator
   */
  public static Exception activateSilent(Object object, boolean useAnnotation)
  {
    try
    {
      activate(object, useAnnotation);
      return null;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public static boolean waitForActive(Object object, long millis)
  {
    try
    {
      if (object instanceof ILifecycle)
      {
        Lifecycle lifecycle = (Lifecycle)object;
        if (lifecycle.isActive())
        {
          return true;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        LifecycleEventAdapter adapter = new LifecycleEventAdapter()
        {
          @Override
          protected void onActivated(ILifecycle lifecycle)
          {
            latch.countDown();
          }
        };

        try
        {
          lifecycle.addListener(adapter);
          latch.await(millis, TimeUnit.MILLISECONDS);
        }
        finally
        {
          lifecycle.removeListener(adapter);
        }

        return lifecycle.isActive();
      }

      return true;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Exception deactivate(Object object)
  {
    return deactivate(object, false);
  }

  /**
   * @see Deactivator
   */
  public static Exception deactivate(Object object, boolean useAnnotation)
  {
    if (object instanceof ILifecycle)
    {
      return ((ILifecycle)object).deactivate();
    }
    else if (object != null && useAnnotation)
    {
      // TODO Handle evtl. return value (exception)
      invokeAnnotation(object, Deactivator.class);
    }

    return null;
  }

  public static void deactivateNoisy(Object object) throws LifecycleException
  {
    deactivateNoisy(object, false);
  }

  public static void deactivateNoisy(Object object, boolean useAnnotation) throws LifecycleException
  {
    Exception ex = deactivate(object, useAnnotation);
    if (ex instanceof RuntimeException)
    {
      throw (RuntimeException)ex;
    }
    else if (ex != null)
    {
      throw new LifecycleException(ex);
    }
  }

  private static <T extends Annotation> void invokeAnnotation(Object object, Class<T> annotationClass)
  {
    Class<?> c = object.getClass();
    while (c != Object.class)
    {
      final Method[] methods = c.getDeclaredMethods();
      for (Method method : methods)
      {
        if (method.getParameterTypes().length == 0)
        {
          Annotation annotation = method.getAnnotation(annotationClass);
          if (annotation != null)
          {
            invokeMethod(object, method);
            boolean propagate = annotationClass == Activator.class ? ((Activator)annotation).propagate()
                : ((Deactivator)annotation).propagate();
            if (!propagate)
            {
              break;
            }
          }
        }
      }

      c = c.getSuperclass();
    }
  }

  private static Object invokeMethod(Object object, Method method)
  {
    try
    {
      return method.invoke(object, (Object[])null);
    }
    catch (IllegalAccessException iae)
    {
      try
      {
        method.setAccessible(true);
        return method.invoke(object, (Object[])null);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(ex);
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Activator
  {
    boolean propagate() default true;
  }

  /**
   * @author Eike Stepper
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Deactivator
  {
    boolean propagate() default true;
  }
}
