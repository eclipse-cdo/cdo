package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.dialogs.PackageManagerDialog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ManagePackagesAction extends SessionAction
{
  private static final String TITLE = "Package Manager";

  private static final String TOOL_TIP = "Browse and install model packages";

  public ManagePackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    PackageManagerDialog dialog = new PackageManagerDialog(getPage(), getSession());
    dialog.open();
    cancel();
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
  }
}