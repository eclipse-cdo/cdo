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
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
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

  private Map<IFacility, FacilityPane> facilityPanes = new HashMap<IFacility, FacilityPane>();

  private Map<IBuddyCollaboration, IFacility> activeFacilities = new HashMap<IBuddyCollaboration, IFacility>();

  public CollaborationsPane(Composite parent, CollaborationsView collaborationsView)
  {
    super(parent, SWT.NONE);
    setLayout(UIUtil.createGridLayout(1));

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
  }
}
