package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.net4j.ui.actions.LongRunningAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class SessionAction extends LongRunningAction
{
  private CDOSession session;

  public SessionAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOSession session)
  {
    super(page, text, toolTipText, image);
    this.session = session;
  }

  public CDOSession getSession()
  {
    return session;
  }
}