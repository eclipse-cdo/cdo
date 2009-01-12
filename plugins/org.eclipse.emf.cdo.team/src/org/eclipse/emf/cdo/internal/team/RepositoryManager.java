/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.team;

import org.eclipse.emf.cdo.team.IRepositoryManager;
import org.eclipse.emf.cdo.team.IRepositoryProject;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.container.Container;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RepositoryManager extends Container<IRepositoryProject> implements IRepositoryManager,
    IResourceChangeListener
{
  public static final RepositoryManager INSTANCE = new RepositoryManager();

  private Map<IProject, IRepositoryProject> map = new HashMap<IProject, IRepositoryProject>();

  public RepositoryManager()
  {
  }

  public IRepositoryProject getElement(IProject project)
  {
    synchronized (map)
    {
      return map.get(project);
    }
  }

  public IRepositoryProject[] getElements()
  {
    synchronized (map)
    {
      return map.values().toArray(new IRepositoryProject[map.size()]);
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
  }

  protected IRepositoryProject addElement(IProject project, CDOView view)
  {
    IRepositoryProject element = new RepositoryProject(project, view);
    synchronized (map)
    {
      map.put(project, element);
    }

    fireElementAddedEvent(element);
    return element;
  }
}
