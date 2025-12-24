/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CloseSessionAction extends SessionAction
{
  private static final String TITLE = Messages.getString("CloseSessionAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("CloseSessionAction.1"); //$NON-NLS-1$

  public CloseSessionAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    if (CDOUtil.isSessionDirty(getSession()))
    {
      MessageDialog dialog = new MessageDialog(getShell(), TITLE, null, Messages.getString("CloseSessionAction.2"), //$NON-NLS-1$
          MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
      if (dialog.open() != MessageDialog.OK)
      {
        cancel();
      }
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    getSession().close();
  }
}
