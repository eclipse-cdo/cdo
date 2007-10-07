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
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class BuddiesItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public BuddiesItemProvider()
  {
  }

  public BuddiesItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof String)
    {
      // return SharedIcons.getImage(SharedIcons.OBJ_ACCEPTOR);
    }

    return super.getImage(obj);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof String)
      {
        manager.add(new RemoveAction(obj));
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class RemoveAction extends LongRunningAction
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
