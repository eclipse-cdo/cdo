/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.explorer.CDORepository;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class CDORepositoryItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public CDORepositoryItemProvider()
  {
  }

  public CDORepositoryItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      if (!repository.isConnected())
      {
        return true;
      }
    }

    return super.hasChildren(element);
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      if (!repository.isConnected())
      {
        ContainerItemProvider<IContainer<Object>>.LazyElement lazyElement = createLazyElement(repository);
        return new Object[] { lazyElement };
      }
    }

    return superGetChildren(element);
  }

  public Object[] superGetChildren(Object element)
  {
    return super.getChildren(element);
  }

  @Override
  public String getText(Object element)
  {
    if (element instanceof CDOBranch)
    {
      CDOBranch branch = (CDOBranch)element;
      return branch.getName();
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDORepository)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_REPO);
    }

    if (obj instanceof CDOBranch)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_BRANCH);
    }

    return super.getImage(obj);
  }

  @Override
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);
    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof CDORepository)
      {
        manager.add(new RemoveAction(obj));
      }
    }
  }

  @Override
  protected boolean isSlow(IContainer<Object> container)
  {
    return true;
  }

  @Override
  protected String getSlowText(IContainer<Object> container)
  {
    if ((IContainer<?>)container instanceof CDORepository)
    {
      return "Connecting...";
    }

    return "Loading...";
  }

  @Override
  protected void handleInactiveElement(Iterator<org.eclipse.net4j.util.ui.views.ContainerItemProvider.Node> it,
      org.eclipse.net4j.util.ui.views.ContainerItemProvider.Node child)
  {
    // Do nothing.
  }

  /**
   * @author Eike Stepper
   */
  public static class RemoveAction extends LongRunningAction
  {
    private Object object;

    public RemoveAction(Object object)
    {
      super("Remove");
      this.object = object;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      // if (object instanceof CDORepository)
      // {
      // CDORepository repository = (CDORepository)object;
      // repository.remove();
      // }
    }
  }
}
