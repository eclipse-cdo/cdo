package org.eclipse.emf.cdo.ui.internal.workspace;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class CheckoutDialog extends TitleAreaDialog
{
  private Text projectNameText;

  private String projectName;

  public CheckoutDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  public String getProjectName()
  {
    return projectName;
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Check out");
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("Check out");
    // setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));

    // Group group1 = new Group(composite, SWT.NONE);
    // group1.setLayout(new GridLayout(1, false));
    // group1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    // group1.setText("Connection");
    // connectorWizard = new ElementWizardComposite.WithRadios(group1, SWT.NONE, "org.eclipse.net4j.connectors",
    // "Type:");
    // connectorWizard.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Group group2 = new Group(composite, SWT.NONE);
    group2.setLayout(new GridLayout(1, false));
    group2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    group2.setText("Repository");
    projectNameText = new Text(group2, SWT.BORDER);
    projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    return composite;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected void okPressed()
  {
    projectName = projectNameText.getText();
    super.okPressed();
  }
}
