/*
 * Copyright (c) 2007-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public abstract class AbstractItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  public static final Color GRAY = UIUtil.getDisplay().getSystemColor(SWT.COLOR_GRAY);

  private Font bold;

  public AbstractItemProvider()
  {
  }

  public AbstractItemProvider(IElementFilter rootElementFilter)
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
  protected Node createNode(Node parent, Object element)
  {
    if (element instanceof IMembership)
    {
      IMembership membership = (IMembership)element;
      return createMembershipNode(parent, membership);
    }

    return super.createNode(parent, element);
  }

  protected abstract Node createMembershipNode(Node parent, IMembership membership);

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

    if (obj instanceof IMembership)
    {
      IMembership membership = (IMembership)obj;
      return getText(membership);
    }

    return super.getText(obj);
  }

  protected abstract String getText(IMembership membership);

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

    if (obj instanceof IMembership)
    {
      IMembership membership = (IMembership)obj;
      return getImage(membership);
    }

    return super.getImage(obj);
  }

  protected abstract Image getImage(IMembership membership);

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
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    manager.add(new Separator());
    if (selection.size() == 1)
    {
      Object obj = selection.getFirstElement();
      if (obj instanceof IBuddy)
      {
        manager.add(new RemoveAction(obj));
      }
      else if (obj instanceof IBuddyCollaboration)
      {
        final IBuddyCollaboration collaboration = (IBuddyCollaboration)obj;
        manager.add(new SafeAction(Messages.getString("AbstractItemProvider.0"), Messages.getString("AbstractItemProvider.1")) //$NON-NLS-1$ //$NON-NLS-2$
        {
          @Override
          protected void safeRun() throws Exception
          {
            collaboration.leave();
          }
        });
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
      super(Messages.getString("AbstractItemProvider.2"), Messages.getString("AbstractItemProvider.3"), //$NON-NLS-1$ //$NON-NLS-2$
          ContainerView.getDeleteImageDescriptor());
      this.object = object;
    }

    /**
     * @since 2.0
     */
    @Override
    protected void doRun(IProgressMonitor progressMonitor) throws Exception
    {
      LifecycleUtil.deactivateNoisy(object);
    }
  }
}
