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
import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.protocol.ICollaboration;
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerEventVisitor;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CollaborationsPane extends Composite implements IListener
{
  private CollaborationsView collaborationsView;

  private ISelectionChangedListener collaborationsViewerListener = new ISelectionChangedListener()
  {
    public void selectionChanged(SelectionChangedEvent event)
    {
    }
  };

  private IBuddySession session;

  private Map<IBuddyCollaboration, IFacility> activeFacilities = new HashMap<IBuddyCollaboration, IFacility>();

  private Map<IFacility, FacilityPane> facilityPanes = new HashMap<IFacility, FacilityPane>();

  private StackLayout paneStack;

  public CollaborationsPane(Composite parent, CollaborationsView collaborationsView)
  {
    super(parent, SWT.NONE);
    setLayout(paneStack = new StackLayout());

    this.collaborationsView = collaborationsView;
    collaborationsView.getViewer().addSelectionChangedListener(collaborationsViewerListener);
  }

  @Override
  public void dispose()
  {
    collaborationsView.getViewer().removeSelectionChangedListener(collaborationsViewerListener);
    super.dispose();
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
  }

  public void notifyEvent(IEvent event)
  {
    if (session != null && event.getSource() == session.getSelf() && event instanceof IContainerEvent)
    {
      IContainerEvent<ICollaboration> e = (IContainerEvent<ICollaboration>)event;
      e.accept(new IContainerEventVisitor<ICollaboration>()
      {
        public void added(ICollaboration collaboration)
        {
          collaborationAdded(collaboration);
        }

        public void removed(ICollaboration collaboration)
        {
          collaborationRemoved(collaboration);
        }
      });
    }
  }

  protected void collaborationAdded(ICollaboration collaboration)
  {
    for (IFacility facility : collaboration.getFacilities())
    {
      addFacilityPane(facility);
    }
  }

  protected void collaborationRemoved(ICollaboration collaboration)
  {
  }

  protected FacilityPane addFacilityPane(IFacility facility)
  {
    FacilityPane pane = createFacilityPane(facility.getType());
    facilityPanes.put(facility, pane);
    return pane;
  }

  protected FacilityPane createFacilityPane(String type)
  {
    return null;
  }
}
