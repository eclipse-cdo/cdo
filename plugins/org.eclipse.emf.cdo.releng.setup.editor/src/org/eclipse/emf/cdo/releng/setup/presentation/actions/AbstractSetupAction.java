/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Eike Stepper
 */
public abstract class AbstractSetupAction implements IWorkbenchWindowActionDelegate
{
  private IWorkbenchWindow window;

  private boolean initialized;

  public AbstractSetupAction()
  {
  }

  public void init(IWorkbenchWindow window)
  {
    this.window = window;
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    if (!initialized)
    {
      boolean exists = AbstractSetupTaskContext.existsCurrentSetup();
      action.setEnabled(exists);
      initialized = true;
    }
  }

  public void dispose()
  {
  }

  public IWorkbenchWindow getWindow()
  {
    return window;
  }
}
