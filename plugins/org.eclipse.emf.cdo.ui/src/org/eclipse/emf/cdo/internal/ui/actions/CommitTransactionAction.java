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

import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CommitTransactionAction extends ViewAction
{
  private static final String TITLE = "Commit";

  private static final String TOOL_TIP = "Commit this transaction";

  public CommitTransactionAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
    setEnabled(getTransaction().isDirty());
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    getTransaction().commit();
  }
}
