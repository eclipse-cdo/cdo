package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenTransactionAction extends AbstractOpenViewAction
{
  private static final String TITLE = "Open Transaction";

  private static final String TOOL_TIP = "Open a read-write CDO view";

  public OpenTransactionAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR), session);
  }

  @Override
  protected void doRun() throws Exception
  {
    getSession().openTransaction(new ResourceSetImpl());
  }
}