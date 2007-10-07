package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenViewEditorAction extends ViewAction
{
  private static final String TITLE = "Open Editor";

  private static final String TOOL_TIP = "Open a CDO editor for this view";

  public OpenViewEditorAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
  }

  @Override
  protected void doRun() throws Exception
  {
    CDOEditor.open(getPage(), getView(), null);
  }
}