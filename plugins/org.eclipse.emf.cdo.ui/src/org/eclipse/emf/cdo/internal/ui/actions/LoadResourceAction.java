package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class LoadResourceAction extends ViewAction
{
  private String resourcePath;

  public LoadResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, "Load Resource", "Load a CDO resource", null, view);
  }

  @Override
  protected void preRun(IWorkbenchPage page) throws Exception
  {
    String uri = ViewAction.lastResourceNumber == 0 ? "" : "/res" + ViewAction.lastResourceNumber;
    InputDialog dialog = new InputDialog(page.getWorkbenchWindow().getShell(), "Load Resource", "Enter resource path:",
        uri, null);
    if (dialog.open() == InputDialog.OK)
    {
      resourcePath = dialog.getValue();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(final IWorkbenchPage page, IProgressMonitor monitor) throws Exception
  {
    CDOEditor.open(page, getView(), resourcePath);
  }
}