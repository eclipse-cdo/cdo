package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.ui.ISessionManager;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public final class DisconnectAction extends SafeAction
{
  public DisconnectAction()
  {
    super("Disonnect", "Disconnect from buddies server");
  }

  @Override
  protected void safeRun() throws Exception
  {
    ISessionManager.INSTANCE.disconnect();
  }
}