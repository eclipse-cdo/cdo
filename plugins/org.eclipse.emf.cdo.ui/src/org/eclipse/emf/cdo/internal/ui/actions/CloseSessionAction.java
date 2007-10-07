package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CloseSessionAction extends SessionAction
{
  private static final String TITLE = "Close";

  private static final String TOOL_TIP = "Close the CDO session";

  public CloseSessionAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected void doRun() throws Exception
  {
    getSession().close();
  }
}