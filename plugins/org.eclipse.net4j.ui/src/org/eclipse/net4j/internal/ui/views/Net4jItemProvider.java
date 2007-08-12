/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.IAcceptor;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.internal.ui.SharedIcons;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class Net4jItemProvider extends ContainerItemProvider
{
  public Net4jItemProvider()
  {
  }

  public Net4jItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  // @Override
  // public String getText(Object obj)
  // {
  // if (obj instanceof IChannel)
  // {
  // IChannel channel = (IChannel)obj;
  // return MessageFormat.format("[{0}] {1}", channel.getChannelIndex(),
  // channel.getReceiveHandler());
  // }
  //
  // return super.getText(obj);
  // }

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

    return super.getImage(obj);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
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
   * @author Eike Stepper
   */
  public class RemoveAction extends SafeAction
  {
    private Object object;

    public RemoveAction(Object object)
    {
      super("Remove", "Remove", ContainerView.getDeleteImageDescriptor());
      this.object = object;
    }

    @Override
    protected void doRun() throws Exception
    {
      LifecycleUtil.deactivateNoisy(object);
    }
  }
}
