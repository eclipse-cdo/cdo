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

import org.eclipse.emf.cdo.internal.ui.LegacyModeRegistry;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenAuditDialog;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenDurableViewDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenDurableViewAction extends AbstractOpenViewAction
{
  private static final String TITLE = Messages.getString("OpenDurableViewAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenDurableViewAction.1"); //$NON-NLS-1$

  private String areaID;

  public OpenDurableViewAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR), session);
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenDurableViewDialog dialog = new OpenDurableViewDialog(getPage());
    if (dialog.open() == OpenAuditDialog.OK)
    {
      areaID = dialog.getAreaID();
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
      CDOSession session = getSession();
      CDOUtil.setLegacyModeDefault(LegacyModeRegistry.isLegacyEnabled(session));

      CDOTransaction transaction = session.openTransaction(areaID);
      new CDOTransactionCommentator(transaction);
    }
    catch (IllegalStateException ex)
    {
      getSession().openView(areaID);
    }
  }
}
