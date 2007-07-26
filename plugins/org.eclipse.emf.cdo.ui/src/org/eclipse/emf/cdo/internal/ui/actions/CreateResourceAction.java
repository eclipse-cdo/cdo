package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CreateResourceAction extends ViewAction
{
  private static final String TITLE = "Create Resource";

  private String resourcePath;

  public CreateResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, "Create a CDO resource", null, view);
  }

  @Override
  protected void preRun() throws Exception
  {
    InputDialog dialog = new InputDialog(getShell(), TITLE, "Enter resource path:", "/res"
        + (ViewAction.lastResourceNumber + 1), null);
    if (dialog.open() == InputDialog.OK)
    {
      ++ViewAction.lastResourceNumber;
      resourcePath = dialog.getValue();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    getTransaction().createResource(resourcePath);
    CDOEditor.open(getPage(), getView(), resourcePath);
  }
}