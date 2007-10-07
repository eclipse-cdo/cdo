package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CommitTransactionAction extends ViewAction
{
  private static final String TITLE = "Commit";

  public CommitTransactionAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, "Commit this transaction", null, view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void doRun() throws Exception
  {
    getTransaction().commit();
  }
}