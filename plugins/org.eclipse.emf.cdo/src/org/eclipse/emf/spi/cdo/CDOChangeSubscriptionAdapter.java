/*
 * Copyright (c) 2010-2012, 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An adapter for registering selected {@link CDOObject}s for change notifications in a {@link CDOView}. When attached
 * to an object, it makes the view's change-subscription policy accept that object; the adapter tracks those notifiers
 * so they can be detached together when the subscription is reset or disposed.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOChangeSubscriptionAdapter extends AdapterImpl
{
  private CDOView view;

  private CDOAdapterPolicy policy = new CDOAdapterPolicy()
  {
    @Override
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return adapter == CDOChangeSubscriptionAdapter.this;
    }
  };

  private Set<CDOObject> notifiers = new HashSet<>();

  public CDOChangeSubscriptionAdapter(CDOView view)
  {
    this.view = view;
    view.options().addChangeSubscriptionPolicy(policy);
  }

  public void dispose()
  {
    reset();
    view.options().removeChangeSubscriptionPolicy(policy);
    view = null;
  }

  public CDOView getView()
  {
    return view;
  }

  public Set<CDOObject> getNotifiers()
  {
    return notifiers;
  }

  public void attach(CDOObject notifier)
  {
    if (notifiers.contains(notifier))
    {
      return;
    }

    EList<Adapter> adapters = notifier.eAdapters();
    boolean wasAttached = adapters.contains(this);

    if (!notifiers.add(notifier))
    {
      return;
    }

    try
    {
      adapters.add(this);

      if (!adapters.contains(this))
      {
        notifiers.remove(notifier);
      }
    }
    catch (RuntimeException | Error failure)
    {
      if (!wasAttached)
      {
        try
        {
          if (adapters.contains(this))
          {
            adapters.remove(this);
          }
        }
        catch (RuntimeException | Error cleanupFailure)
        {
          if (cleanupFailure != failure)
          {
            failure.addSuppressed(cleanupFailure);
          }
        }
      }

      try
      {
        if (!adapters.contains(this))
        {
          notifiers.remove(notifier);
        }
      }
      catch (RuntimeException | Error inspectionFailure)
      {
        if (inspectionFailure != failure)
        {
          failure.addSuppressed(inspectionFailure);
        }
      }

      throw failure;
    }
  }

  public void reset()
  {
    for (Iterator<CDOObject> it = notifiers.iterator(); it.hasNext();)
    {
      CDOObject notifier = it.next();
      boolean attached = true;
      boolean inspectionFailed = false;
      Error failure = null;
      EList<Adapter> adapters = null;

      try
      {
        if (notifier != null)
        {
          adapters = notifier.eAdapters();
        }
      }
      catch (Exception ex)
      {
        inspectionFailed = true;
      }

      if (notifier == null || !inspectionFailed && adapters == null)
      {
        attached = false;
      }
      else if (!inspectionFailed)
      {
        try
        {
          adapters.remove(this);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
        catch (Error ex)
        {
          failure = ex;
        }

        try
        {
          attached = adapters.contains(this);
        }
        catch (Exception ex)
        {
          if (failure != null)
          {
            failure.addSuppressed(ex);
          }
        }
        catch (Error ex)
        {
          if (failure == null)
          {
            failure = ex;
          }
          else if (failure != ex)
          {
            failure.addSuppressed(ex);
          }
        }
      }

      if (!attached)
      {
        try
        {
          it.remove();
        }
        catch (RuntimeException | Error ex)
        {
          if (failure == null)
          {
            throw ex;
          }

          if (failure != ex)
          {
            failure.addSuppressed(ex);
          }
        }
      }

      if (failure != null)
      {
        throw failure;
      }
    }
  }
}
