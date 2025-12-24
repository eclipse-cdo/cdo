/*
 * Copyright (c) 2007-2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class LoadResourceAction extends AbstractViewAction
{
  private static final String TITLE = Messages.getString("LoadResourceAction.0"); //$NON-NLS-1$

  private String resourcePath;

  public LoadResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, Messages.getString("LoadResourceAction.1"), null, view); //$NON-NLS-1$
  }

  @Override
  protected void preRun() throws Exception
  {
    String uri = AbstractViewAction.lastResourceNumber == 0 ? "" : "/res" + AbstractViewAction.lastResourceNumber; //$NON-NLS-1$ //$NON-NLS-2$
    InputDialog dialog = new InputDialog(getShell(), TITLE, Messages.getString("LoadResourceAction.4"), uri, null); //$NON-NLS-1$
    if (dialog.open() == InputDialog.OK)
    {
      resourcePath = dialog.getValue();
      if (!getView().hasResource(resourcePath))
      {
        MessageDialog.openError(getShell(), Messages.getString("LoadResourceAction.2"), MessageFormat.format( //$NON-NLS-1$
            Messages.getString("LoadResourceAction.3"), resourcePath)); //$NON-NLS-1$
        cancel();
      }
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    CDOEditorUtil.openEditor(getPage(), getView(), resourcePath);
  }
}
