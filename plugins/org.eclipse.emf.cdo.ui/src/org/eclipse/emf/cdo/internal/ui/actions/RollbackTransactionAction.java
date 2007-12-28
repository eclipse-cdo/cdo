/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.dialogs.RollbackTransactionDialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class RollbackTransactionAction extends ViewAction
{
  private static final String TITLE = "Rollback";

  private static final String TOOL_TIP = "Rollback this transaction";

  private boolean remote;

  public RollbackTransactionAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void preRun() throws Exception
  {
    CDOTransaction transaction = (CDOTransaction)getView();
    Dialog dialog = new RollbackTransactionDialog(getPage(), TITLE, "Choose how to rollback this transaction.",
        transaction);
    switch (dialog.open())
    {
    case RollbackTransactionDialog.REMOTE:
      remote = true;
      break;
    case RollbackTransactionDialog.LOCAL:
      remote = false;
      break;
    default:
      cancel();
      break;
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    getTransaction().rollback(remote);
  }
}
