package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOpenViewAction extends SessionAction
{
  public AbstractOpenViewAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOSession session)
  {
    super(page, text, toolTipText, image, session);
  }

  @Override
  public boolean isEnabled()
  {
    return !getSession().getPackageRegistry().isEmpty() && super.isEnabled();
  }
}