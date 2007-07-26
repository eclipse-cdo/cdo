package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenAuditAction extends SessionAction
{
  public OpenAuditAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, "Open Audit", "Open a historical CDO view", SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR),
        session);
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    getSession().openAudit(new ResourceSetImpl(), System.currentTimeMillis());
  }
}