/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@Deprecated
public class ContainerPathItemProvider<CONTAINER extends IContainer<Object>> extends ContainerItemProvider<CONTAINER> implements ITreePathContentProvider
{
  private Map<Object, List<TreePath>> parents = new HashMap<Object, List<TreePath>>();

  public ContainerPathItemProvider()
  {
  }

  public ContainerPathItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  public boolean hasChildren(TreePath path)
  {
    return hasChildren((Object)path);
  }

  @Override
  public Object[] getChildren(TreePath path)
  {
    return getChildren((Object)path);
  }

  @Override
  public TreePath[] getParents(Object element)
  {
    List<TreePath> paths = parents.get(element);
    if (paths != null)
    {
      return paths.toArray(new TreePath[paths.size()]);
    }

    return null;
  }

  @Override
  protected void addNode(Object element, Node node)
  {
    super.addNode(node.getTreePath(), node);
    TreePath path = getParentPath(node);
    List<TreePath> paths = parents.get(element);
    if (paths == null)
    {
      paths = new ArrayList<TreePath>();
      parents.put(element, paths);
    }

    paths.add(path);
  }

  @Override
  protected Node removeNode(Object element)
  {
    Node node = super.removeNode(element);
    TreePath path = getParentPath(node);
    List<TreePath> paths = parents.get(element);
    if (paths != null)
    {
      paths.remove(path);
    }

    return node;
  }

  @Override
  protected void disconnectInput(CONTAINER input)
  {
    super.disconnectInput(input);
  }

  protected TreePath getParentPath(Node node)
  {
    Node parent = node.getParent();
    return parent == null ? TreePath.EMPTY : parent.getTreePath();
  }
}
