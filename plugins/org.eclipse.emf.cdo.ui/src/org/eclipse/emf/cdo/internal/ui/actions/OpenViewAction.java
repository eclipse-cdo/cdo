package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenViewAction extends SessionAction
{
  public OpenViewAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, "Open View", "Open a read-write CDO view", SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR),
        session);
  }

  @Override
  protected void doRun(IWorkbenchPage page, IProgressMonitor monitor) throws Exception
  {
    getSession().openTransaction(new ResourceSetImpl());
  }
}