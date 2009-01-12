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

import org.eclipse.emf.cdo.team.IRepositoryManager;
import org.eclipse.emf.cdo.team.IRepositoryProject;
import org.eclipse.emf.cdo.ui.ide.Node.PackagesNode;
import org.eclipse.emf.cdo.ui.ide.Node.ResourcesNode;
import org.eclipse.emf.cdo.ui.ide.Node.SessionsNode;
import org.eclipse.emf.cdo.ui.ide.Node.Type;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RepositoryContentProvider extends StructuredContentProvider<IWorkspaceRoot> implements
    ITreeContentProvider
{
  private static final Object[] EMPTY = {};

  private Map<IRepositoryProject, Node> sessionNodes = new HashMap<IRepositoryProject, Node>();

  private Map<IRepositoryProject, Node> packageNodes = new HashMap<IRepositoryProject, Node>();

  private Map<IRepositoryProject, Node> resourceNodes = new HashMap<IRepositoryProject, Node>();

  private boolean sessionNodesHidden;

  private boolean packageNodesHidden;

  private boolean resourceNodesHidden;

  private IListener repositoryManagerListener = new ContainerEventAdapter<IRepositoryProject>()
  {
    @Override
    protected void onAdded(IContainer<IRepositoryProject> container, IRepositoryProject element)
    {
      refreshViewer(element);
    }

    @Override
    protected void onRemoved(IContainer<IRepositoryProject> container, IRepositoryProject element)
    {
      refreshViewer(element);
    }

    private void refreshViewer(IRepositoryProject element)
    {
      getViewer().refresh(element.getProject());
    }
  };

  public RepositoryContentProvider()
  {
    IRepositoryManager.INSTANCE.addListener(repositoryManagerListener);
  }

  @Override
  public void dispose()
  {
    IRepositoryManager.INSTANCE.removeListener(repositoryManagerListener);
    super.dispose();
  }

  public boolean isSessionNodesHidden()
  {
    return sessionNodesHidden;
  }

  public void setSessionNodesHidden(boolean sessionNodesHidden)
  {
    this.sessionNodesHidden = sessionNodesHidden;
  }

  public boolean isPackageNodesHidden()
  {
    return packageNodesHidden;
  }

  public void setPackageNodesHidden(boolean packageNodesHidden)
  {
    this.packageNodesHidden = packageNodesHidden;
  }

  public boolean isResourceNodesHidden()
  {
    return resourceNodesHidden;
  }

  public void setResourceNodesHidden(boolean resourceNodesHidden)
  {
    this.resourceNodesHidden = resourceNodesHidden;
  }

  public Object[] getChildren(Object parentElement)
  {
    if (parentElement instanceof IProject)
    {
      IProject project = (IProject)parentElement;
      IRepositoryProject repositoryProject = IRepositoryManager.INSTANCE.getElement(project);
      if (repositoryProject != null)
      {
        return getChildren(repositoryProject);
      }
    }

    if (parentElement instanceof Node)
    {
      Node node = (Node)parentElement;
      return node.getChildren();
    }

    return EMPTY;
  }

  public Object[] getChildren(IRepositoryProject repositoryProject)
  {
    List<Object> children = new ArrayList<Object>();
    Node sessionNode = getNode(repositoryProject, Type.SESSIONS, sessionNodes);
    Node packageNode = getNode(repositoryProject, Type.PACKAGES, packageNodes);
    Node resourceNode = getNode(repositoryProject, Type.RESOURCES, resourceNodes);

    if (!isSessionNodesHidden())
    {
      children.add(sessionNode);
    }

    if (!isPackageNodesHidden())
    {
      children.add(packageNode);
    }

    if (!isResourceNodesHidden())
    {
      children.add(resourceNode);
    }

    if (isSessionNodesHidden())
    {
      addChildren(children, sessionNode);
    }

    if (isPackageNodesHidden())
    {
      addChildren(children, packageNode);
    }

    if (isResourceNodesHidden())
    {
      addChildren(children, resourceNode);
    }

    return children.toArray(new Object[children.size()]);
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
    if (element instanceof Node)
    {
      Node node = (Node)element;
      return node.getRepositoryProject().getProject();
    }

    return null;
  }

  private void addChildren(List<Object> result, Node node)
  {
    Object[] children = node.getChildren();
    for (Object child : children)
    {
      result.add(child);
    }
  }

  private Node getNode(IRepositoryProject repositoryProject, Node.Type type, Map<IRepositoryProject, Node> nodes)
  {
    Node node = nodes.get(repositoryProject);
    if (node == null)
    {
      node = createNode(repositoryProject, type);
      nodes.put(repositoryProject, node);
    }

    return node;
  }

  private Node createNode(IRepositoryProject repositoryProject, Type type)
  {
    switch (type)
    {
    case SESSIONS:
      return new SessionsNode(repositoryProject);

    case PACKAGES:
      return new PackagesNode(repositoryProject);

    case RESOURCES:
      return new ResourcesNode(repositoryProject);

    default:
      return null;
    }
  }
}
