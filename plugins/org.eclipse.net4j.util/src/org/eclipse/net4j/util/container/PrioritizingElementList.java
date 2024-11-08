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

import org.eclipse.net4j.util.container.PrioritizingElementList.Prioritized;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.26
 */
public abstract class PrioritizingElementList<ELEMENT extends Prioritized> extends Lifecycle
{
  private final String productGroup;

  private final Class<ELEMENT> elementType;

  private final IManagedContainer container;

  private final IListener containerListener = new ContainerEventAdapter<Object>()
  {
    @Override
    protected void onAdded(IContainer<Object> container, Object element)
    {
      onChanged(element);
    }

    @Override
    protected void onRemoved(IContainer<Object> container, Object element)
    {
      onChanged(element);
    }

    private void onChanged(Object element)
    {
      if (elementType.isInstance(element))
      {
        initElements();
      }
    }
  };

  private ELEMENT[] elements;

  protected PrioritizingElementList(String productGroup, Class<ELEMENT> elementType, IManagedContainer container)
  {
    this.productGroup = productGroup;
    this.elementType = elementType;
    this.container = container;
  }

  protected PrioritizingElementList(String productGroup, Class<ELEMENT> elementType)
  {
    this(productGroup, elementType, IPluginContainer.INSTANCE);
  }

  protected ELEMENT[] getElements()
  {
    return elements;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    initElements();
    container.addListener(containerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    container.removeListener(containerListener);
    elements = null;
    super.doDeactivate();
  }

  protected abstract ELEMENT[] createElementArray(int length);

  private void initElements()
  {
    List<ELEMENT> list = new ArrayList<>();
    container.forEachElement(productGroup, elementType, list::add);
    list.sort(null);

    ELEMENT[] array = createElementArray(list.size());
    elements = list.toArray(array);
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
