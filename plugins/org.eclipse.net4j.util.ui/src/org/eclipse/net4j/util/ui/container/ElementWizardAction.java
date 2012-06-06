package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class ElementWizardAction extends LongRunningAction
{
  private Shell shell;

  private String title;

  private String productGroup;

  private String factoryType;

  private String description;

  private IManagedContainer container;

  public ElementWizardAction(Shell shell, String title, String toolTip, ImageDescriptor image, String productGroup,
      IManagedContainer container)
  {
    super(title, toolTip, image);
    this.shell = shell;
    this.title = title;
    this.productGroup = productGroup;
    this.container = container;
  }

  public ElementWizardAction(Shell shell, String title, String toolTip, ImageDescriptor image, String productGroup)
  {
    this(shell, title, toolTip, image, productGroup, IPluginContainer.INSTANCE);
  }

  @Override
  protected void preRun() throws Exception
  {
    ElementWizardDialog dialog = new ElementWizardDialog(shell, title, productGroup)
    {
      @Override
      protected IManagedContainer getContainer()
      {
        return container;
      }
    };

    if (dialog.open() == ElementWizardDialog.OK)
    {
      factoryType = dialog.getFactoryType();
      description = dialog.getDescription();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      container.getElement(productGroup, factoryType, description);
    }
    catch (final RuntimeException ex)
    {
      OM.LOG.error(ex);
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          MessageDialog.openError(shell, title, "An error occured: " + ex.getMessage());
        }
      });
    }
  }
}
