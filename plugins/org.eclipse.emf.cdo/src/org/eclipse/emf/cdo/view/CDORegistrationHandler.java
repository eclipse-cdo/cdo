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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * Call-back handler used by {@link CDOView views} to tell implementors of this interface about registrations and deregistrations
 * of {@link CDOObject objects} with {@link CDOView views}.
 *
 * @author Eike Stepper
 * @since 4.6
 * @see CDOView#addRegistrationHandler(CDORegistrationHandler)
 * @see CDOView#removeRegistrationHandler(CDORegistrationHandler)
 * @see CDOView#isObjectRegistered(org.eclipse.emf.cdo.common.id.CDOID)
 */
public interface CDORegistrationHandler
{
  public void objectRegistered(CDOView view, CDOObject object);

  public void objectDeregistered(CDOView view, CDOObject object);

  public void objectCollected(CDOView view, CDOID id);

  /**
   * A concrete default implementation of {@link CDORegistrationHandler}.
   *
   * @author Eike Stepper
   */
  public static class Default implements CDORegistrationHandler
  {
    private final IListener deactivateListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        dispose();
      }
    };

    private CDOView view;

    public Default(CDOView view)
    {
      view.sync().run(() -> doInitialize(view));

      this.view = view;
    }

    public final CDOView getView()
    {
      return view;
    }

    public final boolean isDisposed()
    {
      return view == null;
    }

    public synchronized void dispose()
    {
      if (view != null)
      {
        final CDOView finalView = view;
        view = null;

        finalView.sync().run(() -> doDispose(finalView));
      }
    }

    /**
     * Called by the view for each registered object.
     * <p>
     * Also called during initialization for already loaded objects;
     * {@link #isDisposed()} returns <code>true</code> in these cases.
     */
    @Override
    public void objectRegistered(CDOView view, CDOObject object)
    {
      // Subclasses may override.
    }

    /**
     * Called by the view for each deregistered object.
     * <p>
     * Also called during dispose for already loaded objects;
     * {@link #isDisposed()} returns <code>true</code> in these cases.
     */
    @Override
    public void objectDeregistered(CDOView view, CDOObject object)
    {
      // Subclasses may override.
    }

    /**
     * Called by the view for each garbage-collected object.
     */
    @Override
    public void objectCollected(CDOView view, CDOID id)
    {
      // Subclasses may override.
    }

    protected void doInitialize(CDOView view)
    {
      if (LifecycleUtil.isActive(view))
      {
        for (InternalCDOObject object : ((InternalCDOView)view).getObjects().values())
        {
          objectRegistered(view, object);
        }
      }

      view.addRegistrationHandler(this);
      view.addListener(deactivateListener);
    }

    protected void doDispose(CDOView view)
    {
      view.removeListener(deactivateListener);
      view.removeRegistrationHandler(this);

      if (LifecycleUtil.isActive(view))
      {
        for (InternalCDOObject object : ((InternalCDOView)view).getObjects().values())
        {
          objectDeregistered(view, object);
        }
      }
    }
  }
}
