/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.container;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Eike Stepper
 */
public class PluginElementProcessorList extends Lifecycle implements List<IElementProcessor>
{
  private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

  public static final String NAMESPACE = OM.BUNDLE_ID;

  public static final String EXT_POINT = "elementProcessors"; //$NON-NLS-1$

  private List<IElementProcessor> processors = new ArrayList<IElementProcessor>();

  private Object extensionRegistryListener;

  public PluginElementProcessorList()
  {
  }

  @Override
  public boolean add(IElementProcessor o)
  {
    return processors.add(o);
  }

  @Override
  public void add(int index, IElementProcessor element)
  {
    processors.add(index, element);
  }

  @Override
  public boolean addAll(Collection<? extends IElementProcessor> c)
  {
    return processors.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends IElementProcessor> c)
  {
    return processors.addAll(index, c);
  }

  @Override
  public void clear()
  {
    processors.clear();
  }

  @Override
  public boolean contains(Object o)
  {
    return processors.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return processors.containsAll(c);
  }

  @Override
  public boolean equals(Object o)
  {
    return processors.equals(o);
  }

  @Override
  public IElementProcessor get(int index)
  {
    return processors.get(index);
  }

  @Override
  public int hashCode()
  {
    return processors.hashCode();
  }

  @Override
  public int indexOf(Object o)
  {
    return processors.indexOf(o);
  }

  @Override
  public boolean isEmpty()
  {
    return processors.isEmpty();
  }

  @Override
  public Iterator<IElementProcessor> iterator()
  {
    return processors.iterator();
  }

  @Override
  public int lastIndexOf(Object o)
  {
    return processors.lastIndexOf(o);
  }

  @Override
  public ListIterator<IElementProcessor> listIterator()
  {
    return processors.listIterator();
  }

  @Override
  public ListIterator<IElementProcessor> listIterator(int index)
  {
    return processors.listIterator(index);
  }

  @Override
  public IElementProcessor remove(int index)
  {
    return processors.remove(index);
  }

  @Override
  public boolean remove(Object o)
  {
    return processors.remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return processors.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return processors.retainAll(c);
  }

  @Override
  public IElementProcessor set(int index, IElementProcessor element)
  {
    return processors.set(index, element);
  }

  @Override
  public int size()
  {
    return processors.size();
  }

  @Override
  public List<IElementProcessor> subList(int fromIndex, int toIndex)
  {
    return processors.subList(fromIndex, toIndex);
  }

  @Override
  public Object[] toArray()
  {
    return processors.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    return processors.toArray(a);
  }

  @Override
  public String toString()
  {
    return processors.toString();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    try
    {
      doActivateOSGi();
    }
    catch (Throwable t)
    {
      OM.LOG.warn(t);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      doDeactivateOSGi();
    }
    catch (Throwable t)
    {
      OM.LOG.warn(t);
    }

    processors.clear();
    super.doDeactivate();
  }

  private void doActivateOSGi() throws Exception
  {
    org.eclipse.core.runtime.IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
    if (extensionRegistry == null)
    {
      return;
    }

    org.eclipse.core.runtime.IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(NAMESPACE, EXT_POINT);
    for (org.eclipse.core.runtime.IConfigurationElement element : elements)
    {
      IElementProcessor processor = (IElementProcessor)element.createExecutableExtension(ATTR_CLASS);
      processors.add(processor);
    }

    org.eclipse.core.runtime.IRegistryChangeListener listener = new org.eclipse.core.runtime.IRegistryChangeListener()
    {
      @Override
      public void registryChanged(org.eclipse.core.runtime.IRegistryChangeEvent event)
      {
        org.eclipse.core.runtime.IExtensionDelta[] deltas = event.getExtensionDeltas(NAMESPACE, EXT_POINT);
        for (org.eclipse.core.runtime.IExtensionDelta delta : deltas)
        {
          // TODO Handle ExtensionDelta
          OM.LOG.warn("ExtensionDelta not handled: " + delta); //$NON-NLS-1$
        }
      }
    };

    extensionRegistry.addRegistryChangeListener(listener, NAMESPACE);
    extensionRegistryListener = listener;
  }

  private void doDeactivateOSGi()
  {
    org.eclipse.core.runtime.IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
    if (extensionRegistry == null)
    {
      return;
    }

    extensionRegistry.removeRegistryChangeListener((org.eclipse.core.runtime.IRegistryChangeListener)extensionRegistryListener);
  }
}
