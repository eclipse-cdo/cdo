package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class RegisterPackageAction extends SessionAction
{
  private EPackage ePackage;

  public RegisterPackageAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOSession session)
  {
    super(page, text, toolTipText, image, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    ePackage = getEPackage(getPage(), getSession());
    if (ePackage == null)
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    packageRegistry.putEPackage(ePackage);
    postRegistration(ePackage);
  }

  protected void postRegistration(EPackage ePackage)
  {
  }

  protected abstract EPackage getEPackage(IWorkbenchPage page, CDOSession session);
}