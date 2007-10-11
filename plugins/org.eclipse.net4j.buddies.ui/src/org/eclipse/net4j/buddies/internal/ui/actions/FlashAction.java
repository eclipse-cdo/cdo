package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.ui.ISessionManager;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public final class FlashAction extends SafeAction
{
  public FlashAction()
  {
    super("Flash Me", "Flash Me");
  }

  @Override
  protected void safeRun() throws Exception
  {
    ISessionManager.INSTANCE.flashMe();
  }
}