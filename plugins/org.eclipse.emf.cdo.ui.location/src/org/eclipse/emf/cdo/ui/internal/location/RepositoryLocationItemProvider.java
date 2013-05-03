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
package org.eclipse.emf.cdo.ui.internal.location;

import org.eclipse.emf.cdo.internal.location.BranchCheckoutSource;
import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class RepositoryLocationItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public RepositoryLocationItemProvider()
  {
  }

  public RepositoryLocationItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof IRepositoryLocation)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_REPO);
    }

    if (obj instanceof BranchCheckoutSource)
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
      if (obj instanceof IRepositoryLocation)
      {
        manager.add(new RemoveAction(obj));
      }
    }
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
      if (object instanceof IRepositoryLocation)
      {
        IRepositoryLocation location = (IRepositoryLocation)object;
        location.delete();
      }
    }
  }
}
