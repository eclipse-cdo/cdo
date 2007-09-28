package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenSessionDialog;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.CDOSessionFactory;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenSessionAction extends LongRunningAction
{
  private IWorkbenchPage page;

  private String description;

  public OpenSessionAction(IWorkbenchPage page)
  {
    super(OpenSessionDialog.TITLE, "Open a new CDO session", CDOSessionsView.getAddImageDescriptor());
    this.page = page;
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenSessionDialog dialog = new OpenSessionDialog(page);
    dialog.open();
    description = dialog.getDescription();
    if (description == null)
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    String productGroup = CDOSessionFactory.PRODUCT_GROUP;
    String type = CDOProtocolConstants.PROTOCOL_NAME;
    CDOSession session = (CDOSession)IPluginContainer.INSTANCE.getElement(productGroup, type, description);
    if (session == null)
    {
      MessageDialog.openError(getShell(), getText(), "Could not open a session to the specified repository.");
    }
  }
}