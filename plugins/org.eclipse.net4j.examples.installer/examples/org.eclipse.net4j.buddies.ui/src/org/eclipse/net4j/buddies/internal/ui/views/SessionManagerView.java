/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.ISessionManager;
import org.eclipse.net4j.buddies.ISessionManagerEvent;
import org.eclipse.net4j.buddies.common.IBuddyStateEvent;
import org.eclipse.net4j.buddies.internal.ui.BuddiesItemProvider;
import org.eclipse.net4j.buddies.internal.ui.actions.ConnectAction;
import org.eclipse.net4j.buddies.internal.ui.actions.DisconnectAction;
import org.eclipse.net4j.buddies.internal.ui.actions.FlashAction;
import org.eclipse.net4j.buddies.internal.ui.actions.ReconnectAction;
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction.DropDownAction;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class SessionManagerView extends ContainerView implements IListener
{
  private IBuddySession session;

  private ConnectAction connectAction = new ConnectAction();

  private DisconnectAction disconnectAction = new DisconnectAction();

  private ReconnectAction reconnectAction = new ReconnectAction();

  private FlashAction flashAction = new FlashAction();

  private DropDownAction dropDownAction = new DropDownAction();

  private Control control;

  public SessionManagerView()
  {
  }

  public IBuddySession getSession()
  {
    return session;
  }

  @Override
  public void dispose()
  {
    ISessionManager.INSTANCE.removeListener(this);
    session = null;
    super.dispose();
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof ISessionManagerEvent)
    {
      queryBuddiesManager();
    }
    else if (event instanceof IBuddyStateEvent)
    {
      if (session != null && event.getSource() == session.getSelf())
      {
        updateState();
      }
    }
  }

  @Override
  protected final Control createUI(Composite parent)
  {
    control = createControl(parent);
    queryBuddiesManager();
    ISessionManager.INSTANCE.addListener(this);
    return control;
  }

  protected Control createControl(Composite parent)
  {
    return super.createUI(parent);
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new BuddiesItemProvider();
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    super.fillLocalToolBar(manager);
    manager.add(dropDownAction);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(connectAction);
    manager.add(disconnectAction);
    manager.add(reconnectAction);
    manager.add(new Separator());
    manager.add(flashAction);
    super.fillLocalPullDown(manager);
  }

  protected void queryBuddiesManager()
  {
    IBuddySession oldSession = session;
    session = ISessionManager.INSTANCE.getSession();
    if (oldSession != session)
    {
      if (oldSession != null)
      {
        oldSession.removeListener(this);
        oldSession.getSelf().removeListener(this);
      }

      if (session != null)
      {
        session.addListener(this);
        session.getSelf().addListener(this);
      }
    }

    resetInput();
    updateState();
  }

  protected void updateState()
  {
    connectAction.setEnabled(session == null);
    disconnectAction.setEnabled(session != null);
    reconnectAction.setEnabled(session != null);
    flashAction.setEnabled(session != null && !ISessionManager.INSTANCE.isFlashing());
    dropDownAction.updateState();
    // control.setEnabled(session != null);
  }
}
