/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
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
 * @author Eike Stepper
 * @since 4.4
 */
public class CDOTreeExpansionAgent
{
  private final Listener listener = new Listener();

  private final Set<CDOID> expandedIDs = new HashSet<CDOID>();

  private final CDOView view;

  private final TreeViewer viewer;

  public CDOTreeExpansionAgent(final CDOView view, final TreeViewer viewer)
  {
    CheckUtil.checkArg(view, "view");
    CheckUtil.checkArg(viewer, "viewer");

    this.view = view;
    this.viewer = viewer;

    final Tree tree = viewer.getTree();
    if (!tree.isDisposed())
    {
      tree.getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          for (Object element : viewer.getExpandedElements())
          {
            CDOID id = getID(element);
            if (id != null)
            {
              expandedIDs.add(id);
            }
          }

          view.addListener(listener);
          viewer.addTreeListener(listener);
          viewer.getTree().addDisposeListener(listener);
        }
      });
    }
  }

  public void setExpandedStates()
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

  public void dispose()
  {
    Tree tree = viewer.getTree();
    if (!tree.isDisposed())
    {
      tree.removeDisposeListener(listener);
    }

    viewer.removeTreeListener(listener);
    view.removeListener(listener);
    expandedIDs.clear();
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

  /**
   * @author Eike Stepper
   */
  private final class Listener implements IListener, ITreeViewerListener, DisposeListener
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewTargetChangedEvent)
      {
        final Tree tree = viewer.getTree();
        if (!tree.isDisposed())
        {
          tree.getDisplay().asyncExec(new Runnable()
          {
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

    public void treeExpanded(TreeExpansionEvent event)
    {
      CDOID id = getID(event.getElement());
      if (id != null)
      {
        expandedIDs.add(id);
      }
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
      CDOID id = getID(event.getElement());
      if (id != null)
      {
        expandedIDs.remove(id);
      }
    }

    public void widgetDisposed(DisposeEvent e)
    {
      dispose();
    }
  }
}
