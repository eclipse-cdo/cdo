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
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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
    return new SashComposite(parent, SWT.NONE, 10, 30)
    {
      @Override
      protected Control createControl1(Composite parent)
      {
        return CollaborationsView.super.createControl(parent);
      }

      @Override
      protected Control createControl2(Composite parent)
      {
        return new CollaborationsPane(CollaborationsView.this, parent);
      }
    };
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    // manager.add(new AvailableAction());
    // manager.add(new AwayAction());
    manager.add(new Separator());
    super.fillLocalToolBar(manager);
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession().getSelf() : ContainerUtil.emptyContainer();
  }
}