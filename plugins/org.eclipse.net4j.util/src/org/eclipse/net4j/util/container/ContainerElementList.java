/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.event.IListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 * @since 3.26
 */
public class ContainerElementList<ELEMENT> extends Container<ELEMENT>
{
  private final IListener containerListener = new ContainerEventAdapter<Object>()
  {
    @Override
    protected void onAdded(IContainer<Object> container, Object object)
    {
      modify(object, elementList::add, ContainerElementList.this::fireElementAddedEvent);
    }

    @Override
    protected synchronized void onRemoved(IContainer<Object> container, Object object)
    {
      modify(object, elementList::remove, ContainerElementList.this::fireElementRemovedEvent);
    }

    private void modify(Object object, Predicate<ELEMENT> listModifier, Consumer<ELEMENT> notifier)
    {
      if (elementType.isInstance(object))
      {
        ELEMENT element = elementType.cast(object);
        boolean modified;

        synchronized (elementList)
        {
          modified = listModifier.test(element);
          if (modified)
          {
            elements = null;
          }
        }

        if (modified)
        {
          notifier.accept(element);
        }
      }
    }

  };

  private final IManagedContainer container;

  private final Class<ELEMENT> elementType;

  private final List<ELEMENT> elementList = new ArrayList<>();

  private ELEMENT[] elements;

  public ContainerElementList(Class<ELEMENT> elementType, IManagedContainer container)
  {
    this.elementType = elementType;
    this.container = container;
  }

  public ContainerElementList(Class<ELEMENT> elementType)
  {
    this(elementType, IPluginContainer.INSTANCE);
  }

  public final IManagedContainer getContainer()
  {
    return container;
  }

  public final Class<ELEMENT> getElementType()
  {
    return elementType;
  }

  @Override
  public final boolean isEmpty()
  {
    checkActive();

    synchronized (elementList)
    {
      return elementList.isEmpty();
    }
  }

  @Override
  public final ELEMENT[] getElements()
  {
    checkActive();

    synchronized (elementList)
    {
      if (elements == null)
      {
        @SuppressWarnings("unchecked")
        ELEMENT[] array = (ELEMENT[])Array.newInstance(elementType, elementList.size());
        elementList.toArray(array);

        elements = postProcessElementArray(array);
      }

      return elements;
    }
  }

  public final boolean forEachElement(Predicate<ELEMENT> handler)
  {
    for (ELEMENT element : getElements())
    {
      if (handler.test(element))
      {
        return true;
      }
    }

    return false;
  }

  protected ELEMENT[] postProcessElementArray(ELEMENT[] elements)
  {
    if (Comparable.class.isAssignableFrom(elementType))
    {
      Arrays.sort(elements, null);
    }

    return elements;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    for (Object object : container.getElements())
    {
      if (elementType.isInstance(object))
      {
        ELEMENT element = elementType.cast(object);
        elementList.add(element);
      }
    }

    container.addListener(containerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    container.removeListener(containerListener);
    elementList.clear();
    elements = null;
    super.doDeactivate();
  }

  /**
   * Smaller {@link #getPriority() priority} values are ranked higher.
   *
   * @author Eike Stepper
   */
  public interface Prioritized extends Comparable<Prioritized>
  {
    /**
     * Smaller values are ranked higher.
     */
    public int getPriority();

    @Override
    public default int compareTo(Prioritized o)
    {
      return Integer.compare(getPriority(), o.getPriority());
    }
  }
}
