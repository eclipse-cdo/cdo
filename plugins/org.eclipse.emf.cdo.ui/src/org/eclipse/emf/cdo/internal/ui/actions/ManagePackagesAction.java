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

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.dialogs.PackageManagerDialog;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ManagePackagesAction extends SessionAction
{
  private static final String TITLE = "Package Manager";

  private static final String TOOL_TIP = "Browse and install model packages";

  public ManagePackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    PackageManagerDialog dialog = new PackageManagerDialog(getPage(), getSession());
    dialog.open();
    cancel();
  }

  @Override
  protected void doRun() throws Exception
  {
  }
}
