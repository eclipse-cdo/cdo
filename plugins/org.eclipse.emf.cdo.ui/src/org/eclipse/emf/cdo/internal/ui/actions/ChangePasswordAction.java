/*
 * Copyright (c) 2013, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * The "change password" action.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public final class ChangePasswordAction extends SessionAction
{
  private static final String TITLE = Messages.getString("ChangePasswordAction_0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("ChangePasswordAction_1"); //$NON-NLS-1$

  public ChangePasswordAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE, TOOL_TIP, null, session);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    getSession().changeServerPassword();
  }
}
