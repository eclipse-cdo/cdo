/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.internal.ui.ItemsProcessor;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;

import org.eclipse.jface.viewers.TreeViewer;

import java.util.HashSet;
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
        IContainerEvent<?> e = (IContainerEvent<?>)event;
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
      if (event instanceof CDOTransactionFinishedEvent)
      {
        // CDOTransactionFinishedEvent e = (CDOTransactionFinishedEvent)event;
        // if (e.getType() == CDOTransactionFinishedEvent.Type.COMMITTED)
        // {
        // Map<CDOID, CDOID> idMappings = e.getIDMappings();
        // HashSet<CDOID> newOIDs = new HashSet<CDOID>(idMappings.values());
        // new ItemsProcessor(view)
        // {
        // @Override
        // protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
        // {
        // viewer.update(cdoObject.cdoInternalInstance(), null);
        // }
        // }.processCDOObjects(treeViewer, newOIDs);
        // }
        // else
        {
          try
          {
            treeViewer.getControl().getDisplay().syncExec(new Runnable()
            {
              public void run()
              {
                try
                {
                  treeViewer.refresh(true);
                }
                catch (Exception ignore)
                {
                }
              }
            });
          }
          catch (Exception ignore)
          {
          }
        }

        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionStartedEvent)
      {
        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionConflictEvent)
      {
        CDOTransactionConflictEvent e = (CDOTransactionConflictEvent)event;
        viewConflict(e.getConflictingObject(), e.isFirstConflict());
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
    treeViewer = viewer;
  }

  protected void sessionInvalidated(Set<CDOIDAndVersion> dirtyOIDs)
  {
    Set<CDOID> idsWithoutVersion = new HashSet<CDOID>();
    for (CDOIDAndVersion idAandVersion : dirtyOIDs)
    {
      idsWithoutVersion.add(idAandVersion.getID());
    }

    new ItemsProcessor(view)
    {
      @Override
      protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
      {
        objectInvalidated(cdoObject);
        viewer.refresh(cdoObject.cdoInternalInstance(), true);
      }
    }.processCDOObjects(treeViewer, idsWithoutVersion);
  }

  protected void objectInvalidated(InternalCDOObject cdoObject)
  {
  }

  protected void viewDirtyStateChanged()
  {
  }

  protected void viewConflict(CDOObject conflictingObject, boolean firstConflict)
  {
  }

  protected void viewClosed()
  {
  }
}
