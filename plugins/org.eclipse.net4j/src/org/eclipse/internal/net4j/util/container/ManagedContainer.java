/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.container;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.factory.FactoryKey;
import org.eclipse.internal.net4j.util.lifecycle.Lifecycle;
import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class ManagedContainer extends Lifecycle implements IManagedContainer
{
  private IRegistry<IFactoryKey, IFactory> factoryRegistry;

  private List<IElementProcessor> postProcessors;

  private IRegistry<ElementKey, Object> elementRegistry = new HashMapRegistry();

  private long maxElementID;

  public ManagedContainer()
  {
  }

  public synchronized IRegistry<IFactoryKey, IFactory> getFactoryRegistry()
  {
    if (factoryRegistry == null)
    {
      factoryRegistry = createFactoryRegistry();
    }

    return factoryRegistry;
  }

  public ManagedContainer registerFactory(IFactory factory)
  {
    getFactoryRegistry().put(factory.getKey(), factory);
    return this;
  }

  public synchronized List<IElementProcessor> getPostProcessors()
  {
    if (postProcessors == null)
    {
      postProcessors = createPostProcessors();
    }

    return postProcessors;
  }

  public void addPostProcessor(IElementProcessor postProcessor)
  {
    getPostProcessors().add(postProcessor);
  }

  public void removePostProcessor(IElementProcessor postProcessor)
  {
    getPostProcessors().remove(postProcessor);
  }

  public Set<String> getProductGroups()
  {
    Set<String> result = new HashSet();
    for (IFactoryKey key : factoryRegistry.keySet())
    {
      result.add(key.getProductGroup());
    }

    for (ElementKey key : elementRegistry.keySet())
    {
      result.add(key.getProductGroup());
    }

    return result;
  }

  public Set<String> getFactoryTypes(String productGroup)
  {
    Set<String> result = new HashSet();
    for (IFactoryKey key : factoryRegistry.keySet())
    {
      if (ObjectUtil.equals(key.getProductGroup(), productGroup))
      {
        result.add(key.getType());
      }
    }

    for (ElementKey key : elementRegistry.keySet())
    {
      if (ObjectUtil.equals(key.getProductGroup(), productGroup))
      {
        result.add(key.getFactoryType());
      }
    }

    return result;
  }

  public boolean isEmpty()
  {
    return elementRegistry.isEmpty();
  }

  public Object[] getElements()
  {
    return elementRegistry.values().toArray();
  }

  public Object[] getElements(String productGroup)
  {
    List result = new ArrayList();
    for (Entry<ElementKey, Object> entry : elementRegistry.entrySet())
    {
      ElementKey key = entry.getKey();
      if (ObjectUtil.equals(key.getProductGroup(), productGroup))
      {
        result.add(entry.getValue());
      }
    }

    return result.toArray();
  }

  public Object[] getElements(String productGroup, String factoryType)
  {
    List result = new ArrayList();
    for (Entry<ElementKey, Object> entry : elementRegistry.entrySet())
    {
      ElementKey key = entry.getKey();
      if (ObjectUtil.equals(key.getProductGroup(), productGroup)
          && ObjectUtil.equals(key.getFactoryType(), factoryType))
      {
        result.add(entry.getValue());
      }
    }

    return result.toArray();
  }

  public Object getElement(String productGroup, String factoryType, String description)
  {
    synchronized (elementRegistry)
    {
      ElementKey key = new ElementKey(productGroup, factoryType, description);
      Object element = elementRegistry.get(key);
      if (element == null)
      {
        element = createElement(productGroup, factoryType, description);
        if (element != null)
        {
          element = postProcessElement(productGroup, factoryType, description, element);
          LifecycleUtil.activate(element);
          key.setID(++maxElementID);
          elementRegistry.put(key, element);
          fireEvent(new SingleDeltaContainerEvent(this, element, IContainerDelta.Kind.ADDED));
        }
      }

      return element;
    }
  }

  /**
   * TODO Replace usages by factories (BufferProvider, ExecutorService,
   * ProtocolFactoryRegistry)
   */
  public Object putElement(String productGroup, String factoryType, String description, Object element)
  {
    synchronized (elementRegistry)
    {
      ContainerEvent event = new ContainerEvent(this);
      ElementKey key = new ElementKey(productGroup, factoryType, description);
      key.setID(++maxElementID);
      Object oldElement = elementRegistry.put(key, element);
      if (oldElement != null)
      {
        event.addDelta(oldElement, IContainerDelta.Kind.REMOVED);
      }

      event.addDelta(element, IContainerDelta.Kind.ADDED);
      fireEvent(event);
      return oldElement;
    }
  }

  public Object removeElement(String productGroup, String factoryType, String description)
  {
    ElementKey key = new ElementKey(productGroup, factoryType, description);
    Object element = elementRegistry.remove(key);
    if (element != null)
    {
      fireEvent(new SingleDeltaContainerEvent(this, element, IContainerDelta.Kind.REMOVED));
    }

    return element;
  }

  public void clearElements()
  {
    synchronized (elementRegistry)
    {
      if (!elementRegistry.isEmpty())
      {
        ContainerEvent event = new ContainerEvent(this);
        for (Object element : elementRegistry.values())
        {
          event.addDelta(element, IContainerDelta.Kind.REMOVED);
        }

        elementRegistry.clear();
        fireEvent(event);
      }
    }
  }

  public void loadElements(InputStream stream) throws IOException
  {
    synchronized (elementRegistry)
    {
      clearElements();
      ObjectInputStream ois = new ObjectInputStream(stream);
      int size = ois.readInt();
      for (int i = 0; i < size; i++)
      {
        try
        {
          ElementKey key = (ElementKey)ois.readObject();
          Object element = getElement(key.getProductGroup(), key.getFactoryType(), key.getDescription());

          boolean active = ois.readBoolean();
          if (active)
          {
            LifecycleUtil.activate(element);
          }
        }
        catch (ClassNotFoundException cannotHappen)
        {
        }
      }

      initMaxElementID();
    }
  }

  public void saveElements(OutputStream stream) throws IOException
  {
    synchronized (elementRegistry)
    {
      ObjectOutputStream oos = new ObjectOutputStream(stream);
      List<Entry<ElementKey, Object>> entries = new ArrayList(elementRegistry.entrySet());
      Collections.sort(entries, new EntryComparator());

      oos.writeInt(entries.size());
      for (Entry<ElementKey, Object> entry : entries)
      {
        oos.writeObject(entry.getKey());
        oos.writeBoolean(LifecycleUtil.isActive(entry.getValue()));
      }
    }
  }

  @Override
  public String toString()
  {
    return "ManagedContainer";
  }

  protected IRegistry<IFactoryKey, IFactory> createFactoryRegistry()
  {
    return new HashMapRegistry();
  }

  protected List<IElementProcessor> createPostProcessors()
  {
    return new ArrayList();
  }

  protected Object createElement(String productGroup, String factoryType, String description)
  {
    FactoryKey key = new FactoryKey(productGroup, factoryType);
    IFactory factory = getFactoryRegistry().get(key);
    if (factory != null)
    {
      return factory.create(description);
    }

    return null;
  }

  protected Object postProcessElement(String productGroup, String factoryType, String description, Object element)
  {
    for (IElementProcessor processor : getPostProcessors())
    {
      element = processor.process(this, productGroup, factoryType, description, element);
    }

    return element;
  }

  protected void initMaxElementID()
  {
    synchronized (elementRegistry)
    {
      maxElementID = 0L;
      for (ElementKey key : elementRegistry.keySet())
      {
        long id = key.getID();
        if (maxElementID < id)
        {
          maxElementID = id;
        }
      }
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(getFactoryRegistry());
    LifecycleUtil.activate(getPostProcessors());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (Object element : elementRegistry.values())
    {
      try
      {
        LifecycleUtil.deactivateNoisy(element);
      }
      catch (RuntimeException ex)
      {
        Net4j.LOG.error(ex);
      }
    }

    LifecycleUtil.deactivate(factoryRegistry);
    LifecycleUtil.deactivate(postProcessors);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static final class ElementKey implements Serializable, Comparable
  {
    private static final long serialVersionUID = 1L;

    private long id;

    private String productGroup;

    private String factoryType;

    private String description;

    public ElementKey(String productGroup, String factoryType, String description)
    {
      this.productGroup = productGroup;
      this.factoryType = factoryType;
      this.description = description;
    }

    public long getID()
    {
      return id;
    }

    public void setID(long id)
    {
      this.id = id;
    }

    public String getProductGroup()
    {
      return productGroup;
    }

    public String getFactoryType()
    {
      return factoryType;
    }

    public String getDescription()
    {
      return description;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof ElementKey)
      {
        ElementKey key = (ElementKey)obj;
        return ObjectUtil.equals(productGroup, key.productGroup) && ObjectUtil.equals(factoryType, key.factoryType)
            && ObjectUtil.equals(description, key.description);
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return ObjectUtil.hashCode(productGroup) ^ ObjectUtil.hashCode(factoryType) ^ ObjectUtil.hashCode(description);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}[{1}, {2}]", productGroup, factoryType, description);
    }

    public int compareTo(Object o)
    {
      if (o instanceof ElementKey)
      {
        ElementKey key = (ElementKey)o;
        if (id < key.id)
        {
          return -1;
        }

        if (id > key.id)
        {
          return 1;
        }

        return 0;
      }

      return 0;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class EntryComparator implements Comparator
  {
    public int compare(Object o1, Object o2)
    {
      Entry<ElementKey, Object> entry1 = (Entry<ElementKey, Object>)o1;
      Entry<ElementKey, Object> entry2 = (Entry<ElementKey, Object>)o2;
      return entry1.getKey().compareTo(entry2.getKey());
    }
  }
}
