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

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;

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
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession().getSelf() : ContainerUtil.emptyContainer();
  }
}