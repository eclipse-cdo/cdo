/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.common.util.CDORenameContext;
import org.eclipse.emf.cdo.explorer.ui.RenameDialog;

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 */
public class RenameHandler extends AbstractBaseHandler<CDORenameContext>
{
  private String name;

  public RenameHandler()
  {
    super(CDORenameContext.class, false);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    if (elements.size() == 1)
    {
      CDORenameContext renameContext = elements.get(0);

      Shell shell = HandlerUtil.getActiveShell(event);
      RenameDialog dialog = new RenameDialog(shell, renameContext);
      if (dialog.open() == RenameDialog.OK)
      {
        name = dialog.getName();
        return;
      }
    }

    cancel();
  }

  @Override
  protected void doExecute(IProgressMonitor monitor) throws Exception
  {
    CDORenameContext renameContext = elements.get(0);
    renameContext.setName(name);
    name = null;
  }
}
