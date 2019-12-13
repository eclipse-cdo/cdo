/*
 * Copyright (c) 2013-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.ui.internal.admin.actions;

import org.eclipse.emf.cdo.ui.internal.admin.bundle.OM;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.security.NotAuthenticatedException;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

import java.text.MessageFormat;

/**
 * @param <T> the type of the target element of the action in the CDO Administration View
 *
 * @author Christian W. Damus (CEA LIST)
 */
public abstract class AdminAction<T> extends LongRunningAction
{
  protected final T target;

  protected AdminAction(String label, String tooltip, ImageDescriptor imageDescriptor, T target)
  {
    super(label, tooltip, imageDescriptor);
    this.target = target;
  }

  @Override
  protected final void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      safeRun(progressMonitor);
    }
    catch (RemoteException ex)
    {
      handleError(ex.getCause() == null ? ex : ex.getCause());
    }
    catch (Exception e)
    {
      handleError(e);
    }
  }

  protected abstract void safeRun(IProgressMonitor progressMonitor) throws Exception;

  protected void handleError(Throwable ex)
  {
    if (ex instanceof NotAuthenticatedException)
    {
      // Skip silently because user has canceled the authentication
    }
    else
    {
      showError(ex);
    }
  }

  protected void showError(final Throwable ex)
  {
    OM.LOG.error(ex);
    getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        MessageDialog.openError(getShell(), getText(), MessageFormat.format(getErrorPattern(), ex.getLocalizedMessage()));
      }
    });
  }

  protected abstract String getErrorPattern();
}
