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
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.util.event.Notifier;
import org.eclipse.internal.net4j.util.factory.FactoryKey;
import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class ManagedContainer extends Notifier implements IManagedContainer
{
  private IRegistry<FactoryKey, IFactory> factoryRegistry = new HashMapRegistry();

  private IRegistry<ElementKey, Object> elementRegistry = new HashMapRegistry();

  private long maxElementID;

  public IFactory[] getFactories()
  {
    return factoryRegistry.values().toArray(new IFactory[factoryRegistry.size()]);
  }

  public IFactory[] getFactories(String productGroup)
  {
    List<IFactory> result = new ArrayList();
    for (IFactory factory : factoryRegistry.values())
    {
      if (ObjectUtil.equals(factory.getProductGroup(), productGroup))
      {
        result.add(factory);
      }
    }

    return result.toArray(new IFactory[result.size()]);
  }

  public IFactory getFactory(String productGroup, String factoryType)
  {
    FactoryKey key = new FactoryKey(productGroup, factoryType);
    return factoryRegistry.get(key);
  }

  public void registerFactory(IFactory factory)
  {
    FactoryKey key = new FactoryKey(factory.getProductGroup(), factory.getType());
    factoryRegistry.put(key, factory);
  }

  public void deregisterFactory(IFactory factory)
  {
    FactoryKey key = new FactoryKey(factory.getProductGroup(), factory.getType());
    factoryRegistry.remove(key);
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
          key.setID(++maxElementID);
          elementRegistry.put(key, element);
          fireEvent(new SingleDeltaContainerEvent(this, element, IContainerDelta.Kind.ADDED));
        }
      }

      return element;
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

  protected Object createElement(String productGroup, String factoryType, String description)
  {
    IFactory factory = getFactory(productGroup, factoryType);
    if (factory != null)
    {
      return factory.create(description);
    }

    return null;
  }

  protected void initMaxElementID()
  {
    synchronized (elementRegistry)
    {
      maxElementID = 0;
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
