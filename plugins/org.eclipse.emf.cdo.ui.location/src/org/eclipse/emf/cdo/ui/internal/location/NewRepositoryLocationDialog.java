package org.eclipse.emf.cdo.ui.internal.location;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.container.ElementWizardComposite;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class NewRepositoryLocationDialog extends TitleAreaDialog
{
  private ElementWizardComposite connectorWizard;

  public NewRepositoryLocationDialog(Shell parentShell)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  @Override
  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("New Repository Location");
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    setTitle("New Repository Location");
    // setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_PACKAGE_MANAGER));

    connectorWizard = new ElementWizardComposite.WithRadios(parent, SWT.NONE, "org.eclipse.net4j.connectors",
        "Connector:");
    // IPluginContainer container = getContainer();
    // connectorWizard = (IElementWizard)container.getElement("org.eclipse.net4j.util.ui.elementWizards",
    // "org.eclipse.net4j.connectors", null);
    //
    // connectorWizard.create(parent, container, "org.eclipse.net4j.connectors", "", "", null);
    return connectorWizard;
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
