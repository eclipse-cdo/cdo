/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.dialogs.PackageRegistryDialog;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public class ManagePackagesAction extends SessionAction
{
  private static final String TITLE = "Package Registry";

  private static final String TOOL_TIP = "Browse and register model packages";

  public ManagePackagesAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, null, session);
  }

  @Override
  protected void preRun() throws Exception
  {
    PackageRegistryDialog dialog = new PackageRegistryDialog(getPage(), getSession());
    dialog.open();
    cancel();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
  }
}
