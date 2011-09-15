/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * @author Eike Stepper
 */
public class CDOExplorerActionBarAdvisor extends ActionBarAdvisor
{
  private IWorkbenchAction introAction;

  public CDOExplorerActionBarAdvisor(IActionBarConfigurer configurer)
  {
    super(configurer);
  }

  @Override
  protected void makeActions(IWorkbenchWindow window)
  {
    introAction = ActionFactory.INTRO.create(window);
    register(introAction);
  }

  @Override
  protected void fillMenuBar(IMenuManager menuBar)
  {
    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    menuBar.add(helpMenu);

    // Help
    helpMenu.add(introAction);
  }
}
