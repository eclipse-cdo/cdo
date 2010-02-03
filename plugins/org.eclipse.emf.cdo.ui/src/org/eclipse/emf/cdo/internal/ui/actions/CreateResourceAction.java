/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CreateResourceAction extends ViewAction
{
  private static final String TITLE = Messages.getString("CreateResourceAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("CreateResourceAction.1"); //$NON-NLS-1$

  private String resourcePath;

  public CreateResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, view);
  }

  @Override
  protected void preRun() throws Exception
  {
    InputDialog dialog = new InputDialog(getShell(), TITLE, Messages.getString("CreateResourceAction.2"), "/res" //$NON-NLS-1$ //$NON-NLS-2$
        + (ViewAction.lastResourceNumber + 1), null);
    if (dialog.open() == InputDialog.OK)
    {
      ++ViewAction.lastResourceNumber;
      resourcePath = dialog.getValue();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    final boolean[] getOrCreate = { true };

    if (getTransaction().hasResource(resourcePath))
    {
      UIUtil.getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          getOrCreate[0] = MessageDialog.openQuestion(new Shell(),
              Messages.getString("CreateResourceAction.4"), MessageFormat.format( //$NON-NLS-1$
                  Messages.getString("CreateResourceAction.5"), resourcePath)); //$NON-NLS-1$
        }
      });
    }

    if (getOrCreate[0])
    {
      getTransaction().getOrCreateResource(resourcePath);
      CDOEditorUtil.openEditor(getPage(), getView(), resourcePath);
    }
  }
}
