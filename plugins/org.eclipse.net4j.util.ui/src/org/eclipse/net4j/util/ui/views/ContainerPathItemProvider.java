/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
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

/**
 * @author Eike Stepper
 */
@Deprecated
public class ContainerPathItemProvider<CONTAINER extends IContainer<Object>> extends ContainerItemProvider<CONTAINER> implements ITreePathContentProvider
{
  public ContainerPathItemProvider()
  {
    throw new UnsupportedOperationException();
  }

  public ContainerPathItemProvider(IElementFilter rootElementFilter)
  {
    throw new UnsupportedOperationException();
  }

  protected TreePath getParentPath(Node node)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] getChildren(TreePath parentPath)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasChildren(TreePath path)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public TreePath[] getParents(Object element)
  {
    throw new UnsupportedOperationException();
  }
}
