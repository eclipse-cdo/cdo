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

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenViewAction extends AbstractOpenViewAction
{
  private static final String TITLE = Messages.getString("OpenViewAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenViewAction.1"); //$NON-NLS-1$

  public OpenViewAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR), session);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    getSession().openView();
  }
}
