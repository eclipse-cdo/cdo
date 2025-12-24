/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.actions;

import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Martin Fluegge
 */
public class LockObjectsAction implements IObjectActionDelegate
{
  public final static String ID = "org.eclipse.emf.cdo.dawn.LockObjectsAction";

  public LockObjectsAction()
  {
  }

  @Override
  public void run(IAction action)
  {
    MessageDialog.openInformation(DawnEditorHelper.getActiveShell(), "", "Locking not yet supported.");
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }
}
