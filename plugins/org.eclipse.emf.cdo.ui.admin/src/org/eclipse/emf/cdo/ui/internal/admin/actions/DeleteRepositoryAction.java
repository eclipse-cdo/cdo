/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin.actions;

import org.eclipse.emf.cdo.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.ui.internal.admin.messages.Messages;

import org.eclipse.net4j.ui.shared.SharedIcons;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;

import java.text.MessageFormat;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class DeleteRepositoryAction extends AdminAction<CDOAdminClientRepository>
{
  public DeleteRepositoryAction(CDOAdminClientRepository repository)
  {
    super(Messages.DeleteRepositoryAction_1, Messages.DeleteRepositoryAction_2, SharedIcons.getDescriptor(SharedIcons.ETOOL_DELETE), repository);
  }

  @Override
  protected void preRun() throws Exception
  {
    final int NO_BUTTON = 1;
    String message = MessageFormat.format(Messages.DeleteRepositoryAction_4, target.getName());
    MessageDialog dlg = new MessageDialog(getShell(), getText(), null, message, MessageDialog.WARNING,
        new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, NO_BUTTON);
    if (dlg.open() == NO_BUTTON)
    {
      cancel();
    }
  }

  @Override
  protected void safeRun(IProgressMonitor progressMonitor) throws Exception
  {
    target.delete(CDOAdmin.DEFAULT_TYPE);
  }

  @Override
  protected String getErrorPattern()
  {
    return Messages.DeleteRepositoryAction_3;
  }
}
