/*
 * Copyright (c) 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;

import java.util.HashSet;
import java.util.Set;

/**
 * A scale widget that can be {@link #connect(CDOView, TreeViewer) connected} to different {@link CDOView views}
 * and {@link TreeViewer tree viewers} (one such pair at a time) to synchronize the view's {@link CDOView#getTimeStamp() timestamp}
 * with this widget's selection.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public class TimeSlider extends Scale implements IListener, ITreeViewerListener
{
  private static final int MIN = 0;

  private static final int MAX = 100000;

  private static final double FACTOR = MAX - MIN;

  private final Set<CDOID> expandedIDs = new HashSet<>();

  private long startTimeStamp;

  private long endTimeStamp;

  private long absoluteTimeWindowLength;

  private double stepSize;

  private long timeStamp;

  private CDOStaleReferencePolicy.DynamicProxy.Enhanced staleReferencePolicy;

  private CDOView view;

  private TreeViewer viewer;

  public TimeSlider(Composite parent, int style)
  {
    super(parent, style);
    setMinimum(MIN);
    setMaximum(MAX);
    setSelection(MAX);
    setPageIncrement(MAX - MIN);

    addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        long value = getSelection();
        long timeStamp = startTimeStamp + Math.round(stepSize * value);
        setTimeStamp(timeStamp);

        if (viewer != null)
        {
          viewer.refresh();
          setExpandedStates();
        }
      }

      protected void setExpandedStates()
      {
        for (CDOID id : expandedIDs)
        {
          try
          {
            CDOObject object = view.getObject(id);
            viewer.setExpandedState(object, true);
          }
          catch (Exception ex)
          {
            // Ignore
          }
        }
      }
    });
  }

  public final long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      timeStamp = endTimeStamp;
    }
    else if (timeStamp < startTimeStamp)
    {
      timeStamp = startTimeStamp;
    }
    else if (timeStamp > endTimeStamp)
    {
      timeStamp = endTimeStamp;
    }

    if (this.timeStamp != timeStamp)
    {
      this.timeStamp = timeStamp;
      final int newSelection = (int)((timeStamp - startTimeStamp) / stepSize);

      Display display = getDisplay();
      if (display == Display.getCurrent())
      {
        if (getSelection() != newSelection)
        {
          setSelection(newSelection);
        }
      }
      else
      {
        display.asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            if (getSelection() != newSelection)
            {
              setSelection(newSelection);
            }
          }
        });
      }

      timeStampChanged(timeStamp);
    }
  }

  public void connect(CDOView view, TreeViewer viewer)
  {
    if (this.view != view)
    {
      disconnect();

      if (view != null)
      {
        this.view = view;
        this.viewer = viewer;

        CDOBranchPointRange lifetime = null;

        if (this.viewer != null)
        {
          for (Object element : viewer.getExpandedElements())
          {
            CDOID id = getID(element);
            if (id != null)
            {
              expandedIDs.add(id);
            }
          }

          this.viewer.addTreeListener(this);

          Object input = viewer.getInput();
          if (input instanceof EObject)
          {
            lifetime = CDOUtil.getLifetime(CDOUtil.getCDOObject((EObject)input));
          }
        }

        if (lifetime == null)
        {
          CDOSession session = view.getSession();
          CDOBranch branch = view.getBranch();

          CDOBranchPoint firstPoint = branch.getPoint(session.getRepositoryInfo().getCreationTime());
          CDOBranchPoint lastPoint = branch.getHead();
          lifetime = CDOBranchUtil.createRange(firstPoint, lastPoint);
        }

        startTimeStamp = lifetime.getStartPoint().getTimeStamp();
        endTimeStamp = lifetime.getEndPoint().getTimeStamp();
        if (endTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
        {
          CDOSession session = view.getSession();
          endTimeStamp = session.getLastUpdateTime();
        }

        absoluteTimeWindowLength = endTimeStamp - startTimeStamp;
        stepSize = absoluteTimeWindowLength / FACTOR;

        setTimeStamp(view.getTimeStamp());

        staleReferencePolicy = new CDOStaleReferencePolicy.DynamicProxy.Enhanced(view);
        view.addListener(this);
        setEnabled(true);
      }
      else
      {
        setEnabled(false);
      }
    }
    else if (this.view == null)
    {
      setEnabled(false);
    }
  }

  public void disconnect()
  {
    if (view != null)
    {
      if (staleReferencePolicy != null)
      {
        view.removeListener(this);
        staleReferencePolicy.dispose();
        staleReferencePolicy = null;

        if (viewer != null)
        {
          expandedIDs.clear();
          viewer.removeTreeListener(this);
          viewer = null;
        }
      }

      view = null;
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewTargetChangedEvent)
    {
      CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
      setTimeStamp(e.getBranchPoint().getTimeStamp());
    }
    else if (event instanceof ILifecycleEvent)
    {
      ILifecycleEvent e = (ILifecycleEvent)event;
      if (e.getKind() == Kind.ABOUT_TO_DEACTIVATE)
      {
        disconnect();
      }
    }
  }

  @Override
  public void treeExpanded(TreeExpansionEvent event)
  {
    CDOID id = getID(event.getElement());
    if (id != null)
    {
      expandedIDs.add(id);
    }
  }

  @Override
  public void treeCollapsed(TreeExpansionEvent event)
  {
    CDOID id = getID(event.getElement());
    if (id != null)
    {
      expandedIDs.remove(id);
    }
  }

  @Override
  public void dispose()
  {
    disconnect();
    super.dispose();
  }

  protected void timeStampChanged(long timeStamp)
  {
    if (view != null)
    {
      view.setTimeStamp(timeStamp);
    }
  }

  @Override
  protected void checkSubclass()
  {
    // Allow overriding.
  }

  private CDOID getID(Object element)
  {
    if (element instanceof EObject)
    {
      CDOObject object = CDOUtil.getCDOObject((EObject)element, false);
      if (object != null)
      {
        return object.cdoID();
      }
    }

    return null;
  }
}
