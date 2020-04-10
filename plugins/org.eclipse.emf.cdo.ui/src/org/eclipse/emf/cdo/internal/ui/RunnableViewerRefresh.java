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
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.IViewerNotification;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider.ViewerRefresh;

import org.eclipse.jface.viewers.Viewer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class RunnableViewerRefresh extends ViewerRefresh
{
  private final Viewer viewer;

  public RunnableViewerRefresh(Viewer viewer)
  {
    super(viewer);
    this.viewer = viewer;
  }

  public boolean addNotification(Object element, boolean contentRefresh, boolean labelUpdate)
  {
    return addNotification(element, contentRefresh, labelUpdate, null);
  }

  public boolean addNotification(Object element, boolean contentRefresh, boolean labelUpdate, Runnable runnable)
  {
    if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed())
    {
      IViewerNotification notification;

      if (runnable != null)
      {
        RunnableViewerNotification n = new RunnableViewerNotification(element, contentRefresh, labelUpdate);
        n.getRunnables().add(runnable);
        notification = n;
      }
      else
      {
        notification = new ViewerNotification(null, element, contentRefresh, labelUpdate);
      }

      if (addNotification(notification))
      {
        viewer.getControl().getDisplay().asyncExec(this);
        return true;
      }
    }

    return false;
  }

  @Override
  protected IViewerNotification merge(IViewerNotification n1, IViewerNotification n2)
  {
    List<Runnable> runnables = null;
    if (n1 instanceof RunnableViewerNotification)
    {
      RunnableViewerNotification n = (RunnableViewerNotification)n1;
      List<Runnable> list = n.getRunnables();
      if (!list.isEmpty())
      {
        runnables = new ArrayList<>(list);
      }
    }

    if (n2 instanceof RunnableViewerNotification)
    {
      RunnableViewerNotification n = (RunnableViewerNotification)n2;
      List<Runnable> list = n.getRunnables();
      if (!list.isEmpty())
      {
        if (runnables == null)
        {
          runnables = new ArrayList<>(list);
        }
        else
        {
          runnables.addAll(list);
        }
      }
    }

    IViewerNotification result = super.merge(n1, n2);

    if (result != null && runnables != null)
    {
      if (result instanceof RunnableViewerNotification)
      {
        RunnableViewerNotification n = (RunnableViewerNotification)result;
        List<Runnable> list = n.getRunnables();
        list.clear();
        list.addAll(runnables);
      }
      else
      {
        RunnableViewerNotification newResult = new RunnableViewerNotification(result.getElement(), result.isContentRefresh(), result.isLabelUpdate());
        newResult.getRunnables().addAll(runnables);
        result = newResult;
      }
    }

    return result;
  }

  @Override
  protected void refresh(IViewerNotification notification)
  {
    super.refresh(notification);

    if (notification instanceof RunnableViewerNotification)
    {
      RunnableViewerNotification n = (RunnableViewerNotification)notification;
      n.run();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RunnableViewerNotification implements IViewerNotification, Runnable
  {
    private final List<Runnable> runnables = new ArrayList<>();

    private final Object element;

    private final boolean contentRefresh;

    private final boolean labelUpdate;

    public RunnableViewerNotification(Object element, boolean contentRefresh, boolean labelUpdate)
    {
      this.element = element;
      this.contentRefresh = contentRefresh;
      this.labelUpdate = labelUpdate;
    }

    @Override
    public int getEventType()
    {
      return Notification.SET;
    }

    @Override
    public Object getNotifier()
    {
      return element;
    }

    @Override
    public int getFeatureID(Class<?> expectedClass)
    {
      return 0;
    }

    @Override
    public Object getFeature()
    {
      return null;
    }

    @Override
    public Object getOldValue()
    {
      return null;
    }

    @Override
    public Object getNewValue()
    {
      return null;
    }

    @Override
    public boolean wasSet()
    {
      return false;
    }

    @Override
    public boolean isTouch()
    {
      return false;
    }

    @Override
    public boolean isReset()
    {
      return false;
    }

    @Override
    public int getPosition()
    {
      return 0;
    }

    @Override
    public boolean merge(Notification notification)
    {
      return false;
    }

    @Override
    public boolean getOldBooleanValue()
    {
      return false;
    }

    @Override
    public boolean getNewBooleanValue()
    {
      return false;
    }

    @Override
    public byte getOldByteValue()
    {
      return 0;
    }

    @Override
    public byte getNewByteValue()
    {
      return 0;
    }

    @Override
    public char getOldCharValue()
    {
      return 0;
    }

    @Override
    public char getNewCharValue()
    {
      return 0;
    }

    @Override
    public double getOldDoubleValue()
    {
      return 0;
    }

    @Override
    public double getNewDoubleValue()
    {
      return 0;
    }

    @Override
    public float getOldFloatValue()
    {
      return 0;
    }

    @Override
    public float getNewFloatValue()
    {
      return 0;
    }

    @Override
    public int getOldIntValue()
    {
      return 0;
    }

    @Override
    public int getNewIntValue()
    {
      return 0;
    }

    @Override
    public long getOldLongValue()
    {
      return 0;
    }

    @Override
    public long getNewLongValue()
    {
      return 0;
    }

    @Override
    public short getOldShortValue()
    {
      return 0;
    }

    @Override
    public short getNewShortValue()
    {
      return 0;
    }

    @Override
    public String getOldStringValue()
    {
      return null;
    }

    @Override
    public String getNewStringValue()
    {
      return null;
    }

    @Override
    public Object getElement()
    {
      return element;
    }

    @Override
    public boolean isContentRefresh()
    {
      return contentRefresh;
    }

    @Override
    public boolean isLabelUpdate()
    {
      return labelUpdate;
    }

    public List<Runnable> getRunnables()
    {
      return runnables;
    }

    @Override
    public void run()
    {
      for (Runnable runnable : runnables)
      {
        runnable.run();
      }
    }
  }
}
