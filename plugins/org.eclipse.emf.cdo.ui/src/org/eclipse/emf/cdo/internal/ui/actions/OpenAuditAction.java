/*
 * Copyright (c) 2007-2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.internal.ui.dialogs.OpenAuditDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenAuditAction extends AbstractOpenViewAction
{
  private static final String TITLE = Messages.getString("OpenAuditAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenAuditAction.1"); //$NON-NLS-1$

  private long timeStamp;

  public OpenAuditAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR), session);
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenAuditDialog dialog = new OpenAuditDialog(getPage());
    if (dialog.open() == OpenAuditDialog.OK)
    {
      timeStamp = dialog.getTimeStamp();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    getSession().openView(timeStamp);
  }
}
