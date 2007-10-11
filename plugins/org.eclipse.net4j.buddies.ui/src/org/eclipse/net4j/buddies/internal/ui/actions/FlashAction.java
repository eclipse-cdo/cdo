package org.eclipse.net4j.buddies.internal.ui.actions;

import org.eclipse.net4j.buddies.ui.IBuddiesManager;
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
    IBuddiesManager.INSTANCE.flashMe();
  }
}