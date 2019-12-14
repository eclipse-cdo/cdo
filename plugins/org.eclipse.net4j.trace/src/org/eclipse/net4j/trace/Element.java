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
package org.eclipse.net4j.trace;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.internal.trace.BufferTracer;
import org.eclipse.net4j.internal.trace.ElementCounters;

import org.eclipse.internal.net4j.buffer.Buffer;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class Element
{
  private static final Map<Object, Element> ELEMENTS = new WeakHashMap<>();

  private final int id;

  private final String type;

  protected Element(int id, String type)
  {
    this.type = type;
    this.id = id;
  }

  public Element(String type)
  {
    this(ElementCounters.INSTANCE.nextID(type), type);
  }

  public final int id()
  {
    return id;
  }

  public final String type()
  {
    return type;
  }

  @Override
  public String toString()
  {
    return type + "[" + id + "]";
  }

  public static Element getOrNull(Object object)
  {
    synchronized (ELEMENTS)
    {
      return ELEMENTS.get(object);
    }
  }

  public static Element get(Object object)
  {
    Element element;
    boolean created = false;

    synchronized (ELEMENTS)
    {
      element = ELEMENTS.get(object);
      if (element == null)
      {
        if (object instanceof Buffer)
        {
          Buffer buffer = (Buffer)object;
          element = new BufferElement(buffer.getID());
          created = true;
        }
        else if (object instanceof Thread)
        {
          Thread thread = (Thread)object;
          element = new ThreadElement(thread.getName());
        }
        else
        {
          element = new Element(object.getClass().getSimpleName());
          created = true;
        }

        ELEMENTS.put(object, element);
      }
    }

    if (created && BufferTracer.LISTENER != null)
    {
      BufferTracer.LISTENER.elementCreated(element);
    }

    return element;
  }

  public static BufferElement get(IBuffer buffer)
  {
    return (BufferElement)get((Object)buffer);
  }

  public static ThreadElement get(Thread thread)
  {
    return (ThreadElement)get((Object)thread);
  }

  /**
   * @author Eike Stepper
   */
  public static final class ThreadElement extends Element
  {
    public static final String TYPE = "Thread";

    private String name;

    public ThreadElement(String name)
    {
      super(TYPE);
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return type() + "[" + id() + ", " + name + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class BufferElement extends Element
  {
    public static final String TYPE = "Buffer";

    private Element provider;

    private Element owner;

    private Element thread;

    private BufferState state;

    private int position;

    private int limit;

    private boolean eos;

    private boolean ccam;

    public BufferElement(int id)
    {
      super(id, TYPE);
    }

    public Element getProvider()
    {
      return provider;
    }

    public void setProvider(Object object)
    {
      provider = get(object);
    }

    public Element getOwner()
    {
      return owner;
    }

    public void setOwner(Object object)
    {
      updateThread();

      Element newOwner = get(object);
      if (owner != newOwner && newOwner.getClass() != BufferElement.class)
      {
        Element oldOwner = owner;
        owner = newOwner;
        BufferTracer.LISTENER.ownerChanged(this, oldOwner, newOwner);
      }
    }

    public Element getThread()
    {
      return thread;
    }

    public BufferState getState()
    {
      return state;
    }

    public int getPosition()
    {
      return position;
    }

    public int getLimit()
    {
      return limit;
    }

    public boolean isEOS()
    {
      return eos;
    }

    public boolean isCCAM()
    {
      return ccam;
    }

    public void update(BufferState newState, int newPosition, int newLimit, boolean newEOS, boolean newCCAM)
    {
      updateThread();

      if (state != newState)
      {
        BufferState oldState = state;
        state = newState;
        BufferTracer.LISTENER.stateChanged(this, oldState, newState);
      }

      if (position != newPosition)
      {
        int oldPosition = position;
        position = newPosition;
        BufferTracer.LISTENER.positionChanged(this, oldPosition, newPosition);
      }

      if (limit != newLimit)
      {
        int oldLimit = limit;
        limit = newLimit;
        BufferTracer.LISTENER.limitChanged(this, oldLimit, newLimit);
      }

      if (eos != newEOS)
      {
        eos = newEOS;
        BufferTracer.LISTENER.eosChanged(this, newEOS);
      }

      if (ccam != newCCAM)
      {
        ccam = newCCAM;
        BufferTracer.LISTENER.ccamChanged(this, newCCAM);
      }
    }

    private void updateThread()
    {
      Element newThread = state == BufferState.RELEASED ? null : get(Thread.currentThread());
      if (thread != newThread)
      {
        Element oldThread = thread;
        thread = newThread;
        BufferTracer.LISTENER.threadChanged(this, oldThread, newThread);
      }
    }
  }
}
