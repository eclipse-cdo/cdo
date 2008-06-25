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

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewResourcesEvent;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;

import org.eclipse.emf.ecore.resource.Resource;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public final class CDOViewHistory extends Notifier
{
  private CDOView view;

  private Set<CDOViewHistoryEntry> entries = new HashSet<CDOViewHistoryEntry>();

  private IListener viewListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewResourcesEvent)
      {
        CDOViewResourcesEvent e = (CDOViewResourcesEvent)event;
        if (e.getView() == view && e.getKind() == CDOViewResourcesEvent.Kind.ADDED)
        {
          addResource(e.getResourcePath());
        }
      }
    }
  };

  public CDOViewHistory(CDOView view)
  {
    this.view = view;
    view.addListener(viewListener);
  }

  public void dispose()
  {
    view.removeListener(viewListener);
    entries.clear();
  }

  public CDOView getView()
  {
    return view;
  }

  public CDOViewHistoryEntry[] getEntries()
  {
    synchronized (entries)
    {
      return entries.toArray(new CDOViewHistoryEntry[entries.size()]);
    }
  }

  public boolean hasEntries()
  {
    synchronized (entries)
    {
      return !entries.isEmpty();
    }
  }

  public void reset()
  {
    Set<CDOViewHistoryEntry> openResources = new HashSet<CDOViewHistoryEntry>();
    for (Resource resource : view.getResourceSet().getResources())
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        openResources.add(new CDOViewHistoryEntry(view, cdoResource.getPath()));
      }
    }

    boolean changed;
    synchronized (entries)
    {
      changed = entries.retainAll(openResources);
    }

    if (changed)
    {
      fireEvent(new ViewHistoryEvent(null));
    }
  }

  protected void addResource(String resourcePath)
  {
    CDOViewHistoryEntry entry = new CDOViewHistoryEntry(view, resourcePath);
    boolean changed;
    synchronized (entries)
    {
      changed = entries.add(entry);
    }

    if (changed)
    {
      fireEvent(new ViewHistoryEvent(entry));
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ViewHistoryEvent extends Event implements CDOViewHistoryEvent
  {
    private static final long serialVersionUID = 1L;

    CDOViewHistoryEntry addedEntry;

    public ViewHistoryEvent(CDOViewHistoryEntry addedEntry)
    {
      super(CDOViewHistory.this);
      this.addedEntry = addedEntry;
    }

    public CDOViewHistory getViewHistory()
    {
      return CDOViewHistory.this;
    }

    public CDOViewHistoryEntry getAddedEntry()
    {
      return addedEntry;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOViewHistoryEvent[source={0}, addedEntry={1}]", getSource(), getAddedEntry());
    }
  }
}
