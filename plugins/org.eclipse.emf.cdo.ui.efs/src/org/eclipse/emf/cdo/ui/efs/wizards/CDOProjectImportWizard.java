package org.eclipse.emf.cdo.ui.efs.wizards;

import org.eclipse.net4j.util.ui.container.ElementWizardComposite;
import org.eclipse.net4j.util.ui.container.IElementWizard.ValidationContext;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Martin Fluegge
 */
public class CDOProjectImportWizard extends Wizard implements IImportWizard
{
  public CDOProjectImportWizard()
  {
  }

  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
  }

  @Override
  public boolean performFinish()
  {
    return false;
  }

  @Override
  public void addPages()
  {
    addPage(new Page());
  }

  /**
   * @author Eike Stepper
   */
  public static class Page extends WizardPage implements ValidationContext
  {
    public Page()
    {
      super("CDOProjectImportWizardPage");
    }

    public void createControl(Composite parent)
    {
      Group group = new Group(parent, SWT.NONE);
      group.setText("Connection");
      group.setLayout(new FillLayout());

      new ElementWizardComposite.WithRadios(group, SWT.NONE, "org.eclipse.net4j.connectors", "Type:");
      setControl(group);
    }

    public void setValidationOK()
    {
      setMessage(null);
    }

    public void setValidationError(Control control, String message)
    {
      setMessage(message, IMessageProvider.ERROR);
    }
  }
}
