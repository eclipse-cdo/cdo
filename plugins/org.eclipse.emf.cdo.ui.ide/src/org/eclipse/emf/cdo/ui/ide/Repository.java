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
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.resources.IProject;

/**
 * @author Eike Stepper
 */
public final class Repository implements IListener
{
  private IProject project;

  private CDOView view;

  private Content[] roots = { new SessionsContent(), new PackagesContent(), new ResourcesContent() };

  public Repository(IProject project, CDOView view)
  {
    this.project = project;
    this.view = view;
  }

  public IProject getProject()
  {
    return project;
  }

  public CDOView getView()
  {
    return view;
  }

  public Object[] getRoots()
  {
    return roots;
  }

  public void notifyEvent(IEvent event)
  {
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Content
  {
    public abstract String getText();

    public abstract String getImageKey();

    public IProject getParent()
    {
      return getProject();
    }

    public Object[] getChildren()
    {
      return RepositoryContentProvider.EMPTY;
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class SessionsContent extends Content
  {
    @Override
    public String getText()
    {
      return "Sessions";
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Sessions.gif";
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class PackagesContent extends Content
  {
    @Override
    public String getText()
    {
      return "Packages";
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Packages.gif";
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class ResourcesContent extends Content
  {
    @Override
    public String getText()
    {
      return "Resources";
    }

    @Override
    public String getImageKey()
    {
      return "icons/full/obj16/Resources.gif";
    }
  }
}
