package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class ShowViewAction extends ViewAction
{
  public ShowViewAction(IWorkbenchPage page, CDOView view)
  {
    super(page, "Show Editor", "Show a CDO editor", null, view);
  }

  @Override
  protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
  {
    CDOEditor.open(page, getView(), null);
  }
}