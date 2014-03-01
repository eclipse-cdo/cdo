/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.presentation.actions;

import org.eclipse.emf.cdo.releng.internal.setup.AbstractSetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.presentation.SetupEditorPlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ide.IDE;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class OpenLogAction extends AbstractSetupAction
{
  public OpenLogAction()
  {
  }

  public void run(IAction action)
  {
    try
    {
      File logFile = new File(AbstractSetupTaskContext.getCurrentBranchDir(), "setup.log");
      if (!logFile.exists())
      {
        logFile.createNewFile();
      }

      IDE.openEditor(getWindow().getActivePage(), logFile.toURI(), "org.eclipse.ui.DefaultTextEditor", true);
    }
    catch (Exception ex)
    {
      SetupEditorPlugin.INSTANCE.log(ex);
    }
  }
}
