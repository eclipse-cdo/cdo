package org.eclipse.emf.cdo.ui.internal.location;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class NewRepositoryDialog extends TitleAreaDialog
{
  public NewRepositoryDialog(IWorkbenchPage page)
  {
    super(page.getWorkbenchWindow().getShell());
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("New Repository");
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("New Repository");
    // setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));
    return null;
  }
}
