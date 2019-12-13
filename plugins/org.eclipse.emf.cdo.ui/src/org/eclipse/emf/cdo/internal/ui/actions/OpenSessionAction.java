/*
 * Copyright (c) 2007-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistryPopulator;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenSessionDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.security.NotAuthenticatedException;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenSessionAction extends LongRunningAction
{
  private static final String TITLE = OpenSessionDialog.TITLE;

  private static final String TOOL_TIP = Messages.getString("OpenSessionAction.0"); //$NON-NLS-1$

  private SessionComposite sessionComposite;

  public OpenSessionAction(IWorkbenchPage page)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, CDOSessionsView.getAddImageDescriptor());
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenSessionDialog dialog = new OpenSessionDialog(getPage());
    if (dialog.open() == OpenSessionDialog.OK)
    {
      sessionComposite = dialog.getSessionComposite();
      sessionComposite.rememberSettings();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      IManagedContainer container = getContainer();
      String productGroup = CDOSessionFactory.PRODUCT_GROUP;
      String description = sessionComposite.getSessionDescription();

      String userID = org.eclipse.emf.internal.cdo.bundle.OM.PREF_USER_NAME.getValue();
      if (userID != null)
      {
        description += "&userID=" + userID;
      }

      CDOSession session = (CDOSession)container.getElement(productGroup, "cdo", description); //$NON-NLS-1$

      if (sessionComposite.isAutomaticRegistry())
      {
        CDOPackageRegistryPopulator.populate(session.getPackageRegistry());
      }
    }
    catch (RemoteException ex)
    {
      Throwable cause = ex.getCause();
      handleError(cause);
    }
    catch (Exception ex)
    {
      handleError(ex);
    }
  }

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
        MessageDialog.openError(getShell(), getText(), Messages.getString("OpenSessionAction.3") //$NON-NLS-1$
            + ex.getMessage());
      }
    });
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
