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
package org.eclipse.emf.cdo.security.internal.ui.handlers;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.handlers.LongRunningHandler;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;

/**
 * Administrative password reset command handler on users in the security model.
 * 
 * @author Christian W. Damus (CEA LIST)
 */
public class ResetPasswordHandler extends LongRunningHandler
{
  private User user;

  public ResetPasswordHandler()
  {
  }

  @Override
  protected void doExecute(IProgressMonitor progressMonitor) throws Exception
  {
    if (user != null)
    {
      InternalCDOSession session = (InternalCDOSession)user.cdoView().getSession();
      session.resetCredentials(user.getId());
    }
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    user = UIUtil.adaptElement(getSelection(), User.class);
    return user != null && canWrite(user);
  }

  private boolean canWrite(CDOObject object)
  {
    CDOView view = object.cdoView();

    return (view == null || !view.isReadOnly()) && object.cdoPermission().isWritable();
  }
}
