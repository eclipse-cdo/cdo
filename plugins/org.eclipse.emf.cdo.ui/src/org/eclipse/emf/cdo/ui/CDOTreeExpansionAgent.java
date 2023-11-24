/*
 * Copyright (c) 2015, 2019, 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.views.CDOTimeMachineView;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Automatically preserves a {@link TreeViewer tree viewer's} item expansion states across
 * multiple {@link CDOView#setBranchPoint(org.eclipse.emf.cdo.common.branch.CDOBranch, long) target changes}
 * of the underlying {@link CDOView view}.
 * <p>
 * When the {@link CDOBranchPoint target} of the tree viewer's underlying {@link CDOView view} changes, for example
 * during {@link CDOBranch branch} switches or {@link CDOTimeMachineView time travels}, viewer items can become unavailable
 * and are, hence, removed from the tree viewer. These items can become available again during subsequent view target changes.
 * This agent remembers the set of expanded tree items and reestablishes these expansion states when items become available again.
 * <p>
 * Internally this is achieved by registering a {@link ITreeViewerListener} to the tree viewer and by listening to
 * {@link CDOViewTargetChangedEvent CDOViewTargetChangedEvents} of the underlying {@link CDOView view}. This agent disposes of itself
 * when the {@link TreeViewer tree viewer} is disposed or when the {@link CDOView view} is closed.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public class CDOTreeExpansionAgent
{
  private final Listener listener = new Listener();

  private final Set<CDOID> expandedIDs = new HashSet<>();

  private final Set<CDOID> expandedWrappers = new HashSet<>();

  private final CDOView view;

  private final TreeViewer viewer;

  public CDOTreeExpansionAgent(final CDOView view, final TreeViewer viewer)
  {
    CheckUtil.checkArg(view, "view"); //$NON-NLS-1$
    CheckUtil.checkArg(viewer, "viewer"); //$NON-NLS-1$

    this.view = view;
    this.viewer = viewer;

    final Tree tree = viewer.getTree();
    if (!tree.isDisposed())
    {
      tree.getDisplay().syncExec(new Runnable()
      {
        @Override
        public void run()
        {
          for (Object element : viewer.getExpandedElements())
          {
            addExpandedElement(element);
          }

          view.addListener(listener);
          viewer.addTreeListener(listener);
          viewer.getTree().addDisposeListener(listener);
        }
      });
    }
  }

  public final CDOView getView()
  {
    return view;
  }

  public final TreeViewer getViewer()
  {
    return viewer;
  }

  public void setExpandedStates()
  {
    for (CDOID id : expandedIDs)
    {
      try
      {
        CDOObject object = view.getObject(id);
        if (object != null)
        {
          EObject eObject = CDOUtil.getEObject(object);
          viewer.setExpandedState(eObject, true);
        }
      }
      catch (Exception ex)
      {
        // Ignore.
      }
    }

    for (CDOID id : expandedWrappers)
    {
      try
      {
        CDOObject object = view.getObject(id);
        if (object != null)
        {
          CDOElement wrapper = CDOElement.getFor(object);
          if (wrapper != null)
          {
            viewer.setExpandedState(wrapper, true);
          }
        }
      }
      catch (Exception ex)
      {
        // Ignore.
      }
    }
  }

  public void dispose()
  {
    try
    {
      final Tree tree = viewer.getTree();
      if (!tree.isDisposed())
      {
        tree.getDisplay().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            tree.removeDisposeListener(listener);
          }
        });
      }

      viewer.removeTreeListener(listener);
      view.removeListener(listener);
      expandedIDs.clear();
      expandedWrappers.clear();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  private CDOID getID(Object element)
  {
    if (element instanceof EObject)
    {
      CDOObject object = CDOUtil.getCDOObject((EObject)element, false);
      if (object != null && object.cdoView() == view)
      {
        return object.cdoID();
      }
    }

    return null;
  }

  private void addExpandedElement(Object element)
  {
    if (element instanceof CDOElement)
    {
      CDOElement wrapper = (CDOElement)element;

      CDOID id = getID(wrapper.getDelegate());
      if (id != null)
      {
        expandedWrappers.add(id);
      }
    }
    else
    {
      CDOID id = getID(element);
      if (id != null)
      {
        expandedIDs.add(id);
      }
    }
  }

  private void removeExpandedElement(Object element)
  {
    if (element instanceof CDOElement)
    {
      CDOElement wrapper = (CDOElement)element;

      CDOID id = getID(wrapper.getDelegate());
      if (id != null)
      {
        expandedWrappers.remove(id);
      }
    }
    else
    {
      CDOID id = getID(element);
      if (id != null)
      {
        expandedIDs.remove(id);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Listener implements IListener, ITreeViewerListener, DisposeListener
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewTargetChangedEvent)
      {
        final Tree tree = viewer.getTree();
        if (!tree.isDisposed())
        {
          tree.getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              if (!tree.isDisposed() && !view.isClosed())
              {
                setExpandedStates();
              }
            }
          });
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == Kind.ABOUT_TO_DEACTIVATE)
        {
          dispose();
        }
      }
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event)
    {
      Object element = event.getElement();
      addExpandedElement(element);
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event)
    {
      Object element = event.getElement();
      removeExpandedElement(element);
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
      dispose();
    }
  }
}
