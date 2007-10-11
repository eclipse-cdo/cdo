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

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class CollaborationsView extends SessionManagerView
{
  private SashComposite sashComposite;

  public CollaborationsView()
  {
  }

  public CollaborationsPane getCollaborationsPane()
  {
    return (CollaborationsPane)sashComposite.getControl2();
  }

  @Override
  protected Control createControl(Composite parent)
  {
    sashComposite = new SashComposite(parent, SWT.NONE, 10, 30)
    {
      @Override
      protected Control createControl1(Composite parent)
      {
        return CollaborationsView.super.createControl(parent);
      }

      @Override
      protected Control createControl2(Composite parent)
      {
        return new CollaborationsPane(parent, CollaborationsView.this);
      }
    };

    IActionBars bars = getViewSite().getActionBars();
    bars.getMenuManager().add(new Separator());
    bars.getToolBarManager().add(new Separator());
    getCollaborationsPane().fillActionBars(bars);
    return sashComposite;
  }

  @Override
  protected void queryBuddiesManager()
  {
    super.queryBuddiesManager();
    getCollaborationsPane().setSession(getSession());
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    super.notifyEvent(event);
    getCollaborationsPane().notifyEvent(event);
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);
    if (selection.size() == 1)
    {
      Object firstElement = selection.getFirstElement();
      if (firstElement instanceof IBuddyCollaboration)
      {
        IBuddyCollaboration collaboration = (IBuddyCollaboration)firstElement;
        manager.add(new Separator());

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, OM.EXT_POINT);
        for (final IConfigurationElement element : elements)
        {
          if ("facilityPaneCreator".equals(element.getName()))
          {
            String type = element.getAttribute("type");
            if (collaboration.getFacility(type) == null)
            {
              String icon = element.getAttribute("icon");
              ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(OM.BUNDLE_ID, icon);
              IAction action = new InstallFacilityAction(collaboration, type, descriptor);
              manager.add(action);
            }
          }
        }
      }
    }
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession().getSelf() : ContainerUtil.emptyContainer();
  }

  /**
   * @author Eike Stepper
   */
  private final class InstallFacilityAction extends SafeAction
  {
    private final String type;

    private IBuddyCollaboration collaboration;

    private InstallFacilityAction(IBuddyCollaboration collaboration, String type, ImageDescriptor descriptor)
    {
      super("Install " + StringUtil.cap(type), AS_RADIO_BUTTON);
      setToolTipText("Install " + type + " facility");
      setImageDescriptor(descriptor);
      this.collaboration = collaboration;
      this.type = type;
    }

    @Override
    protected void safeRun() throws Exception
    {
      collaboration.installFacility(type);
    }
  }
}