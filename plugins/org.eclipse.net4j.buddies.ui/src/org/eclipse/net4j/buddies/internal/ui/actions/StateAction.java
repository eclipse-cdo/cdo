package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.SharedIcons;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.buddies.ui.IBuddiesManager;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.jface.action.Action;

/**
 * @author Eike Stepper
 */
public class StateAction extends SafeAction
{
  private State state;

  public StateAction(String text, State state, String key)
  {
    super(text, Action.AS_RADIO_BUTTON);
    setToolTipText("Set own state to '" + text.toLowerCase() + "'");
    setImageDescriptor(SharedIcons.getDescriptor(key));
    this.state = state;
  }

  public void updateState()
  {
    IBuddySession session = IBuddiesManager.INSTANCE.getSession();
    setEnabled(session != null);
    setChecked(session != null && session.getSelf().getState() == state);
  }

  @Override
  protected void safeRun() throws Exception
  {
    IBuddySession session = IBuddiesManager.INSTANCE.getSession();
    if (session != null && isChecked())
    {
      Self self = (Self)session.getSelf();
      self.setState(state);
    }
  }

  public static final class AvailableAction extends StateAction
  {
    public AvailableAction()
    {
      super("Available", State.AVAILABLE, SharedIcons.OBJ_BUDDY);
    }
  }

  public static final class LonesomeAction extends StateAction
  {
    public LonesomeAction()
    {
      super("Lonesome", State.LONESOME, SharedIcons.OBJ_BUDDY_LONESOME);
    }
  }

  public static final class AwayAction extends StateAction
  {
    public AwayAction()
    {
      super("Away", State.AWAY, SharedIcons.OBJ_BUDDY_AWAY);
    }
  }

  public static final class DoNotDisturbAction extends StateAction
  {
    public DoNotDisturbAction()
    {
      super("Do Not Disturb", State.DO_NOT_DISTURB, SharedIcons.OBJ_BUDDY_DO_NOT_DISTURB);
    }
  }
}