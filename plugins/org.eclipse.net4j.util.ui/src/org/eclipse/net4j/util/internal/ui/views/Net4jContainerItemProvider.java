/*
 * Copyright (c) 2007-2012, 2015, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.ManagedContainerItemProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class Net4jContainerItemProvider extends ManagedContainerItemProvider
{
  public Net4jContainerItemProvider()
  {
  }

  @Override
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    manager.add(new RemoveAction(selection));
  }

  /**
   * @author Eike Stepper
   */
  public class RemoveAction extends LongRunningAction
  {
    private ITreeSelection selection;

    public RemoveAction(ITreeSelection selection)
    {
      super(Messages.getString("Net4jContainerItemProvider_0"), Messages.getString("Net4jContainerItemProvider.1"), //$NON-NLS-1$ //$NON-NLS-2$
          ContainerView.getDeleteImageDescriptor());
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
