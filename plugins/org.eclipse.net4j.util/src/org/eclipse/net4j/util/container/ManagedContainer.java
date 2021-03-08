/*
 * Copyright (c) 2008, 2009, 2011-2013, 2016, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - bug 399641: container-aware factories
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A default implementation of a {@link IManagedContainer managed container}.
 *
 * @author Eike Stepper
 */
public class ManagedContainer extends Lifecycle implements IManagedContainer
{
  private String name;

  private IRegistry<IFactoryKey, IFactory> factoryRegistry;

  private List<IElementProcessor> postProcessors;

  private IRegistry<ElementKey, Object> elementRegistry = new HashMapRegistry<>();

  @ExcludeFromDump
  private transient long maxElementID;

  @ExcludeFromDump
  private transient IListener elementListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      for (Entry<ElementKey, Object> entry : getElementRegistryEntries())
      {
        Object value = entry.getValue();
        if (lifecycle == value)
        {
          ElementKey key = entry.getKey();
          removeElement(key);
          return;
        }
      }
    }
  };

  public ManagedContainer()
  {
  }

  /**
   * @since 3.8
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * @since 3.8
   */
  @Override
  public void setName(String name)
  {
    checkInactive();
    this.name = name;
  }

  @Override
  public synchronized IRegistry<IFactoryKey, IFactory> getFactoryRegistry()
  {
    if (factoryRegistry == null)
    {
      factoryRegistry = createFactoryRegistry();
      factoryRegistry.addListener(new ContainerEventAdapter<Map.Entry<IFactoryKey, IFactory>>()
      {
        @Override
        protected void onAdded(IContainer<Map.Entry<IFactoryKey, IFactory>> container, Map.Entry<IFactoryKey, IFactory> entry)
        {
          updateFactory(entry, ManagedContainer.this);
        }

        @Override
        protected void onRemoved(IContainer<Map.Entry<IFactoryKey, IFactory>> container, Map.Entry<IFactoryKey, IFactory> entry)
        {
          updateFactory(entry, null);
        }

        private void updateFactory(Map.Entry<IFactoryKey, IFactory> entry, IManagedContainer container)
        {
          IFactory factory = entry.getValue();
          if (factory instanceof ContainerAware)
          {
            ContainerAware f = (ContainerAware)factory;
            f.setManagedContainer(container);
          }
        }
      });
    }

    return factoryRegistry;
  }

  @Override
  public ManagedContainer registerFactory(IFactory factory)
  {
    getFactoryRegistry().put(factory.getKey(), factory);
    return this;
  }

  @Override
  public synchronized List<IElementProcessor> getPostProcessors()
  {
    if (postProcessors == null)
    {
      postProcessors = createPostProcessors();
    }

    return postProcessors;
  }

  @Override
  public synchronized void addPostProcessor(IElementProcessor postProcessor, boolean processExistingElements)
  {
    if (processExistingElements)
    {
      ContainerEvent<Object> event = new ContainerEvent<>(this);
      for (Entry<ElementKey, Object> entry : getElementRegistryEntries())
      {
        ElementKey key = entry.getKey();
        Object element = entry.getValue();

        String productGroup = key.getProductGroup();
        String factoryType = key.getFactoryType();
        String description = key.getDescription();
        Object newElement = postProcessor.process(this, productGroup, factoryType, description, element);
        if (newElement != element)
        {
          synchronized (elementRegistry)
          {
            elementRegistry.put(key, newElement);
          }

          event.addDelta(element, IContainerDelta.Kind.REMOVED);
          event.addDelta(newElement, IContainerDelta.Kind.ADDED);
        }
      }

      fireEvent(event);
    }

    getPostProcessors().add(postProcessor);
  }

  @Override
  public void addPostProcessor(IElementProcessor postProcessor)
  {
    addPostProcessor(postProcessor, false);
  }

  @Override
  public void removePostProcessor(IElementProcessor postProcessor)
  {
    getPostProcessors().remove(postProcessor);
  }

  @Override
  public Set<String> getProductGroups()
  {
    checkActive();
    Set<String> result = new HashSet<>();
    for (IFactoryKey key : factoryRegistry.keySet())
    {
      result.add(key.getProductGroup());
    }

    for (ElementKey key : getElementRegistryKeys())
    {
      result.add(key.getProductGroup());
    }

    return result;
  }

  @Override
  public Set<String> getFactoryTypes(String productGroup)
  {
    checkActive();
    Set<String> result = new HashSet<>();
    for (IFactoryKey key : factoryRegistry.keySet())
    {
      if (ObjectUtil.equals(key.getProductGroup(), productGroup))
      {
        result.add(key.getType());
      }
    }

    for (ElementKey key : getElementRegistryKeys())
    {
      if (ObjectUtil.equals(key.getProductGroup(), productGroup))
      {
        result.add(key.getFactoryType());
      }
    }

    return result;
  }

  @Override
  public IFactory getFactory(String productGroup, String factoryType) throws FactoryNotFoundException
  {
    FactoryKey key = new FactoryKey(productGroup, factoryType);
    IFactory factory = getFactoryRegistry().get(key);
    if (factory == null)
    {
      throw new FactoryNotFoundException("Factory not found: " + key); //$NON-NLS-1$
    }

    return factory;
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    synchronized (elementRegistry)
    {
      return elementRegistry.isEmpty();
    }
  }

  @Override
  public String[] getElementKey(Object element)
  {
    checkActive();
    for (Entry<ElementKey, Object> entry : getElementRegistryEntries())
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

  @Override
  public Object[] getElements()
  {
    checkActive();
    return getElementRegistryValues();
  }

  @Override
  public Object[] getElements(String productGroup)
  {
    checkActive();
    List<Object> result = new ArrayList<>();
    for (Entry<ElementKey, Object> entry : getElementRegistryEntries())
    {
      ElementKey key = entry.getKey();
      if (ObjectUtil.equals(key.getProductGroup(), productGroup))
      {
        result.add(entry.getValue());
      }
    }

    return result.toArray();
  }

  @Override
  public Object[] getElements(String productGroup, String factoryType)
  {
    checkActive();
    List<Object> result = new ArrayList<>();
    for (Entry<ElementKey, Object> entry : getElementRegistryEntries())
    {
      ElementKey key = entry.getKey();
      if (ObjectUtil.equals(key.getProductGroup(), productGroup) && ObjectUtil.equals(key.getFactoryType(), factoryType))
      {
        result.add(entry.getValue());
      }
    }

    return result.toArray();
  }

  @Override
  public Object getElement(String productGroup, String factoryType, String description) throws FactoryNotFoundException, ProductCreationException
  {
    return getElement(productGroup, factoryType, description, true);
  }

  /**
   * @since 2.0
   */
  @Override
  public Object getElement(String productGroup, String factoryType, String description, boolean activate)
      throws FactoryNotFoundException, ProductCreationException
  {
    checkActive();
    ElementKey key = new ElementKey(productGroup, factoryType, description);
    Object element;
    synchronized (elementRegistry)
    {
      element = elementRegistry.get(key);
    }

    if (element == null)
    {
      element = createElement(productGroup, factoryType, description);
      element = postProcessElement(productGroup, factoryType, description, element);

      if (activate)
      {
        activateElement(element);
      }

      putElement(key, element);
    }

    return element;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getElementOrNull(String productGroup, String factoryType, String description)
  {
    try
    {
      return (T)getElement(productGroup, factoryType, description);
    }
    catch (FactoryNotFoundException | ProductCreationException ex)
    {
      return null;
    }
  }

  /**
   * @since 3.2
   */
  protected void activateElement(Object element)
  {
    EventUtil.addUniqueListener(element, elementListener);
    LifecycleUtil.activate(element);

    boolean active;
    if (LifecycleUtil.isDeferredActivation(element))
    {
      active = LifecycleUtil.waitForActive(element, 10 * 1000);
    }
    else
    {
      active = LifecycleUtil.isActive(element);
    }

    if (!active)
    {
      EventUtil.removeListener(element, elementListener);
      throw new LifecycleException("Could not activate " + element);
    }
  }

  @Override
  public Object putElement(String productGroup, String factoryType, String description, Object element)
  {
    checkActive();
    element = postProcessElement(productGroup, factoryType, description, element);

    ElementKey key = new ElementKey(productGroup, factoryType, description);
    return putElement(key, element);
  }

  protected Object putElement(ElementKey key, Object element)
  {
    ContainerEvent<Object> event = new ContainerEvent<>(this);
    Object oldElement;
    synchronized (elementRegistry)
    {
      key.setID(++maxElementID);
      oldElement = elementRegistry.put(key, element);
    }

    if (oldElement != element)
    {
      if (element instanceof ContainerAware)
      {
        ((ContainerAware)element).setManagedContainer(this);
      }

      EventUtil.addUniqueListener(element, elementListener);

      if (oldElement != null)
      {
        EventUtil.removeListener(oldElement, elementListener);
        event.addDelta(oldElement, IContainerDelta.Kind.REMOVED);
      }

      event.addDelta(element, IContainerDelta.Kind.ADDED);
      fireEvent(event);
    }

    return oldElement;
  }

  @Override
  public Object removeElement(String productGroup, String factoryType, String description)
  {
    checkActive();
    ElementKey key = new ElementKey(productGroup, factoryType, description);
    return removeElement(key);
  }

  protected Object removeElement(ElementKey key)
  {
    Object element;
    synchronized (elementRegistry)
    {
      element = elementRegistry.remove(key);
    }

    if (element != null)
    {
      EventUtil.removeListener(element, elementListener);
      fireEvent(new SingleDeltaContainerEvent<>(this, element, IContainerDelta.Kind.REMOVED));

      if (element instanceof ContainerAware)
      {
        ((ContainerAware)element).setManagedContainer(null);
      }
    }

    return element;
  }

  @Override
  public <T> void forEachElement(String productGroup, Class<T> productType, Function<String, String> descriptionProvider, Consumer<T> consumer)
  {
    for (String type : getFactoryTypes(productGroup))
    {
      String description = descriptionProvider == null ? null : descriptionProvider.apply(type);

      try
      {
        @SuppressWarnings("unchecked")
        T element = (T)getElement(productGroup, type, description);
        consumer.accept(element);
      }
      catch (FactoryNotFoundException ex)
      {
        // Should not happen.
      }
      catch (ProductCreationException ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  @Override
  public <T> void forEachElement(String productGroup, Class<T> productType, String description, Consumer<T> consumer)
  {
    forEachElement(productGroup, productType, type -> description, consumer);
  }

  @Override
  public <T> void forEachElement(String productGroup, Class<T> productType, Consumer<T> consumer)
  {
    forEachElement(productGroup, productType, type -> null, consumer);
  }

  @Override
  public void clearElements()
  {
    checkActive();
    ContainerEvent<Object> event = null;
    synchronized (elementRegistry)
    {
      if (!elementRegistry.isEmpty())
      {
        event = new ContainerEvent<>(this);
        for (Object element : elementRegistry.values())
        {
          EventUtil.removeListener(element, elementListener);
          event.addDelta(element, IContainerDelta.Kind.REMOVED);
        }

        elementRegistry.clear();
      }
    }

    if (event != null)
    {
      fireEvent(event);
    }
  }

  @Override
  public void loadElements(InputStream stream) throws IOException, FactoryNotFoundException, ProductCreationException
  {
    checkActive();
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
  }

  @Override
  public void saveElements(OutputStream stream) throws IOException
  {
    checkActive();
    synchronized (elementRegistry)
    {
      ObjectOutputStream oos = new ObjectOutputStream(stream);
      List<Entry<ElementKey, Object>> entries = new ArrayList<>(elementRegistry.entrySet());
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
  public void fireEvent(IEvent event)
  {
    if (event instanceof IContainerEvent<?>)
    {
      @SuppressWarnings("unchecked")
      IContainerEvent<Object> e = (IContainerEvent<Object>)event;
      if (e.isEmpty())
      {
        return;
      }
    }

    super.fireEvent(event);
  }

  @Override
  public String toString()
  {
    if (name != null)
    {
      return getTypeName() + "[" + name + "]";
    }

    return getTypeName();
  }

  /**
   * @since 3.8
   */
  protected String getTypeName()
  {
    return "ManagedContainer"; //$NON-NLS-1$
  }

  protected IRegistry<IFactoryKey, IFactory> createFactoryRegistry()
  {
    return new HashMapRegistry<>();
  }

  protected List<IElementProcessor> createPostProcessors()
  {
    return new ArrayList<>();
  }

  /**
   * @since 2.0
   */
  protected ElementKey[] getElementRegistryKeys()
  {
    synchronized (elementRegistry)
    {
      return elementRegistry.keySet().toArray(new ElementKey[elementRegistry.size()]);
    }
  }

  /**
   * @since 2.0
   */
  protected Object[] getElementRegistryValues()
  {
    synchronized (elementRegistry)
    {
      return elementRegistry.values().toArray(new Object[elementRegistry.size()]);
    }
  }

  /**
   * @since 2.0
   */
  @SuppressWarnings("unchecked")
  protected Entry<ElementKey, Object>[] getElementRegistryEntries()
  {
    synchronized (elementRegistry)
    {
      return elementRegistry.entrySet().toArray(new Entry[elementRegistry.size()]);
    }
  }

  protected Object createElement(String productGroup, String factoryType, String description) throws FactoryNotFoundException, ProductCreationException
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

  private void initMaxElementID()
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
    for (Object element : getElementRegistryValues())
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
    elementRegistry.clear();
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
      return MessageFormat.format("{0}[{1}, {2}]", productGroup, factoryType, description); //$NON-NLS-1$
    }

    @Override
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
    @Override
    public int compare(Entry<ElementKey, Object> entry1, Entry<ElementKey, Object> entry2)
    {
      return entry1.getKey().compareTo(entry2.getKey());
    }
  }
}
