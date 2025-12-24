/*
 * Copyright (c) 2007-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.ISessionManager;
import org.eclipse.net4j.buddies.common.IBuddy.State;
import org.eclipse.net4j.buddies.internal.ui.messages.Messages;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class StateAction extends SafeAction
{
  private State state;

  public StateAction(String text, State state, String key)
  {
    super(text, Action.AS_RADIO_BUTTON);
    setToolTipText(MessageFormat.format(Messages.getString("StateAction_0"), text.toLowerCase())); //$NON-NLS-1$
    setImageDescriptor(SharedIcons.getDescriptor(key));
    this.state = state;
  }

  public void updateState()
  {
    IBuddySession session = ISessionManager.INSTANCE.getSession();
    setEnabled(session != null);
    setChecked(session != null && session.getSelf().getState() == state);
  }

  @Override
  protected void safeRun() throws Exception
  {
    IBuddySession session = ISessionManager.INSTANCE.getSession();
    if (session != null && isChecked())
    {
      Self self = (Self)session.getSelf();
      self.setState(state);
    }
  }

  public static class DropDownAction extends Action implements IMenuCreator
  {
    private Menu fMenu;

    private StateAction availableAction = new AvailableAction();

    private StateAction lonesomeAction = new LonesomeAction();

    private StateAction awayAction = new AwayAction();

    private StateAction doNotDisturbAction = new DoNotDisturbAction();

    public DropDownAction()
    {
      setText(Messages.getString("StateAction_2")); //$NON-NLS-1$
      setMenuCreator(this);
    }

    @Override
    public void dispose()
    {
      if (fMenu != null)
      {
        fMenu.dispose();
        fMenu = null;
      }
    }

    public void updateState()
    {
      availableAction.updateState();
      lonesomeAction.updateState();
      awayAction.updateState();
      doNotDisturbAction.updateState();

      IBuddySession session = ISessionManager.INSTANCE.getSession();
      if (session != null)
      {
        setEnabled(true);
        State state = session.getSelf().getState();
        switch (state)
        {
        case AVAILABLE:
          setImageDescriptor(availableAction.getImageDescriptor());
          break;

        case LONESOME:
          setImageDescriptor(lonesomeAction.getImageDescriptor());
          break;

        case AWAY:
          setImageDescriptor(awayAction.getImageDescriptor());
          break;

        case DO_NOT_DISTURB:
          setImageDescriptor(doNotDisturbAction.getImageDescriptor());
          break;
        }
      }
      else
      {
        setImageDescriptor(awayAction.getImageDescriptor());
        setEnabled(false);
      }
    }

    @Override
    public Menu getMenu(Control parent)
    {
      if (fMenu != null)
      {
        fMenu.dispose();
      }

      fMenu = new Menu(parent);
      addActionToMenu(fMenu, availableAction);
      addActionToMenu(fMenu, lonesomeAction);
      addActionToMenu(fMenu, awayAction);
      addActionToMenu(fMenu, doNotDisturbAction);
      return fMenu;
    }

    @Override
    public Menu getMenu(Menu parent)
    {
      return null;
    }

    @Override
    public void run()
    {
    }

    protected void addActionToMenu(Menu parent, Action action)
    {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(parent, -1);
    }
  }

  public static final class AvailableAction extends StateAction
  {
    public AvailableAction()
    {
      super(Messages.getString("StateAction_3"), State.AVAILABLE, SharedIcons.OBJ_BUDDY); //$NON-NLS-1$
    }
  }

  public static final class LonesomeAction extends StateAction
  {
    public LonesomeAction()
    {
      super(Messages.getString("StateAction_4"), State.LONESOME, SharedIcons.OBJ_BUDDY_LONESOME); //$NON-NLS-1$
    }
  }

  public static final class AwayAction extends StateAction
  {
    public AwayAction()
    {
      super(Messages.getString("StateAction_5"), State.AWAY, SharedIcons.OBJ_BUDDY_AWAY); //$NON-NLS-1$
    }
  }

  public static final class DoNotDisturbAction extends StateAction
  {
    public DoNotDisturbAction()
    {
      super(Messages.getString("StateAction_6"), State.DO_NOT_DISTURB, SharedIcons.OBJ_BUDDY_DO_NOT_DISTURB); //$NON-NLS-1$
    }
  }
}
