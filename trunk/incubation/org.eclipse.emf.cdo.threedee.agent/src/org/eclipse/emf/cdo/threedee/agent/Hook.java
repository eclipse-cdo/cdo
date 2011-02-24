/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.threedee.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class Hook
{
  private static Map<Thread, List<Object>> stacks = new WeakHashMap<Thread, List<Object>>();

  public static void before(Object target)
  {
    List<Object> stack = getStack();
    int last = stack.size() - 1;
    if (last >= 0)
    {
      Object source = stack.get(last);
      if (source == target)
      {
        return;
      }

      before(source, target);
    }

    stack.add(target);
  }

  public static void after(Object target)
  {
    List<Object> stack = getStack();
    int last = stack.size() - 1;
    if (last - 1 >= 0)
    {
      Object source = stack.get(last - 1);
      if (source == target)
      {
        return;
      }

      after(source, target);
      stack.remove(last);
    }
  }

  private static List<Object> getStack()
  {
    Thread thread = Thread.currentThread();
    List<Object> stack = stacks.get(thread);
    if (stack == null)
    {
      stack = new ArrayList<Object>();
      stacks.put(thread, stack);
    }

    return stack;
  }

  private static void before(Object source, Object target)
  {
    String sourceName = source.getClass().getName();
    if (!"org.eclipse.internal.net4j.buffer.BufferPool$Monitor".equals(sourceName))
    {
      System.err.println("3D-BEFORE: " + sourceName + " --> " + target.getClass().getName());
    }
  }

  private static void after(Object source, Object target)
  {
    String sourceName = source.getClass().getName();
    if (!"org.eclipse.internal.net4j.buffer.BufferPool$Monitor".equals(sourceName))
    {
      System.err.println("3D-AFTER: " + sourceName + " --> " + target.getClass().getName());
    }
  }
}
