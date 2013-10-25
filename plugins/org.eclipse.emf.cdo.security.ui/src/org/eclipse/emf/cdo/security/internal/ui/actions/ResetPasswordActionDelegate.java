/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.actions;

import org.eclipse.emf.cdo.security.User;

import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Administrative password reset action on users in the security model.
 * @author Christian W. Damus (CEA LIST)
 */
public class ResetPasswordActionDelegate extends LongRunningActionDelegate
{
  public ResetPasswordActionDelegate()
  {
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    ISelection selection = getSelection();
    if (selection instanceof IStructuredSelection)
    {
      Object selected = ((IStructuredSelection)selection).getFirstElement();
      if (selected instanceof User)
      {
        User user = (User)selected;
        InternalCDOSession session = (InternalCDOSession)user.cdoView().getSession();
        session.resetCredentials(user.getId());
      }
    }
  }
}
