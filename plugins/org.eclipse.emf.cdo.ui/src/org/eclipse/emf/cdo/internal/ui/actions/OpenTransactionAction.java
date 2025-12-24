/*
 * Copyright (c) 2007-2013, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenTransactionAction extends AbstractOpenViewAction
{
  private static final String TITLE = Messages.getString("OpenTransactionAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenTransactionAction.1"); //$NON-NLS-1$

  public OpenTransactionAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR), session);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOSession session = getSession();
    openTransaction(session);
  }

  public static CDOTransaction openTransaction(CDOSession session)
  {
    CDOTransaction transaction = session.openTransaction();
    CDOUtil.configureView(transaction);
    return transaction;
  }
}
