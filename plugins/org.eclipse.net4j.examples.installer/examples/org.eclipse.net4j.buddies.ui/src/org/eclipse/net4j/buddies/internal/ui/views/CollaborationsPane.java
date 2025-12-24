/*
 * Copyright (c) 2007-2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IFacility;
import org.eclipse.net4j.buddies.common.IFacilityInstalledEvent;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.buddies.ui.IFacilityPaneCreator;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CollaborationsPane extends Composite implements IListener
{
  private CollaborationsView collaborationsView;

  private IBuddySession session;

  private IBuddyCollaboration activeCollaboration;

  private Map<IBuddyCollaboration, IFacility> activeFacilities = new HashMap<>();

  private Map<IFacility, FacilityPane> facilityPanes = new HashMap<>();

  private List<ActivateFacilityAction> activateFacilityActions = new ArrayList<>();

  private StackLayout paneStack;

  public CollaborationsPane(Composite parent, CollaborationsView collaborationsView)
  {
    super(parent, SWT.NONE);
    setLayout(paneStack = new StackLayout());
    this.collaborationsView = collaborationsView;
  }

  public CollaborationsView getCollaborationsView()
  {
    return collaborationsView;
  }

  public IBuddySession getSession()
  {
    return session;
  }

  public void setSession(IBuddySession session)
  {
    this.session = session;
    if (session != null)
    {
      for (ICollaboration collaboration : session.getSelf().getCollaborations())
      {
        collaborationAdded((IBuddyCollaboration)collaboration);
      }
    }

    updateState();
  }

  public IBuddyCollaboration getActiveCollaboration()
  {
    return activeCollaboration;
  }

  public void setActiveCollaboration(IBuddyCollaboration collaboration)
  {
    if (activeCollaboration != collaboration)
    {
      activeCollaboration = collaboration;
      IFacility facility = activeFacilities.get(collaboration);
      setActiveFacility(collaboration, facility);
      updateState();
      collaborationsView.refreshViewer(true);
    }
  }

  public void setActiveFacility(IBuddyCollaboration collaboration, IFacility facility)
  {
    activeFacilities.put(collaboration, facility);
    if (collaboration == activeCollaboration)
    {
      FacilityPane facilityPane = facilityPanes.get(facility);
      setActiveFacilityPane(facilityPane);
      updateState();
    }
  }

  protected void setActiveFacilityPane(FacilityPane newPane)
  {
    if (paneStack.topControl != newPane)
    {
      FacilityPane oldPane = (FacilityPane)paneStack.topControl;
      if (oldPane != null)
      {
        oldPane.hidden(newPane);
      }

      paneStack.topControl = newPane;
      layout();
      if (newPane != null)
      {
        newPane.showed(oldPane);
      }

      updateState();
    }
  }

  public void fillActionBars(IActionBars bars)
  {
    IToolBarManager manager = bars.getToolBarManager();
    for (IFacilityPaneCreator c : collaborationsView.getFacilityPaneCreators().values())
    {
      ActivateFacilityAction action = new ActivateFacilityAction(c.getType(), c.getImageDescriptor());
      activateFacilityActions.add(action);
      manager.add(action);
    }
  }

  public void updateState()
  {
    for (ActivateFacilityAction action : activateFacilityActions)
    {
      if (activeCollaboration == null)
      {
        action.setEnabled(false);
      }
      else
      {
        String type = action.getType();
        action.setEnabled(activeCollaboration.getFacility(type) != null);

        IFacility activeFacility = activeFacilities.get(activeCollaboration);
        action.setChecked(activeFacility != null && ObjectUtil.equals(activeFacility.getType(), type));
      }
    }
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (session == null)
    {
      return;
    }

    if (event.getSource() == session.getSelf() && event instanceof IContainerEvent<?>)
    {
      @SuppressWarnings("unchecked")
      IContainerEvent<IMembership> e = (IContainerEvent<IMembership>)event;
      e.accept(new IContainerEventVisitor<IMembership>()
      {
        @Override
        public void added(IMembership membership)
        {
          collaborationAdded((IBuddyCollaboration)membership.getCollaboration());
        }

        @Override
        public void removed(IMembership membership)
        {
          collaborationRemoved((IBuddyCollaboration)membership.getCollaboration());
        }
      });
    }
    else if (event instanceof IFacilityInstalledEvent)
    {
      IFacilityInstalledEvent e = (IFacilityInstalledEvent)event;
      facilityInstalled(e.getFacility(), e.fromRemote());
    }
  }

  protected void collaborationAdded(IBuddyCollaboration collaboration)
  {
    IFacility[] facilities = collaboration.getFacilities();
    for (IFacility facility : facilities)
    {
      addFacilityPane(facility);
    }

    if (activeCollaboration == null)
    {
      setActiveCollaboration(collaboration);
    }

    if (facilities.length != 0)
    {
      setActiveFacility(collaboration, facilities[0]);
    }

    collaboration.addListener(this);
  }

  protected void collaborationRemoved(IBuddyCollaboration collaboration)
  {
    collaboration.removeListener(this);
    if (activeCollaboration == collaboration)
    {
      setActiveCollaboration(activeFacilities.isEmpty() ? null : activeFacilities.keySet().iterator().next());
    }

    activeFacilities.remove(collaboration);
    for (IFacility facility : collaboration.getFacilities())
    {
      FacilityPane pane = facilityPanes.remove(facility);
      if (pane != null)
      {
        pane.dispose();
      }
    }
  }

  protected void facilityInstalled(final IFacility facility, boolean fromRemote)
  {
    final IBuddyCollaboration collaboration = (IBuddyCollaboration)facility.getCollaboration();
    if (fromRemote)
    {
      Runnable runnable = new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            addFacilityPane(facility);
            IFacility activeFacility = activeFacilities.get(collaboration);
            if (activeFacility == null)
            {
              setActiveFacility(collaboration, facility);
            }
            else
            {
              updateState();
            }
          }
          catch (RuntimeException ignore)
          {
          }
        }
      };

      try
      {
        Display display = getDisplay();
        if (display.getThread() == Thread.currentThread())
        {
          runnable.run();
        }
        else
        {
          display.asyncExec(runnable);
        }
      }
      catch (RuntimeException ignore)
      {
      }
    }
    else
    {
      addFacilityPane(facility);
      setActiveCollaboration(collaboration);
      setActiveFacility(collaboration, facility);
    }
  }

  protected FacilityPane addFacilityPane(IFacility facility)
  {
    IFacilityPaneCreator creator = collaborationsView.getFacilityPaneCreators().get(facility.getType());
    FacilityPane pane = creator.createPane(this, SWT.NONE);
    pane.setFacility(facility);
    facilityPanes.put(facility, pane);
    return pane;
  }

  /**
   * @author Eike Stepper
   */
  private final class ActivateFacilityAction extends SafeAction
  {
    private final String type;

    private ActivateFacilityAction(String type, ImageDescriptor descriptor)
    {
      super(StringUtil.cap(type), AS_RADIO_BUTTON);
      setToolTipText(MessageFormat.format(Messages.getString("CollaborationsPane_0"), type)); //$NON-NLS-1$
      setImageDescriptor(descriptor);
      this.type = type;
    }

    public String getType()
    {
      return type;
    }

    @Override
    protected void safeRun() throws Exception
    {
      if (activeCollaboration != null)
      {
        IFacility facility = activeCollaboration.getFacility(type);
        setActiveFacility(activeCollaboration, facility);
      }
    }
  }
}
