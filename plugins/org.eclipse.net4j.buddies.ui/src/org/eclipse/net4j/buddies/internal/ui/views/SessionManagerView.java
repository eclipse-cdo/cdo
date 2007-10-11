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

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.actions.ConnectAction;
import org.eclipse.net4j.buddies.internal.ui.actions.DisconnectAction;
import org.eclipse.net4j.buddies.internal.ui.actions.FlashAction;
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction.DropDownAction;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.ui.ISessionManager;
import org.eclipse.net4j.buddies.ui.ISessionManagerEvent;
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

  public void notifyEvent(IEvent event)
  {
    if (event instanceof ISessionManagerEvent)
    {
      queryBuddiesManager();
    }
    else if (event instanceof IBuddyStateChangedEvent)
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
    manager.add(dropDownAction);
    super.fillLocalToolBar(manager);
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    manager.add(connectAction);
    manager.add(disconnectAction);
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
    flashAction.setEnabled(session != null && !ISessionManager.INSTANCE.isFlashing());
    dropDownAction.updateState();
    // control.setEnabled(session != null);
  }
}