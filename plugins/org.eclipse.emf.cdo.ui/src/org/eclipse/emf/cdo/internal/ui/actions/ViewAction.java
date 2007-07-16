package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;

import org.eclipse.net4j.ui.actions.LongRunningAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class ViewAction extends LongRunningAction
{
  protected static int lastResourceNumber = 0;

  private CDOView view;

  public ViewAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOView view)
  {
    super(page, text, toolTipText, image);
    this.view = view;
  }

  public CDOView getView()
  {
    return view;
  }
}