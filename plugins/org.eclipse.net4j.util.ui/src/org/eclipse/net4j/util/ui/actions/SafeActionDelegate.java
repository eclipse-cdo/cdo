package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.internal.ui.bundle.OM;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;

/**
 * @author Eike Stepper
 */
public abstract class SafeActionDelegate implements IActionDelegate
{
  private IAction action;

  private ISelection selection;

  public SafeActionDelegate()
  {
  }

  public IAction getAction()
  {
    return action;
  }

  public ISelection getSelection()
  {
    return selection;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.action = action;
    this.selection = selection;
  }

  public void run(IAction action)
  {
    this.action = action;

    try
    {
      safeRun();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      MessageDialog.openError(null, getText(), ex.getMessage() + "\nSee the Error log for details.");
    }
  }

  protected abstract void safeRun() throws Exception;

  protected String getText()
  {
    return action == null ? "Error" : action.getText();
  }
}