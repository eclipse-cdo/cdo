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

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class ReloadViewAction extends ViewAction
{
  private static final String TITLE = "Reload";

  private static final String TOOL_TIP = "Reload this view";

  public ReloadViewAction(IWorkbenchPage page, CDOView view)
  {
    super(page, TITLE, TOOL_TIP, null, view);
  }

  @Override
  protected void doRun() throws Exception
  {
    if (getView().reload() != 0)
    {
      CDOEditor.refresh(getPage(), getView());
    }
  }
}
