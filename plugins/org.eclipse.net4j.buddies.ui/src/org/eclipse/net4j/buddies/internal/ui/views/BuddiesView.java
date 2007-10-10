package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddySession;
import org.eclipse.net4j.buddies.internal.ui.SharedIcons;
import org.eclipse.net4j.buddies.protocol.IBuddy;
import org.eclipse.net4j.buddies.protocol.IBuddyStateChangedEvent;
import org.eclipse.net4j.buddies.protocol.IBuddy.State;
import org.eclipse.net4j.buddies.ui.IBuddiesManager;
import org.eclipse.net4j.buddies.ui.IBuddiesManagerStateChangedEvent;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;

import org.eclipse.jface.action.Action;
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

  private StateAction availableAction = new StateAction("Available", State.AVAILABLE, SharedIcons.OBJ_BUDDY);

  private StateAction lonesomeAction = new StateAction("Lonesome", State.LONESOME, SharedIcons.OBJ_BUDDY_LONESOME);

  private StateAction awayAction = new StateAction("Away", State.AWAY, SharedIcons.OBJ_BUDDY_AWAY);

  private StateAction doNotDisturbAction = new StateAction("Do Not Disturb", State.DO_NOT_DISTURB,
      SharedIcons.OBJ_BUDDY_DO_NOT_DISTURB);

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
      session = IBuddiesManager.INSTANCE.getSession();
      updateState();
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
    session = IBuddiesManager.INSTANCE.getSession();
    IBuddiesManager.INSTANCE.addListener(this);
    INSTANCE = this;
    updateState();
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

  protected void updateState()
  {
    updateState(availableAction, IBuddy.State.AVAILABLE);
    updateState(lonesomeAction, IBuddy.State.LONESOME);
    updateState(awayAction, IBuddy.State.AWAY);
    updateState(doNotDisturbAction, IBuddy.State.DO_NOT_DISTURB);
  }

  protected void updateState(StateAction action, State state)
  {
    action.setEnabled(session != null);
    action.setChecked(session != null && session.getSelf().getState() == state);
  }

  /**
   * @author Eike Stepper
   */
  private final class ConnectAction extends SafeAction
  {
    private ConnectAction()
    {
      super("Connect", "Connect to buddies server");
    }

    @Override
    protected void safeRun() throws Exception
    {
      IBuddiesManager.INSTANCE.connect();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DisconnectAction extends SafeAction
  {
    private DisconnectAction()
    {
      super("Disonnect", "Disconnect from buddies server");
    }

    @Override
    protected void safeRun() throws Exception
    {
      IBuddiesManager.INSTANCE.disconnect();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StateAction extends SafeAction
  {
    private State state;

    private StateAction(String text, State state, String key)
    {
      super(text, Action.AS_RADIO_BUTTON);
      setToolTipText("Set own state to '" + text.toLowerCase() + "'");
      setImageDescriptor(SharedIcons.getDescriptor(key));
      this.state = state;
    }

    @Override
    protected void safeRun() throws Exception
    {
      if (session != null && isChecked())
      {
        Self self = (Self)session.getSelf();
        self.setState(state);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FlashAction extends SafeAction
  {
    private FlashAction()
    {
      super("Flash Me", "Flash Me");
    }

    @Override
    protected void safeRun() throws Exception
    {
      IBuddiesManager.INSTANCE.flashMe();
    }
  }
}