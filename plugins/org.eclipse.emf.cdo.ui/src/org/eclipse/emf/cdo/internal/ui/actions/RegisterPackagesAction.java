package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class RegisterPackagesAction extends SessionAction
{
  private List<EPackage> ePackages;

  public RegisterPackagesAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOSession session)
  {
    super(page, text, toolTipText, image, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    ePackages = getEPackages(getPage(), getSession());
    if (ePackages == null)
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    for (EPackage ePackage : ePackages)
    {
      packageRegistry.putEPackage(ePackage);
    }

    postRegistration(ePackages);
  }

  protected void postRegistration(List<EPackage> ePackages)
  {
  }

  protected abstract List<EPackage> getEPackages(IWorkbenchPage page, CDOSession session);
}