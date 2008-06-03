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
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewResourcesEvent;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.ui.CDOViewHistoryEvent;

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
 */
public final class CDOViewHistory extends Notifier
{
  private CDOView view;

  private Set<Entry> entries = new HashSet<Entry>();

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

  public Entry[] getEntries()
  {
    synchronized (entries)
    {
      return entries.toArray(new Entry[entries.size()]);
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
    Set<Entry> openResources = new HashSet<Entry>();
    for (Resource resource : view.getResourceSet().getResources())
    {
      if (resource instanceof CDOResource)
      {
        CDOResource cdoResource = (CDOResource)resource;
        openResources.add(new Entry(cdoResource.getPath()));
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
    Entry entry = new Entry(resourcePath);
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

    Entry addedEntry;

    public ViewHistoryEvent(Entry addedEntry)
    {
      super(CDOViewHistory.this);
      this.addedEntry = addedEntry;
    }

    public CDOViewHistory getViewHistory()
    {
      return CDOViewHistory.this;
    }

    public Entry getAddedEntry()
    {
      return addedEntry;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOViewHistoryEvent[source={0}, addedEntry={1}]", getSource(), getAddedEntry());
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class Entry implements Comparable<Entry>
  {
    private String resourcePath;

    public Entry(String resourcePath)
    {
      if (resourcePath == null)
      {
        throw new IllegalArgumentException("resourcePath == null");
      }

      this.resourcePath = resourcePath;
    }

    public CDOView getView()
    {
      return view;
    }

    public String getResourcePath()
    {
      return resourcePath;
    }

    public int compareTo(Entry entry)
    {
      return resourcePath.compareTo(entry.resourcePath);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (obj instanceof Entry)
      {
        Entry that = (Entry)obj;
        return view == that.getView() && resourcePath.equals(that.resourcePath);
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return resourcePath.hashCode();
    }

    @Override
    public String toString()
    {
      return resourcePath;
    }
  }
}
