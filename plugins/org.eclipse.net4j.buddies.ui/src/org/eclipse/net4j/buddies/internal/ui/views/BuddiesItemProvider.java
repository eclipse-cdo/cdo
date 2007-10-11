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

import org.eclipse.net4j.buddies.internal.ui.SharedIcons;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class BuddiesItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public static final Color GRAY = UIUtil.getDisplay().getSystemColor(SWT.COLOR_GRAY);

  private Font bold;

  public BuddiesItemProvider()
  {
  }

  public BuddiesItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  public void dispose()
  {
    UIUtil.dispose(bold);
    super.dispose();
  }

  public Font getBold()
  {
    return bold;
  }

  @Override
  public String getText(Object obj)
  {
    if (obj instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)obj;
      return buddy.getUserID();
    }

    if (obj instanceof ICollaboration)
    {
      ICollaboration collaboration = (ICollaboration)obj;
      return collaboration.getTitle();
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)obj;
      switch (buddy.getState())
      {
      case AVAILABLE:
        return SharedIcons.getImage(SharedIcons.OBJ_BUDDY);

      case LONESOME:
        return SharedIcons.getImage(SharedIcons.OBJ_BUDDY_LONESOME);

      case AWAY:
        return SharedIcons.getImage(SharedIcons.OBJ_BUDDY_AWAY);

      case DO_NOT_DISTURB:
        return SharedIcons.getImage(SharedIcons.OBJ_BUDDY_DO_NOT_DISTURB);
      }
    }

    if (obj instanceof ICollaboration)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_COLLABORATION);
    }

    return super.getImage(obj);
  }

  @Override
  public Color getForeground(Object obj)
  {
    if (obj instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)obj;
      switch (buddy.getState())
      {
      case AWAY:
      case DO_NOT_DISTURB:
        return GRAY;
      }
    }

    return super.getForeground(obj);
  }

  @Override
  public Font getFont(Object obj)
  {
    if (obj instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)obj;
      switch (buddy.getState())
      {
      case LONESOME:
        return bold;
      }
    }

    return super.getFont(obj);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof IBuddy)
      {
        manager.add(new RemoveAction(obj));
      }
    }
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    UIUtil.dispose(bold);
    super.inputChanged(viewer, oldInput, newInput);
    bold = UIUtil.getBoldFont(getViewer().getControl());
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
