/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.internal.ui.actions.RegisterFilesystemPackagesAction;

/**
 * @author Victor Roldan Betancort
 */
public class RegisterFileSystemPackageActionDelegate extends SessionAwareActionDelegate
{
  public RegisterFileSystemPackageActionDelegate()
  {
  }

  @Override
  protected void safeRun() throws Exception
  {
    new RegisterFilesystemPackagesAction(getPage(), getSession()).run();
  }
}
