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
  protected void preRun(IWorkbenchPage page) throws Exception
  {
    ePackage = getEPackage(page, getSession());
    if (ePackage == null)
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    packageRegistry.putEPackage(ePackage);
  }

  protected abstract EPackage getEPackage(IWorkbenchPage page, CDOSession session);
}