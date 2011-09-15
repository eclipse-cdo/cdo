/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.team;

import org.eclipse.emf.cdo.internal.team.bundle.OM;
import org.eclipse.emf.cdo.team.IRepositoryManager;
import org.eclipse.emf.cdo.team.IRepositoryProject;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RepositoryManager extends Container<IRepositoryProject> implements IRepositoryManager,
    IResourceChangeListener
{
  public static final RepositoryManager INSTANCE = new RepositoryManager();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, RepositoryManager.class);

  private Map<IProject, RepositoryProject> map = new HashMap<IProject, RepositoryProject>();

  public RepositoryManager()
  {
  }

  public IRepositoryProject addElement(IProject project)
  {
    RepositoryProject element = new RepositoryProject(project);
    synchronized (map)
    {
      map.put(project, element);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Added repository for project {0}", project.getName()); //$NON-NLS-1$
    }

    fireElementAddedEvent(element);
    return element;
  }

  public void removeElement(IProject project)
  {
    RepositoryProject element = null;
    synchronized (map)
    {
      element = map.remove(project);
    }

    if (element != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removed repository for project {0}", project.getName()); //$NON-NLS-1$
      }

      fireElementRemovedEvent(element);
      element.dispose();
    }
  }

  public RepositoryProject getElement(IProject project)
  {
    synchronized (map)
    {
      return map.get(project);
    }
  }

  public RepositoryProject[] getElements()
  {
    synchronized (map)
    {
      return map.values().toArray(new RepositoryProject[map.size()]);
    }
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (map)
    {
      return map.isEmpty();
    }
  }

  public void resourceChanged(IResourceChangeEvent event)
  {
    IResourceDelta delta = event.getDelta();
    if (delta != null)
    {
      for (IResourceDelta child : delta.getAffectedChildren())
      {
        if (child instanceof IProject)
        {
          IProject project = (IProject)child;
          switch (delta.getKind())
          {
          case IResourceDelta.OPEN:
            resourceChangedOpen(project);
            break;
          }
        }
      }
    }
  }

  private void resourceChangedOpen(IProject project)
  {
    if (project.isOpen())
    {
      if (RepositoryTeamProvider.isMapped(project))
      {
        addElement(project);
      }
    }
    else
    {
      removeElement(project);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    for (IProject project : root.getProjects())
    {
      if (RepositoryTeamProvider.isMapped(project))
      {
        addElement(project);
      }
    }
  }
}
