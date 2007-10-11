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

import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

public class CollaborationsView extends SessionManagerView
{
  private static final int LIMIT = 10;

  private static final int PERCENT = 30;

  private static CollaborationsView INSTANCE;

  private Sash sash;

  private Control leftControl;

  private Control rightControl;

  public CollaborationsView()
  {
  }

  public static synchronized CollaborationsView getINSTANCE()
  {
    return INSTANCE;
  }

  @Override
  public synchronized void dispose()
  {
    INSTANCE = null;
    super.dispose();
  }

  @Override
  protected Control createControl(Composite parent)
  {
    final FormLayout form = new FormLayout();
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(form);

    leftControl = super.createControl(composite);
    sash = new Sash(composite, SWT.VERTICAL);
    rightControl = createPane(composite);

    FormData leftControlData = new FormData();
    leftControlData.left = new FormAttachment(0, 0);
    leftControlData.right = new FormAttachment(sash, 0);
    leftControlData.top = new FormAttachment(0, 0);
    leftControlData.bottom = new FormAttachment(100, 0);
    leftControl.setLayoutData(leftControlData);

    final FormData sashData = new FormData();
    sashData.left = new FormAttachment(PERCENT, 0);
    sashData.top = new FormAttachment(0, 0);
    sashData.bottom = new FormAttachment(100, 0);
    sash.setLayoutData(sashData);
    sash.addListener(SWT.Selection, new Listener()
    {
      public void handleEvent(Event e)
      {
        Rectangle sashRect = sash.getBounds();
        Rectangle shellRect = composite.getClientArea();
        int right = shellRect.width - sashRect.width - LIMIT;
        e.x = Math.max(Math.min(e.x, right), LIMIT);
        if (e.x != sashRect.x)
        {
          sashData.left = new FormAttachment(0, e.x);
          composite.layout();
        }
      }
    });

    FormData rightControlData = new FormData();
    rightControlData.left = new FormAttachment(sash, 0);
    rightControlData.right = new FormAttachment(100, 0);
    rightControlData.top = new FormAttachment(0, 0);
    rightControlData.bottom = new FormAttachment(100, 0);
    rightControl.setLayoutData(rightControlData);

    INSTANCE = this;
    return composite;
  }

  protected Control createPane(Composite parent)
  {
    List list = new List(parent, SWT.NONE);
    for (String facilityType : BuddiesUtil.getFacilityTypes())
    {
      list.add(facilityType);
    }

    return list;
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession().getSelf() : ContainerUtil.emptyContainer();
  }
}