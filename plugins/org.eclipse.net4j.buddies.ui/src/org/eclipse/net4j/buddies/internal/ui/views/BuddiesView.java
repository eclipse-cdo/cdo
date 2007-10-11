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
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction;
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction.AvailableAction;
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction.AwayAction;
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction.DoNotDisturbAction;
import org.eclipse.net4j.buddies.internal.ui.actions.StateAction.LonesomeAction;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.ui.IBuddiesManager;
import org.eclipse.net4j.buddies.ui.IBuddiesManagerStateChangedEvent;
import org.eclipse.net4j.util.container.ContainerUtil;
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

public class BuddiesView extends ContainerView implements IListener
{
  private static BuddiesView INSTANCE;

  private IBuddySession session;

  private ConnectAction connectAction = new ConnectAction();

  private DisconnectAction disconnectAction = new DisconnectAction();

  private FlashAction flashAction = new FlashAction();

  private StateAction availableAction = new AvailableAction();

  private StateAction lonesomeAction = new LonesomeAction();

  private StateAction awayAction = new AwayAction();

  private StateAction doNotDisturbAction = new DoNotDisturbAction();

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
    IBuddiesManager.INSTANCE.removeListener(this);
    session = null;
    super.dispose();
  }

  public void notifyEvent(IEvent event)
  {
    if (event instanceof IBuddiesManagerStateChangedEvent)
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
  protected Control createUI(Composite parent)
  {
    Control control = super.createUI(parent);
    queryBuddiesManager();
    IBuddiesManager.INSTANCE.addListener(this);
    INSTANCE = this;
    return control;
  }

  @Override
  protected IContainer<?> getContainer()
  {
    return session != null ? session : ContainerUtil.emptyContainer();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new BuddiesItemProvider();
  }

  @Override
  protected void doubleClicked(Object object)
  {
    if (session != null && object instanceof IBuddy)
    {
      IBuddy buddy = (IBuddy)object;
      IBuddy self = session.getSelf();
      self.initiate(buddy);
    }
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(availableAction);
    manager.add(lonesomeAction);
    manager.add(awayAction);
    manager.add(doNotDisturbAction);
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
    session = IBuddiesManager.INSTANCE.getSession();
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
    flashAction.setEnabled(session != null && !IBuddiesManager.INSTANCE.isFlashing());

    availableAction.updateState();
    lonesomeAction.updateState();
    awayAction.updateState();
    doNotDisturbAction.updateState();
  }
}