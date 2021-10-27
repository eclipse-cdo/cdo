/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public class CDOViewSetHandler extends Lifecycle
{
  private final CDOViewSet viewSet;

  private final Adapter viewSetAdapter = new AdapterImpl()
  {
    @Override
    public boolean isAdapterForType(Object type)
    {
      return type == CDOViewSet.class;
    }

    @Override
    public void notifyChanged(Notification msg)
    {
      switch (msg.getEventType())
      {
      case Notification.ADD:
      {
        CDOView view = (CDOView)msg.getNewValue();
        view.addListener(viewListener);
        viewAdded(view);
        break;
      }

      case Notification.REMOVE:
      {
        CDOView view = (CDOView)msg.getOldValue();
        view.removeListener(viewListener);
        viewRemoved(view);
        break;
      }

      default:
        break;
      }
    }
  };

  private final IListener viewListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewTargetChangedEvent)
      {
        CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
        viewChanged(e.getSource(), e.getOldBranchPoint(), e.getBranchPoint());
      }
      else
      {
        notifyViewEvent(event);
      }
    }
  };

  public CDOViewSetHandler(CDOViewSet viewSet)
  {
    this.viewSet = viewSet;
  }

  public CDOViewSetHandler(ResourceSet resourceSet)
  {
    this(CDOUtil.getViewSet(resourceSet));
  }

  public final CDOViewSet getViewSet()
  {
    return viewSet;
  }

  /**
   * Subclasses may override.
   */
  protected void viewAdded(CDOView view)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  protected void viewChanged(CDOView view, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  protected void viewRemoved(CDOView view)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  protected void notifyViewEvent(IEvent event)
  {
    // Do nothing.
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    for (CDOView view : viewSet.getViews())
    {
      viewAdded(view);
    }

    viewSet.eAdapters().add(viewSetAdapter);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    viewSet.eAdapters().remove(viewSetAdapter);

    for (CDOView view : viewSet.getViews())
    {
      viewRemoved(view);
    }

    super.doDeactivate();
  }
}
