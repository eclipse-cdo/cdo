/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.internal.ui.actions.ManagePackagesAction;

import org.eclipse.jface.action.IAction;

/**
 * @author Victor Roldan Betancort
 */
public class ManagePackagesActionDelegate extends SessionAwareActionDelegate
{
  private IAction action;

  public ManagePackagesActionDelegate()
  {
  }

  @Override
  protected void safeRun() throws Exception
  {
    if (action == null)
    {
      action = new ManagePackagesAction(getPage(), getSession());
    }

    action.run();
  }
}
