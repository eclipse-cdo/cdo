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
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

public class CollaborationsView extends SessionManagerView
{
  private SashComposite sashComposite;

  public CollaborationsView()
  {
  }

  public Control getPane()
  {
    return sashComposite.getControl2();
  }

  @Override
  protected Control createControl(Composite parent)
  {
    return sashComposite = new SashComposite(parent, SWT.NONE, 10, 30)
    {
      @Override
      protected Control createControl1(Composite parent)
      {
        return CollaborationsView.super.createControl(parent);
      }

      @Override
      protected Control createControl2(Composite parent)
      {
        return createPane(parent);
      }
    };
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