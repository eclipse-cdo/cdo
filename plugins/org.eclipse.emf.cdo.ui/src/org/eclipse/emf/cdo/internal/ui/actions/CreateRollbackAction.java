package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CreateRollbackAction extends ViewAction
{
  private static final String TITLE = "Rollback";

  public CreateRollbackAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, "Rollback this transaction", null, view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void doRun() throws Exception
  {
    getTransaction().rollback();
  }
}