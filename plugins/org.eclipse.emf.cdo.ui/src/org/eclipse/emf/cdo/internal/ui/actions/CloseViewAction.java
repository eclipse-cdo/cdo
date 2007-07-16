package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CloseViewAction extends ViewAction
{
  public CloseViewAction(IWorkbenchPage page, CDOView view)
  {
    super(page, "Close", "Close the CDO view", null, view);
  }

  @Override
  protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
  {
    getView().close();
  }
}