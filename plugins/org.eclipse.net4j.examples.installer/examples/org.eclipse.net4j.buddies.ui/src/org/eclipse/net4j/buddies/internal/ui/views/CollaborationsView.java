/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.internal.ui.CollaborationsItemProvider;
import org.eclipse.net4j.buddies.internal.ui.bundle.OM;
import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.buddies.ui.IFacilityPaneCreator;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.actions.SashLayoutAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class CollaborationsView extends SessionManagerView
{
  private SashComposite sashComposite;

  private Map<String, IFacilityPaneCreator> facilityPaneCreators = new HashMap<>();

  public CollaborationsView()
  {
    initFacilityPaneCreators();
  }

  public CollaborationsPane getCollaborationsPane()
  {
    return (CollaborationsPane)sashComposite.getControl2();
  }

  public Map<String, IFacilityPaneCreator> getFacilityPaneCreators()
  {
    return facilityPaneCreators;
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

    BuddiesDropAdapter.support(getViewer());
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
  protected void fillLocalPullDown(IMenuManager manager)
  {
    super.fillLocalPullDown(manager);
    manager.add(new Separator());
    manager.add(new SashLayoutAction.LayoutMenu(sashComposite));
  }

  @Override
  protected void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);
    if (selection.size() == 1)
    {
      final IBuddyCollaboration collaboration = (IBuddyCollaboration)selection.getFirstElement();
      if (collaboration != null)
      {
        manager.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, new Separator());
        for (IFacilityPaneCreator c : facilityPaneCreators.values())
        {
          String type = c.getType();
          if (collaboration.getFacility(type) == null)
          {
            IAction action = new StartFacilityAction(collaboration, type, c.getImageDescriptor());
            manager.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, action);
          }
        }
      }
    }
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (object instanceof IBuddyCollaboration)
    {
      IBuddyCollaboration collaboration = (IBuddyCollaboration)object;
      getCollaborationsPane().setActiveCollaboration(collaboration);
    }
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession().getSelf() : ContainerUtil.emptyContainer();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new CollaborationsItemProvider()
    {
      @Override
      public Font getFont(Object obj)
      {
        if (obj instanceof IBuddyCollaboration)
        {
          if (obj == getCollaborationsPane().getActiveCollaboration())
          {
            return getBold();
          }
        }

        return super.getFont(obj);
      }
    };
  }

  protected void initFacilityPaneCreators()
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, OM.EXT_POINT);
    for (final IConfigurationElement element : elements)
    {
      if ("facilityPaneCreator".equals(element.getName())) //$NON-NLS-1$
      {
        try
        {
          IFacilityPaneCreator creator = (IFacilityPaneCreator)element.createExecutableExtension("class"); //$NON-NLS-1$
          facilityPaneCreators.put(creator.getType(), creator);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StartFacilityAction extends SafeAction
  {
    private final String type;

    private IBuddyCollaboration collaboration;

    private StartFacilityAction(IBuddyCollaboration collaboration, String type, ImageDescriptor descriptor)
    {
      super(MessageFormat.format(Messages.getString("CollaborationsView_2"), StringUtil.cap(type)), AS_RADIO_BUTTON); //$NON-NLS-1$
      setToolTipText(MessageFormat.format(Messages.getString("CollaborationsView_3"), type)); //$NON-NLS-1$
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
