package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CommitTransactionAction extends ViewAction
{
  private static final String TITLE = "Commit";

  private static final String TOOL_TIP = "Commit this transaction";

  public CommitTransactionAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void doRun() throws Exception
  {
    getTransaction().commit();
  }
}