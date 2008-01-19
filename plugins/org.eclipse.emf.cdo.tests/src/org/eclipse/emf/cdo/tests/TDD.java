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
package org.eclipse.emf.cdo.tests;

import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.io.IOUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Eike Stepper
 */
public class TDD
{
  private static final AbstractOMTest test = new ChunkingWithMEMTest();

  private static final String METHOD_NAME = "testWriteNative";

  public static void main(String[] args) throws Throwable
  {
    if (METHOD_NAME != null && METHOD_NAME.length() != 0)
    {
      runTest(test, METHOD_NAME);
    }
    else
    {
      Method[] methods = test.getClass().getMethods();
      for (Method method : methods)
      {

        String methodName = method.getName();
        if (methodName.startsWith("test"))
        {
          runTest(test, methodName);
        }
      }
    }

    IOUtil.OUT().println("******* COMPLETED *******");
  }

  private static void runTest(AbstractOMTest test, String methodName) throws Exception, Throwable
  {
    test.setName(methodName);
    test.setUp();
    callMethod(test, methodName);
    test.tearDown();
  }

  private static void callMethod(Object object, String methodName) throws Throwable
  {
    try
    {
      Method method = object.getClass().getMethod(methodName, new Class[0]);
      method.invoke(object, new Object[0]);
    }
    catch (InvocationTargetException ex)
    {
      throw filterException(ex.getTargetException(), object.getClass());
    }
  }

  private static Throwable filterException(Throwable t, Class<? extends Object> entry)
  {
    StackTraceElement[] stackTrace = t.getStackTrace();
    int len;
    for (len = stackTrace.length - 1; len >= 0; len--)
    {
      if (stackTrace[len].getClassName().equals(entry.getName()))
      {
        StackTraceElement[] filtered = new StackTraceElement[++len];
        System.arraycopy(stackTrace, 0, filtered, 0, len);
        t.setStackTrace(filtered);
        break;
      }
    }

    return t;
  }
}
