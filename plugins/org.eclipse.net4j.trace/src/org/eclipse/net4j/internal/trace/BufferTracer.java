/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.trace;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.trace.Element;
import org.eclipse.net4j.trace.Element.BufferElement;
import org.eclipse.net4j.trace.Element.ThreadElement;
import org.eclipse.net4j.trace.Listener;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.Stack;

/**
 * @author Eike Stepper
 */
public class BufferTracer
{
  public static final Listener LISTENER = createListener();

  private static final ThreadLocal<Stack<Call>> CALL_STACKS = new ThreadLocal<Stack<Call>>()
  {
    @Override
    protected Stack<Call> initialValue()
    {
      return new Stack<Call>();
    }
  };

  private static final ThreadLocal<Boolean> EXECUTION = new ThreadLocal<Boolean>();

  public static void setThreadName(Thread thread, String name)
  {
    Element element = Element.getOrNull(thread);
    if (element instanceof ThreadElement)
    {
      ThreadElement threadElement = (ThreadElement)element;
      threadElement.setName(name);
    }
  }

  public static Stack<Call> execution(Object object, String method, Stack<Call> callStack)
  {
    if (EXECUTION.get() == null)
    {
      try
      {
        EXECUTION.set(Boolean.TRUE);

        if (callStack == null)
        {
          callStack = CALL_STACKS.get();

          Call lastCall = callStack.isEmpty() ? null : callStack.peek();
          if (lastCall != null)
          {
            Object owner = lastCall.getObject();
            Element caller = Element.get(owner);
            Element callee = Element.get(object);

            if (object instanceof IBuffer)
            {
              ((BufferElement)callee).setOwner(owner);
            }

            LISTENER.methodCalled(caller, lastCall.getMethod(), callee, method);
          }

          callStack.push(new Call(object, method));
        }
        else
        {
          callStack.pop();

          if (object instanceof IBuffer)
          {
            IBuffer buffer = (IBuffer)object;
            Call lastCall = callStack.peek();
            if (lastCall != null)
            {
              handleBuffer(buffer, lastCall.getObject(), lastCall.getMethod(), method);
            }
          }
        }
      }
      catch (Throwable ex)
      {
        ex.printStackTrace();
      }
      finally
      {
        EXECUTION.remove();
      }
    }

    return callStack;
  }

  private static void handleBuffer(IBuffer buffer, Object caller, String callingMethod, String calledMethod)
  {
    // System.out.println(
    // "---- [" + Thread.currentThread().getName() + "] " + caller.getClass().getSimpleName() + "." + callingMethod + "()
    // --> " + calledMethod + "()");

    BufferElement element = Element.get(buffer);

    if ("setBufferProvider".equals(calledMethod))
    {
      element.setProvider(buffer.getBufferProvider());
    }
    else
    {
      element.update(buffer.getState(), buffer.getPosition(), buffer.getLimit(), buffer.isEOS(), buffer.isCCAM());
    }
  }

  private static Listener createListener()
  {
    String type = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.trace.listenerType");
    if (type != null)
    {
      String description = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.trace.listenerDescription");
      return (Listener)IPluginContainer.INSTANCE.getElement(Listener.Factory.PRODUCT_GROUP, type, description);
    }

    return new DefaultListener();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Call
  {
    private final Object object;

    private final String method;

    public Call(Object object, String method)
    {
      this.object = object;
      this.method = method;
    }

    public Object getObject()
    {
      return object;
    }

    public String getMethod()
    {
      return method;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("Call[object=");
      builder.append(object);
      builder.append(", method=");
      builder.append(method);
      builder.append("]");
      return builder.toString();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DefaultListener implements Listener
  {
    @Override
    public void methodCalled(Element caller, String callingMethod, Element callee, String calledMethod)
    {
    }

    @Override
    public void elementCreated(Element element)
    {
    }

    @Override
    public void ownerChanged(BufferElement element, Element oldOwner, Element newOwner)
    {
    }

    @Override
    public void threadChanged(BufferElement element, Element oldThread, Element newThread)
    {
    }

    @Override
    public void stateChanged(BufferElement element, BufferState oldState, BufferState newState)
    {
    }

    @Override
    public void positionChanged(BufferElement element, int oldPosition, int newPosition)
    {
    }

    @Override
    public void limitChanged(BufferElement element, int oldLimit, int newLimit)
    {
    }

    @Override
    public void eosChanged(BufferElement element, boolean newEOS)
    {
    }

    @Override
    public void ccamChanged(BufferElement element, boolean newCCAM)
    {
    }
  }
}
