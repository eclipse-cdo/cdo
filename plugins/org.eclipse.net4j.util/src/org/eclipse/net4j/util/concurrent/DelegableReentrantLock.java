/*
 * Copyright (c) 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class DelegableReentrantLock extends NonFairReentrantLock implements ILifecycle, IManagedContainerProvider
{
  private static final long serialVersionUID = 1L;

  private final IManagedContainer container;

  private final IListener containerListener = new ContainerEventAdapter<Object>()
  {
    @Override
    protected void onAdded(IContainer<Object> container, Object element)
    {
      addDelegateDetector(element);
    }

    @Override
    protected void onRemoved(IContainer<Object> container, Object element)
    {
      removeDelegateDetector(element);
    }
  };

  private final List<DelegateDetector> delegateDetectors = new CopyOnWriteArrayList<>();

  private volatile boolean active;

  public DelegableReentrantLock(IManagedContainer container)
  {
    this.container = container;
  }

  public DelegableReentrantLock()
  {
    this(IPluginContainer.INSTANCE);
  }

  @Override
  public final IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public final synchronized void activate() throws LifecycleException
  {
    if (!active)
    {
      active = true;

      for (Object element : container.getElements(DelegateDetector.Factory.PRODUCT_GROUP))
      {
        addDelegateDetector(element);
      }

      container.addListener(containerListener);
    }
  }

  @Override
  public final synchronized Exception deactivate()
  {
    if (active)
    {
      try
      {
        container.removeListener(containerListener);
        delegateDetectors.clear();
      }
      catch (Exception ex)
      {
        return ex;
      }
      finally
      {
        active = false;
      }
    }

    return null;
  }

  @Override
  public final LifecycleState getLifecycleState()
  {
    return active ? LifecycleState.ACTIVE : LifecycleState.INACTIVE;
  }

  @Override
  public final boolean isActive()
  {
    return active;
  }

  @Override
  public final void addListener(IListener listener)
  {
    // Do nothing
  }

  @Override
  public final void removeListener(IListener listener)
  {
    // Do nothing
  }

  @Override
  public final IListener[] getListeners()
  {
    return EventUtil.NO_LISTENERS;
  }

  @Override
  public final boolean hasListeners()
  {
    return false;
  }

  @Override
  protected boolean isOwner(Thread thread, Thread owner)
  {
    if (super.isOwner(thread, owner))
    {
      return true;
    }

    return isDelegate(thread, owner);
  }

  protected boolean isDelegate(Thread thread, Thread owner)
  {
    for (DelegateDetector delegateDetector : delegateDetectors)
    {
      if (delegateDetector.isDelegate(thread, owner))
      {
        return true;
      }
    }

    return false;
  }

  private void addDelegateDetector(Object element)
  {
    if (element instanceof DelegateDetector)
    {
      DelegateDetector delegateDetector = (DelegateDetector)element;
      delegateDetectors.add(delegateDetector);
    }
  }

  private void removeDelegateDetector(Object element)
  {
    if (element instanceof DelegateDetector)
    {
      DelegateDetector delegateDetector = (DelegateDetector)element;
      delegateDetectors.remove(delegateDetector);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface DelegateDetector
  {
    public boolean isDelegate(Thread thread, Thread owner);

    /**
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.concurrent.delegateDetectors";

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract DelegateDetector create(String description) throws ProductCreationException;
    }
  }
}
