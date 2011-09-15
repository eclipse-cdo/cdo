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
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class Net4jContainerItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public Net4jContainerItemProvider()
  {
  }

  public Net4jContainerItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    manager.add(new RemoveAction(selection));
  }

  @Override
  public Image getImage(Object obj)
  {
    return SharedIcons.getImage(SharedIcons.OBJ_BEAN);
  }

  /**
   * @author Eike Stepper
   */
  public class RemoveAction extends LongRunningAction
  {
    private ITreeSelection selection;

    public RemoveAction(ITreeSelection selection)
    {
      super(
          Messages.getString("Net4jContainerItemProvider_0"), Messages.getString("Net4jContainerItemProvider.1"), ContainerView.getDeleteImageDescriptor()); //$NON-NLS-1$ //$NON-NLS-2$
      this.selection = selection;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      for (Iterator<?> it = selection.iterator(); it.hasNext();)
      {
        Object object = it.next();
        LifecycleUtil.deactivate(object);
      }
    }
  }
}
