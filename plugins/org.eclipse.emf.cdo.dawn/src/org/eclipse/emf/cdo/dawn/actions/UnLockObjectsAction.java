/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
public class UnLockObjectsAction implements IObjectActionDelegate
{
  public final static String ID = "org.eclipse.emf.cdo.dawn.UnlockObjectsAction";

  public UnLockObjectsAction()
  {
  }

  public void run(IAction action)
  {
    MessageDialog.openInformation(DawnEditorHelper.getActiveShell(), "", "Locking not yet supported");
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }
}
