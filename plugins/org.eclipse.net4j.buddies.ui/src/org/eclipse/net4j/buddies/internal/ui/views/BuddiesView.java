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

import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BuddiesView extends SessionManagerView
{
  private static BuddiesView INSTANCE;

  public BuddiesView()
  {
  }

  public static synchronized BuddiesView getINSTANCE()
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
    Control control = super.createControl(parent);
    INSTANCE = this;
    return control;
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession() : ContainerUtil.emptyContainer();
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (getSession() != null && object instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)object;
      IBuddy self = getSession().getSelf();
      self.initiate(buddy);
    }
  }

}