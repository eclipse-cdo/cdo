/*
 * Copyright (c) 2012, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.ui.messages.Messages;
import org.eclipse.net4j.signal.Signal;
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class Net4jItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public Net4jItemProvider()
  {
  }

  public Net4jItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof IAcceptor)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_ACCEPTOR);
    }

    if (obj instanceof IConnector)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_CONNECTOR);
    }

    if (obj instanceof IChannel)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_CHANNEL);
    }

    if (obj instanceof Signal)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_SIGNAL);
    }

    return super.getImage(obj);
  }

  /**
   * @since 4.2
   */
  @Override
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof IAcceptor || obj instanceof IConnector || obj instanceof IChannel)
      {
        manager.add(new RemoveAction(obj));
      }
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   */
  public static class RemoveAction extends LongRunningAction
  {
    private Object object;

    public RemoveAction(Object object)
    {
      super(Messages.getString("Net4jItemProvider.0"), Messages.getString("Net4jItemProvider.1"), //$NON-NLS-1$ //$NON-NLS-2$
          ContainerView.getDeleteImageDescriptor());
      this.object = object;
    }

    public Object getObject()
    {
      return object;
    }

    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      LifecycleUtil.deactivateNoisy(object);
    }
  }
}
