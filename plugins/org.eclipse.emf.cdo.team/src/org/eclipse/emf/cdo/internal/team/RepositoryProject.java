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

import org.eclipse.emf.cdo.team.IRepositoryProject;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Eike Stepper
 */
public class RepositoryProject extends PlatformObject implements IRepositoryProject, IListener
{
  private IProject project;

  private CDOView view;

  public RepositoryProject(IProject project)
  {
    this.project = project;
  }

  public IProject getProject()
  {
    return project;
  }

  public CDOView getView()
  {
    return view;
  }

  public void notifyEvent(IEvent event)
  {
  }
}
