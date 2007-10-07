package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;

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
  protected void preRun() throws Exception
  {
    CDOTransaction transaction = (CDOTransaction)getView();
    String msg = MessageFormat.format("This transaction contains " + "{0} new resources, " + "{1} new objects and "
        + "{2} dirty objects.\n" + "Are you sure to rollback this transaction?", transaction.getNewResources().size(),
        transaction.getNewObjects().size(), transaction.getDirtyObjects().size());
    if (!MessageDialog.openQuestion(getShell(), TITLE, msg))
    {
      cancel();
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    getTransaction().rollback();
  }
}