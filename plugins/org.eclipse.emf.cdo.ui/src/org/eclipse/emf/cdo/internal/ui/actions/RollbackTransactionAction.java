package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class RollbackTransactionAction extends ViewAction
{
  private static final String TITLE = "Rollback";

  private static final String TOOL_TIP = "Rollback this transaction";

  public RollbackTransactionAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void doRun() throws Exception
  {
    getTransaction().rollback();
  }
}