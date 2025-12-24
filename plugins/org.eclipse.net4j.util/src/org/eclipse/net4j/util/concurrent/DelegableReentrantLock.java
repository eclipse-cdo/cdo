/*
 * Copyright (c) 2016, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A reentrant lock that can be delegated to other threads as detected by {@link DelegateDetector delegate detectors}.
 * <p>
 * Delegate detectors can be registered programmatically or discovered in a {@link IManagedContainer managed container}.
 * The {@link IPluginContainer plugin container} is used by default.
 * <p>
 * This class implements {@link ILifecycle} and must be {@link #activate() activated} before use. As a consequence, it
 * also implements {@link INotifier}, but it does not send any events.
 *
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
      addDelegateDetectorObject(element);
    }

    @Override
    protected void onRemoved(IContainer<Object> container, Object element)
    {
      removeDelegateDetectorObject(element);
    }
  };

  private final List<DelegateDetector> delegateDetectors = new CopyOnWriteArrayList<>();

  private volatile boolean active;

  /**
   * Constructs a delegable reentrant lock that uses the given {@link IManagedContainer managed container} to discover
   * {@link DelegateDetector delegate detectors}. If the given container is <code>null</code>, no container is
   * used.
   */
  public DelegableReentrantLock(IManagedContainer container)
  {
    this.container = container;
  }

  /**
   * Constructs a delegable reentrant lock that uses the {@link IPluginContainer plugin container} to discover
   * {@link DelegateDetector delegate detectors} if the given flag is <code>true</code>. Uses no container otherwise.
   *
   * @since 3.29
   */
  public DelegableReentrantLock(boolean usePluginContainer)
  {
    this(usePluginContainer ? IPluginContainer.INSTANCE : null);
  }

  /**
   * Constructs a delegable reentrant lock that uses the {@link IPluginContainer plugin container} to discover
   * {@link DelegateDetector delegate detectors}.
   */
  public DelegableReentrantLock()
  {
    this(IPluginContainer.INSTANCE);
  }

  /**
   * @since 3.29
   */
  public final void addDelegateDetector(DelegateDetector delegateDetector)
  {
    addDelegateDetectorObject(delegateDetector);
  }

  /**
   * @since 3.29
   */
  public final void removeDelegateDetector(DelegateDetector delegateDetector)
  {
    removeDelegateDetectorObject(delegateDetector);
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

      if (container != null)
      {
        for (Object element : container.getElements(DelegateDetector.Factory.PRODUCT_GROUP))
        {
          addDelegateDetectorObject(element);
        }

        container.addListener(containerListener);
      }
    }
  }

  @Override
  public final synchronized Exception deactivate()
  {
    if (active)
    {
      try
      {
        if (container != null)
        {
          container.removeListener(containerListener);
        }

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

  private void addDelegateDetectorObject(Object element)
  {
    if (element instanceof DelegateDetector)
    {
      DelegateDetector delegateDetector = (DelegateDetector)element;
      delegateDetectors.add(delegateDetector);
    }
  }

  private final void removeDelegateDetectorObject(Object element)
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
      public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.concurrent.delegateDetectors"; //$NON-NLS-1$

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract DelegateDetector create(String description) throws ProductCreationException;
    }
  }
}
