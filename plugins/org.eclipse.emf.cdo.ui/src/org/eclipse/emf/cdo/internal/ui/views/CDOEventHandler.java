/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.CDOTransactionCommittedEvent;
import org.eclipse.emf.cdo.CDOTransactionDirtyEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.ItemsProcessor;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.TreeViewer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOEventHandler
{
  private CDOView view;

  private TreeViewer treeViewer;

  private IListener sessionListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
        if (e.getView() != view)
        {
          sessionInvalidated(e.getDirtyOIDs());
        }
      }
      else if (event instanceof IContainerEvent)
      {
        IContainerEvent e = (IContainerEvent)event;
        if (e.getDeltaElement() == view && e.getDeltaKind() == IContainerDelta.Kind.REMOVED)
        {
          viewClosed();
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          viewClosed();
        }
      }
    }
  };

  private IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOTransactionCommittedEvent)
      {
        Map<CDOID, CDOID> idMappings = ((CDOTransactionCommittedEvent)event).getIDMappings();
        HashSet newOIDs = new HashSet(idMappings.values());
        new ItemsProcessor(view)
        {
          @Override
          protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
          {
            viewer.update(cdoObject.cdoInternalInstance(), null);
          }
        }.processCDOObjects(treeViewer, newOIDs);

        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionDirtyEvent)
      {
        viewDirtyStateChanged();
      }
    }
  };

  public CDOEventHandler(CDOView view, TreeViewer treeViewer)
  {
    this.view = view;
    this.treeViewer = treeViewer;
    view.getSession().addListener(sessionListener);
    view.addListener(viewListener);
  }

  public void dispose()
  {
    view.removeListener(viewListener);
    view.getSession().removeListener(sessionListener);
    treeViewer = null;
    view = null;
  }

  public CDOView getView()
  {
    return view;
  }

  public TreeViewer getTreeViewer()
  {
    return treeViewer;
  }

  public void setViewer(TreeViewer viewer)
  {
    this.treeViewer = viewer;
  }

  protected void sessionInvalidated(Set<CDOID> dirtyOIDs)
  {
    new ItemsProcessor(view)
    {
      @Override
      protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
      {
        objectInvalidated(cdoObject);
        viewer.refresh(cdoObject.cdoInternalInstance(), true);
      }
    }.processCDOObjects(treeViewer, dirtyOIDs);
  }

  protected void objectInvalidated(InternalCDOObject cdoObject)
  {
  }

  protected void viewDirtyStateChanged()
  {
  }

  protected void viewClosed()
  {
  }
}
