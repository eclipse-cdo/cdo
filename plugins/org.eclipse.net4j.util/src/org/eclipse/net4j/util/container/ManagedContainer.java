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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
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

  private IRegistry<ElementKey, Object> elementRegistry = new HashMapRegistry<ElementKey, Object>();

  private long maxElementID;

  private IListener elementListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      for (Entry<ElementKey, Object> entry : elementRegistry.entrySet())
      {
        if (lifecycle == entry.getValue())
        {
          removeElement(entry.getKey());
          return;
        }
      }
    }
  };

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

  public synchronized void addPostProcessor(IElementProcessor postProcessor, boolean processExistingElements)
  {
    if (processExistingElements)
    {
      ContainerEvent<Object> event = new ContainerEvent<Object>(this);
      for (Entry<ElementKey, Object> entry : elementRegistry.entrySet())
      {
        ElementKey key = entry.getKey();
        Object element = entry.getValue();

        String productGroup = key.getProductGroup();
        String factoryType = key.getFactoryType();
        String description = key.getDescription();
        Object newElement = postProcessor.process(this, productGroup, factoryType, description, element);
        if (newElement != element)
        {
          elementRegistry.put(key, newElement);
          event.addDelta(element, IContainerDelta.Kind.REMOVED);
          event.addDelta(newElement, IContainerDelta.Kind.ADDED);
        }
      }

      fireEvent(event);
    }

    getPostProcessors().add(postProcessor);
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
    Set<String> result = new HashSet<String>();
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
    Set<String> result = new HashSet<String>();
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

  public IFactory getFactory(String productGroup, String factoryType)
  {
    FactoryKey key = new FactoryKey(productGroup, factoryType);
    IFactory factory = getFactoryRegistry().get(key);
    if (factory == null)
    {
      throw new FactoryNotFoundException("Factory not found: " + key);
    }

    return factory;
  }

  public boolean isEmpty()
  {
    return elementRegistry.isEmpty();
  }

  public String[] getElementKey(Object element)
  {
    for (Entry<ElementKey, Object> entry : elementRegistry.entrySet())
    {
      if (entry.getValue() == element)
      {
        ElementKey key = entry.getKey();
        String[] result = { key.getProductGroup(), key.getFactoryType(), key.getDescription() };
        return result;
      }
    }

    return null;
  }

  public Object[] getElements()
  {
    return elementRegistry.values().toArray();
  }

  public Object[] getElements(String productGroup)
  {
    List<Object> result = new ArrayList<Object>();
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
    List<Object> result = new ArrayList<Object>();
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
    ElementKey key = new ElementKey(productGroup, factoryType, description);
    Object element = elementRegistry.get(key);
    if (element == null)
    {
      element = createElement(productGroup, factoryType, description);
      element = postProcessElement(productGroup, factoryType, description, element);
      LifecycleUtil.activate(element);
      putElement(key, element);
    }

    return element;
  }

  public Object putElement(String productGroup, String factoryType, String description, Object element)
  {
    ElementKey key = new ElementKey(productGroup, factoryType, description);
    return putElement(key, element);
  }

  protected Object putElement(ElementKey key, Object element)
  {
    ContainerEvent<Object> event = new ContainerEvent<Object>(this);
    key.setID(++maxElementID);
    Object oldElement = elementRegistry.put(key, element);
    if (oldElement != null)
    {
      EventUtil.removeListener(oldElement, elementListener);
      event.addDelta(oldElement, IContainerDelta.Kind.REMOVED);
    }

    event.addDelta(element, IContainerDelta.Kind.ADDED);
    fireEvent(event);
    EventUtil.addListener(element, elementListener);
    return oldElement;
  }

  public Object removeElement(String productGroup, String factoryType, String description)
  {
    ElementKey key = new ElementKey(productGroup, factoryType, description);
    return removeElement(key);
  }

  protected Object removeElement(ElementKey key)
  {
    Object element = elementRegistry.remove(key);
    if (element != null)
    {
      EventUtil.removeListener(element, elementListener);
      fireEvent(new SingleDeltaContainerEvent<Object>(this, element, IContainerDelta.Kind.REMOVED));
    }

    return element;
  }

  public void clearElements()
  {
    if (!elementRegistry.isEmpty())
    {
      ContainerEvent<Object> event = new ContainerEvent<Object>(this);
      for (Object element : elementRegistry.values())
      {
        EventUtil.removeListener(element, elementListener);
        event.addDelta(element, IContainerDelta.Kind.REMOVED);
      }

      elementRegistry.clear();
      fireEvent(event);
    }
  }

  public void loadElements(InputStream stream) throws IOException
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
          // TODO Reconsider activation
          LifecycleUtil.activate(element);
        }
      }
      catch (ClassNotFoundException cannotHappen)
      {
      }
    }

    initMaxElementID();
  }

  public void saveElements(OutputStream stream) throws IOException
  {
    ObjectOutputStream oos = new ObjectOutputStream(stream);
    List<Entry<ElementKey, Object>> entries = new ArrayList<Entry<ElementKey, Object>>(elementRegistry.entrySet());
    Collections.sort(entries, new EntryComparator());

    oos.writeInt(entries.size());
    for (Entry<ElementKey, Object> entry : entries)
    {
      oos.writeObject(entry.getKey());
      oos.writeBoolean(LifecycleUtil.isActive(entry.getValue()));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fireEvent(IEvent event)
  {
    if (event instanceof IContainerEvent)
    {
      IContainerEvent<Object> containerEvent = (IContainerEvent<Object>)event;
      if (containerEvent.isEmpty())
      {
        return;
      }
    }

    super.fireEvent(event);
  }

  @Override
  public String toString()
  {
    return "ManagedContainer";
  }

  protected IRegistry<IFactoryKey, IFactory> createFactoryRegistry()
  {
    return new HashMapRegistry<IFactoryKey, IFactory>();
  }

  protected List<IElementProcessor> createPostProcessors()
  {
    return new ArrayList<IElementProcessor>();
  }

  protected Object createElement(String productGroup, String factoryType, String description)
  {
    IFactory factory = getFactory(productGroup, factoryType);
    return factory.create(description);
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
    Collection<Object> values = elementRegistry.values();
    for (Object element : values.toArray())
    {
      try
      {
        LifecycleUtil.deactivateNoisy(element);
        EventUtil.removeListener(element, elementListener);
      }
      catch (RuntimeException ex)
      {
        OM.LOG.warn(ex);
      }
    }

    LifecycleUtil.deactivate(factoryRegistry);
    LifecycleUtil.deactivate(postProcessors);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static final class ElementKey implements Serializable, Comparable<ElementKey>
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
      if (obj == this)
      {
        return true;
      }

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

    public int compareTo(ElementKey key)
    {
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
  }

  /**
   * @author Eike Stepper
   */
  private static final class EntryComparator implements Comparator<Entry<ElementKey, Object>>
  {
    public int compare(Entry<ElementKey, Object> entry1, Entry<ElementKey, Object> entry2)
    {
      return entry1.getKey().compareTo(entry2.getKey());
    }
  }
}
