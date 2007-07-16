package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CloseSessionAction extends SessionAction
{
  public CloseSessionAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, "Close", "Close the CDO session", null, session);
  }

  @Override
  protected void doRun(final IWorkbenchPage page, IProgressMonitor monitor) throws Exception
  {
    getSession().close();
  }
}