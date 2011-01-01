/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class ReloadViewAction extends ViewAction
{
  private static final String TITLE = Messages.getString("ReloadViewAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("ReloadViewAction.1"); //$NON-NLS-1$

  public ReloadViewAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    if (getView().reload() != 0)
    {
      CDOEditorUtil.refreshEditors(getPage(), getView());
    }
  }
}
