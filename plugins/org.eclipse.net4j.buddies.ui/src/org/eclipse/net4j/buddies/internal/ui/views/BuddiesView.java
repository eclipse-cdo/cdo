/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.buddies.internal.ui.ChatInstaller;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BuddiesView extends SessionManagerView
{
  public BuddiesView()
  {
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return getSession() != null ? getSession() : ContainerUtil.emptyContainer();
  }

  @Override
  protected Control createControl(Composite parent)
  {
    Control control = super.createControl(parent);
    BuddiesDragListener.support(getViewer());
    BuddiesDropAdapter.support(getViewer());
    return control;
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (getSession() != null && object instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)object;
      IBuddy self = getSession().getSelf();
      IMembership membership = self.initiate(buddy);

      try
      {
        // The chat dependency is optional
        ChatInstaller.installChat((IBuddyCollaboration)membership.getCollaboration());
      }
      catch (Throwable ignore)
      {
      }
    }
  }
}
