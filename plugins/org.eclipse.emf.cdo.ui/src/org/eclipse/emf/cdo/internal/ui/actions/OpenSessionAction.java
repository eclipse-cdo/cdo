/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenSessionDialog;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.session.CDOSessionProvider;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenSessionAction extends LongRunningAction
{
  private static final String TITLE = OpenSessionDialog.TITLE;

  private static final String TOOL_TIP = "Open a new CDO session";

  private CDOSessionProvider sessionProvider;

  public OpenSessionAction(IWorkbenchPage page)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, CDOSessionsView.getAddImageDescriptor());
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenSessionDialog dialog = new OpenSessionDialog(getPage());
    if (dialog.open() == OpenSessionDialog.OK)
    {
      sessionProvider = dialog.getSessionComposite();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      sessionProvider.getSession();
    }
    catch (final RuntimeException ex)
    {
      OM.LOG.error(ex);
      getShell().getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          MessageDialog.openError(getShell(), getText(), "Unable to open a session on the specified repository.\n"
              + ex.getMessage());
        }
      });
    }
  }
}
