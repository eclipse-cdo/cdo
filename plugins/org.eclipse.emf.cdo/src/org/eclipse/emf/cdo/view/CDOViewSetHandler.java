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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public class CDOViewSetHandler
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

      case InternalCDOViewSet.NOTIFICATION_COMMIT:
      {
        viewSetCommitted();
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
    init(viewSet);
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
  public void dispose()
  {
    viewSet.eAdapters().remove(viewSetAdapter);

    for (CDOView view : viewSet.getViews())
    {
      viewRemoved(view);
    }
  }

  /**
   * Subclasses may override.
   */
  protected void init(CDOViewSet viewSet)
  {
    for (CDOView view : viewSet.getViews())
    {
      viewAdded(view);
    }

    viewSetCommitted();
    viewSet.eAdapters().add(viewSetAdapter);
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

  /**
   * Subclasses may override.
   */
  protected void viewSetCommitted()
  {
    // Do nothing.
  }

  /**
   * @author Eike Stepper
   */
  public static class Transactional extends CDOViewSetHandler
  {
    private Set<CDOView> addedViews;

    private Set<CDOView> changedViews;

    private Set<CDOView> removedViews;

    public Transactional(CDOViewSet viewSet)
    {
      super(viewSet);
    }

    public Transactional(ResourceSet resourceSet)
    {
      super(resourceSet);
    }

    @Override
    protected void init(CDOViewSet viewSet)
    {
      clearSets();
      super.init(viewSet);
    }

    @Override
    protected final void viewAdded(CDOView view)
    {
      synchronized (this)
      {
        addedViews.add(view);
      }
    }

    @Override
    protected final void viewChanged(CDOView view, CDOBranchPoint oldBranchPoint, CDOBranchPoint newBranchPoint)
    {
      synchronized (this)
      {
        changedViews.add(view);
      }
    }

    @Override
    protected final void viewRemoved(CDOView view)
    {
      synchronized (this)
      {
        removedViews.add(view);
      }
    }

    @Override
    protected final void viewSetCommitted()
    {
      Set<CDOView> paramAddedViews;
      Set<CDOView> paramChangedViews;
      Set<CDOView> paramRemovedViews;

      synchronized (this)
      {
        paramAddedViews = addedViews;
        paramChangedViews = changedViews;
        paramRemovedViews = removedViews;

        clearSets();
      }

      viewSetCommitted(paramAddedViews, paramChangedViews, paramRemovedViews);
    }

    /**
     * Subclasses may override.
     */
    protected void viewSetCommitted(Set<CDOView> addedViews, Set<CDOView> changedViews, Set<CDOView> removedViews)
    {
      // Do nothing.
    }

    private void clearSets()
    {
      addedViews = new HashSet<>();
      changedViews = new HashSet<>();
      removedViews = new HashSet<>();
    }
  }
}
