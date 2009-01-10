/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.core.runtime.IProgressMonitor;
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
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOTransaction transaction = getSession().openTransaction();
    // transaction.options().getConflictResolvers().add(new CDOObjectConflictResolver.MergeLocalChangesPerFeature());
  }
}
