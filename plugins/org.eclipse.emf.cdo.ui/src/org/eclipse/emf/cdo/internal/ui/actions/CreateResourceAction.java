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

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class CreateResourceAction extends ViewAction
{
  private static final String TITLE = "Create Resource";

  private static final String TOOL_TIP = "Create a CDO resource";

  private String resourcePath;

  public CreateResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, view);
  }

  @Override
  protected void preRun() throws Exception
  {
    InputDialog dialog = new InputDialog(getShell(), TITLE, "Enter resource path:", "/res"
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
  protected void doRun() throws Exception
  {
    getTransaction().createResource(resourcePath);
    CDOEditor.open(getPage(), getView(), resourcePath);
  }
}
