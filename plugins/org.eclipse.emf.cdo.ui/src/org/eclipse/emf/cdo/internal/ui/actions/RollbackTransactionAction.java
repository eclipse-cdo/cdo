package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;

import org.eclipse.jface.dialogs.MessageDialog;
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
  protected void preRun() throws Exception
  {
    CDOTransaction transaction = (CDOTransaction)getView();
    int newResources = transaction.getNewResources().size();
    int newObjects = transaction.getNewObjects().size();
    int dirtyObjects = transaction.getDirtyObjects().size();
    int count = (newResources > 0 ? 1 : 0) + (newObjects > 0 ? 1 : 0) + (dirtyObjects > 0 ? 1 : 0);

    StringBuilder builder = new StringBuilder();
    builder.append("This transaction contains ");
    if (newResources > 0)
    {
      builder.append(newResources);
      builder.append(" new resource");
      if (newResources > 1)
      {
        builder.append("s");
      }
    }

    if (newObjects > 0)
    {
      if (newResources > 0)
      {
        if (count > 2)
        {
          builder.append(", ");
        }
        else
        {
          builder.append(" and ");
        }
      }

      builder.append(newObjects);
      builder.append(" new object");
      if (newObjects > 1)
      {
        builder.append("s");
      }
    }

    if (dirtyObjects > 0)
    {
      if (count > 1)
      {
        builder.append(" and ");
      }

      builder.append(dirtyObjects);
      builder.append(" dirty object");
      if (dirtyObjects > 1)
      {
        builder.append("s");
      }
    }

    builder.append(".\nAre you sure to rollback this transaction?");
    if (!MessageDialog.openQuestion(getShell(), TITLE, builder.toString()))
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