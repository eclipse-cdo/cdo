package org.eclipse.emf.cdo.internal.ui.wizards;

import org.eclipse.net4j.transport.ConnectorException;

import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import java.lang.reflect.InvocationTargetException;

public class NewSessionWizard extends Wizard implements INewWizard
{
  private NewSessionWizardPage page;

  private ISelection selection;

  public NewSessionWizard()
  {
    setNeedsProgressMonitor(true);
  }

  public void addPages()
  {
    page = new NewSessionWizardPage(selection);
    addPage(page);
  }

  public boolean performFinish()
  {
    final String connectorDescription = page.getConnectorDescription();
    final String repositoryName = page.getRepositoryName();
    IRunnableWithProgress op = new IRunnableWithProgress()
    {
      public void run(IProgressMonitor monitor) throws InvocationTargetException
      {
        try
        {
          doFinish(connectorDescription, repositoryName, monitor);
        }
        catch (Exception e)
        {
          throw new InvocationTargetException(e);
        }
        finally
        {
          monitor.done();
        }
      }
    };

    try
    {
      getContainer().run(true, false, op);
    }
    catch (InterruptedException e)
    {
      return false;
    }
    catch (InvocationTargetException e)
    {
      Throwable realException = e.getTargetException();
      MessageDialog.openError(getShell(), "Error", realException.getMessage());
      return false;
    }

    return true;
  }

  private void doFinish(String connectorDescription, String repositoryName, IProgressMonitor monitor)
      throws ConnectorException
  {
    String description = repositoryName + "@" + connectorDescription;
    monitor.beginTask("Opening " + description, 1);
    Container container = ContainerManager.INSTANCE.getContainer();
    CDOContainerAdapter adapter = (CDOContainerAdapter)container.getAdapter("cdoclient");
    adapter.getSession(description);
    monitor.worked(1);

    // monitor.setTaskName("Opening file for editing...");
    // getShell().getDisplay().asyncExec(new Runnable()
    // {
    // public void run()
    // {
    // IWorkbenchPage page =
    // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    // try
    // {
    // IDE.openEditor(page, file, true);
    // }
    // catch (PartInitException e)
    // {
    // }
    // }
    // });
    // monitor.worked(1);
  }

  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    this.selection = selection;
  }
}