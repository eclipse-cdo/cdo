/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface CDOCheckoutContentModifier
{
  public Object[] modifyChildren(Object parent, Object[] children);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.explorer.ui.checkouts.contentModifiers";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract CDOCheckoutContentModifier create(String description) throws ProductCreationException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Registry implements CDOCheckoutContentModifier, IListener
  {
    public static final Registry INSTANCE = new Registry();

    private final Map<String, CDOCheckoutContentModifier> modifiers = new LinkedHashMap<>();

    private Registry()
    {
      IRegistry<IFactoryKey, IFactory> factories = IPluginContainer.INSTANCE.getFactoryRegistry();
      for (Map.Entry<IFactoryKey, IFactory> entry : factories.getElements())
      {
        updateModifiers(entry, IContainerDelta.Kind.ADDED);
      }

      factories.addListener(this);
    }

    private List<CDOCheckoutContentModifier> getModifiers()
    {
      synchronized (modifiers)
      {
        if (modifiers.isEmpty())
        {
          return null;
        }

        return new ArrayList<>(modifiers.values());
      }
    }

    @Override
    public Object[] modifyChildren(Object parent, Object[] children)
    {
      List<CDOCheckoutContentModifier> modifiers = getModifiers();
      if (modifiers != null)
      {
        for (int i = 0; i < children.length; i++)
        {
          Object child = children[i];
          CDOElement.removeFrom(child);
        }

        for (CDOCheckoutContentModifier modifier : modifiers)
        {
          try
          {
            children = modifier.modifyChildren(parent, children);
          }
          catch (ObjectNotFoundException ex)
          {
            // Do nothing
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }

      return children;
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IContainerEvent)
      {
        @SuppressWarnings("unchecked")
        IContainerEvent<Map.Entry<IFactoryKey, IFactory>> e = (IContainerEvent<Map.Entry<IFactoryKey, IFactory>>)event;

        for (IContainerDelta<Map.Entry<IFactoryKey, IFactory>> delta : e.getDeltas())
        {
          Map.Entry<IFactoryKey, IFactory> entry = delta.getElement();
          updateModifiers(entry, delta.getKind());
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          IPluginContainer.INSTANCE.getFactoryRegistry().removeListener(this);

          synchronized (modifiers)
          {
            modifiers.clear();
          }
        }
      }
    }

    private void updateModifiers(Map.Entry<IFactoryKey, IFactory> entry, IContainerDelta.Kind deltaKind)
    {
      try
      {
        IFactoryKey key = entry.getKey();
        if (Factory.PRODUCT_GROUP.equals(key.getProductGroup()))
        {
          String type = key.getType();
          if (deltaKind == IContainerDelta.Kind.ADDED)
          {
            CDOCheckoutContentModifier modifier = (CDOCheckoutContentModifier)IPluginContainer.INSTANCE.getElement(Factory.PRODUCT_GROUP, type, null);

            synchronized (modifiers)
            {
              modifiers.put(type, modifier);
            }
          }
          else
          {
            synchronized (modifiers)
            {
              modifiers.remove(type);
            }
          }
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}
