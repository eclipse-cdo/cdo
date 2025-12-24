/*
 * Copyright (c) 2007-2009, 2011-2013 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CommitTransactionAction extends AbstractViewAction
{
  private static final String TITLE = Messages.getString("CommitTransactionAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("CommitTransactionAction.1"); //$NON-NLS-1$

  public CommitTransactionAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_SAVE), view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    getTransaction().commit();
  }
}
