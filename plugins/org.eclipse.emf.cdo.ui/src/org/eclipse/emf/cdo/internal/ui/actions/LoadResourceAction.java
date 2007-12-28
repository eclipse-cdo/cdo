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
public final class LoadResourceAction extends ViewAction
{
  private static final String TITLE = "Load Resource";

  private String resourcePath;

  public LoadResourceAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE + INTERACTIVE, "Load a CDO resource", null, view);
  }

  @Override
  protected void preRun() throws Exception
  {
    String uri = ViewAction.lastResourceNumber == 0 ? "" : "/res" + ViewAction.lastResourceNumber;
    InputDialog dialog = new InputDialog(getShell(), TITLE, "Enter resource path:", uri, null);
    if (dialog.open() == InputDialog.OK)
    {
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
    CDOEditor.open(getPage(), getView(), resourcePath);
  }
}
