package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.ui.IBuddiesManager;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public final class ConnectAction extends SafeAction
{
  public ConnectAction()
  {
    super("Connect", "Connect to buddies server");
  }

  @Override
  protected void safeRun() throws Exception
  {
    IBuddiesManager.INSTANCE.connect();
  }
}