package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import java.util.List;

/**
 * @author Eike Stepper
 * @ADDED
 */
public class ImportResourceAction extends EditingDomainAction
{
  private static final String TITLE = "Import Resource";

  private List<URI> uris;

  public ImportResourceAction()
  {
    super("Import Resource...");
  }

  @Override
  protected void preRun() throws Exception
  {
    ResourceDialog dialog = new ResourceDialog(getShell(), TITLE, SWT.OPEN | SWT.MULTI)
    {
      @Override
      protected boolean processResources()
      {
        return true;
      }
    };

    if (dialog.open() == ResourceDialog.OK)
    {
      uris = dialog.getURIs();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor monitor) throws Exception
  {
    MessageDialog.openInformation(getShell(), TITLE, uris.toString());
  }
}