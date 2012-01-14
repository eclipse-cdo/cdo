/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistryPopulator;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenSessionDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenSessionAction extends LongRunningAction
{
  private static final String TITLE = OpenSessionDialog.TITLE;

  private static final String TOOL_TIP = Messages.getString("OpenSessionAction.0"); //$NON-NLS-1$

  private SessionComposite sessionComposite;

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
      sessionComposite = dialog.getSessionComposite();
      sessionComposite.rememberSettings();
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
      String description = sessionComposite.getSessionDescription();
      final InternalCDOSession session = (InternalCDOSession)getContainer().getElement("org.eclipse.emf.cdo.sessions", //$NON-NLS-1$
          "cdo", description); //$NON-NLS-1$

      if (sessionComposite.isAutomaticRegistry())
      {
        CDOPackageRegistryPopulator.populate(session.getPackageRegistry());
      }
    }
    catch (final RuntimeException ex)
    {
      OM.LOG.error(ex);
      getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          MessageDialog.openError(getShell(), getText(), Messages.getString("OpenSessionAction.3") //$NON-NLS-1$
              + ex.getMessage());
        }
      });
    }
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
