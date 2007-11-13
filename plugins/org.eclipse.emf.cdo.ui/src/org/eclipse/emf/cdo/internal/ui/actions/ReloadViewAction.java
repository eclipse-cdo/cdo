package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class ReloadViewAction extends ViewAction
{
  private static final String TITLE = "Reload";

  private static final String TOOL_TIP = "Reload this view";

  public ReloadViewAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
  }

  @Override
  protected void doRun() throws Exception
  {
    if (getView().reload() != 0)
    {
      CDOEditor.refresh(getPage(), getView());
    }
  }
}