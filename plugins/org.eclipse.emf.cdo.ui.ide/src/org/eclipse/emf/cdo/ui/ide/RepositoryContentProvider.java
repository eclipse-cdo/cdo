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


import org.eclipse.net4j.util.ui.StructuredContentProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RepositoryContentProvider extends StructuredContentProvider<IWorkspaceRoot> implements
    ITreeContentProvider
{
  static final Object[] EMPTY = {};

  private Map<IProject, Repository> repositories = new HashMap<IProject, Repository>();

  public RepositoryContentProvider()
  {
  }

  public Object[] getChildren(Object parentElement)
  {
    if (parentElement instanceof IProject)
    {
      IProject project = (IProject)parentElement;
      Repository repository = repositories.get(project);
      if (repository == null)
      {
        repository = new Repository(project, null);
        repositories.put(project, repository);
      }

      return repository.getRoots();
    }

    if (parentElement instanceof Repository.Content)
    {
      Repository.Content content = (Repository.Content)parentElement;
      return content.getChildren();
    }

    return EMPTY;
  }

  public boolean hasChildren(Object parentElement)
  {
    Object[] children = getChildren(parentElement);
    return children != null && children.length != 0;
  }

  public Object[] getElements(Object parentElement)
  {
    return getChildren(parentElement);
  }

  public Object getParent(Object element)
  {
    if (element instanceof Repository.Content)
    {
      Repository.Content content = (Repository.Content)element;
      return content.getParent();
    }

    return null;
  }

  @Override
  protected void connectInput(IWorkspaceRoot wsRoot)
  {
    // view.addListener(viewListener);
    // view.getSession().addListener(sessionListener);
  }

  @Override
  protected void disconnectInput(IWorkspaceRoot wsRoot)
  {
    // view.getSession().removeListener(sessionListener);
    // view.removeListener(viewListener);
  }
}
