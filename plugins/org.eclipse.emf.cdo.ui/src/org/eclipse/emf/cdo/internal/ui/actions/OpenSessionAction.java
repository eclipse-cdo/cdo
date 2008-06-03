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

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenSessionDialog;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;

import org.eclipse.emf.internal.cdo.CDOSessionFactory;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenSessionAction extends LongRunningAction
{
  private static final String TITLE = OpenSessionDialog.TITLE;

  private static final String TOOL_TIP = "Open a new CDO session";

  private String description;

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
      StringBuilder builder = new StringBuilder();
      builder.append(dialog.getServerDescription());
      builder.append("?repositoryName=");
      builder.append(dialog.getRepositoryName());
      if (dialog.isAutomaticPackageRegistry())
      {
        builder.append("&automaticPackageRegistry=true");
      }

      if (dialog.isLegacyModelSupport())
      {
        builder.append("&legacySupportEnabled=true");
      }

      description = builder.toString();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    CDOSession session = null;

    try
    {
      String productGroup = CDOSessionFactory.PRODUCT_GROUP;
      String type = CDOProtocolConstants.PROTOCOL_NAME;
      session = (CDOSession)IPluginContainer.INSTANCE.getElement(productGroup, type, description);
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }

    if (session == null)
    {
      try
      {
        getShell().getDisplay().syncExec(new Runnable()
        {
          public void run()
          {
            try
            {
              MessageDialog.openError(getShell(), getText(), "Unable to open a session on the specified repository.\n"
                  + description);
            }
            catch (RuntimeException ignoe)
            {
            }
          }
        });
      }
      catch (RuntimeException ignoe)
      {
      }
    }
  }
}
