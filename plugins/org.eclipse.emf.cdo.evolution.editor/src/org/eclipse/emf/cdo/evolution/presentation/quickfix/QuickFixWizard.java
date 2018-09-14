package org.eclipse.emf.cdo.evolution.presentation.quickfix;

import org.eclipse.emf.cdo.evolution.presentation.EvolutionEditorPlugin;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

/**
 * The wizard for quick fixes.
 */
public class QuickFixWizard extends Wizard
{
  private Diagnostic[] selectedDiagnostics;

  private Map<DiagnosticResolution, Collection<Diagnostic>> resolutionsMap;

  private String description;

  private AdapterFactoryEditingDomain editingDomain;

  public QuickFixWizard(String description, Diagnostic[] selectedDiagnostics, Map<DiagnosticResolution, Collection<Diagnostic>> resolutionsMap,
      AdapterFactoryEditingDomain editingDomain)
  {
    this.selectedDiagnostics = selectedDiagnostics;
    this.resolutionsMap = resolutionsMap;
    this.description = description;
    this.editingDomain = editingDomain;

    setNeedsProgressMonitor(true);
    setDefaultPageImageDescriptor(ExtendedImageRegistry.INSTANCE
        .getImageDescriptor(URI.createPlatformPluginURI(EvolutionEditorPlugin.PLUGIN_ID + "/icons/full/wizban/quick_fix.png", true)));
  }

  @Override
  public void addPages()
  {
    super.addPages();
    addPage(new QuickFixPage(description, selectedDiagnostics, resolutionsMap, editingDomain));
  }

  @Override
  public boolean performFinish()
  {
    IRunnableWithProgress finishRunnable = new IRunnableWithProgress()
    {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        IWizardPage[] pages = getPages();
        SubMonitor subMonitor = SubMonitor.convert(monitor, "Fixing", 10 * pages.length + 1);
        subMonitor.worked(1);
        for (IWizardPage page : pages)
        {
          // Allow for cancel event processing
          getShell().getDisplay().readAndDispatch();
          QuickFixPage wizardPage = (QuickFixPage)page;
          wizardPage.performFinish(subMonitor.split(10));
        }
      }
    };

    try
    {
      getContainer().run(false, true, finishRunnable);
    }
    catch (InvocationTargetException e)
    {
      StatusManager.getManager().handle(newStatus(IStatus.ERROR, e.getLocalizedMessage(), e));
      return false;
    }
    catch (InterruptedException e)
    {
      StatusManager.getManager().handle(newStatus(IStatus.ERROR, e.getLocalizedMessage(), e));
      return false;
    }

    return true;
  }

  public static IStatus newStatus(int severity, String message, Throwable exception)
  {
    String statusMessage = message;
    if (message == null || message.trim().length() == 0)
    {
      if (exception == null)
      {
        throw new IllegalArgumentException();
      }
      else if (exception.getMessage() == null)
      {
        statusMessage = exception.toString();
      }
      else
      {
        statusMessage = exception.getMessage();
      }
    }

    return new Status(severity, EvolutionEditorPlugin.PLUGIN_ID, severity, statusMessage, exception);
  }
}
