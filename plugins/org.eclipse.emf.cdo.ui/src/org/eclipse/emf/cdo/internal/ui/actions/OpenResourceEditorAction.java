/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.editor.CDOLobEditorInput;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenResourceEditorAction extends ResourceNodeAction
{
  private static final String TITLE = Messages.getString("OpenResourceEditorAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("OpenResourceEditorAction.1"); //$NON-NLS-1$

  private static final String FILE_TITLE = Messages.getString("OpenFileEditorAction.0"); //$NON-NLS-1$

  private static final String FILE_TOOL_TIP = Messages.getString("OpenFileEditorAction.1"); //$NON-NLS-1$

  public OpenResourceEditorAction(IWorkbenchPage page, CDOResourceLeaf resource)
  {
    super(page, resource instanceof CDOResource ? TITLE : FILE_TITLE, resource instanceof CDOResource ? TOOL_TIP : FILE_TOOL_TIP, null, resource);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    final CDOResourceLeaf resource = (CDOResourceLeaf)getResourceNode();
    final IWorkbenchPage page = getPage();

    if (resource instanceof CDOResource)
    {
      CDOView view = resource.cdoView();
      String resourcePath = resource.getPath();
      CDOEditorUtil.openEditor(page, view, resourcePath);
    }
    else if (resource instanceof CDOFileResource)
    {
      Display display = page.getWorkbenchWindow().getShell().getDisplay();
      display.asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            IEditorInput input = new CDOLobEditorInput(resource);
            page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      });
    }
  }
}
