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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewCommittedEvent;
import org.eclipse.emf.cdo.internal.ui.ItemsProcessor;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

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
        Set<CDOID> dirtyOIDs = ((CDOSessionInvalidationEvent)event).getDirtyOIDs();
        new ItemsProcessor()
        {
          @Override
          protected void processCDOObject(TreeViewer viewer, CDOObject cdoObject)
          {
            viewer.refresh(cdoObject, true);
          }
        }.processCDOObjects(treeViewer, dirtyOIDs);
      }
    }
  };

  private IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewCommittedEvent)
      {
        Map<CDOID, CDOID> idMappings = ((CDOViewCommittedEvent)event).getIDMappings();
        HashSet newOIDs = new HashSet(idMappings.values());
        new ItemsProcessor()
        {
          @Override
          protected void processCDOObject(TreeViewer viewer, CDOObject cdoObject)
          {
            viewer.update(cdoObject, null);
          }
        }.processCDOObjects(treeViewer, newOIDs);
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
}
