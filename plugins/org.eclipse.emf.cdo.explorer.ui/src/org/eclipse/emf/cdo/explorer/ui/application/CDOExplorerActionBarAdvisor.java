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
package org.eclipse.emf.cdo.explorer.ui.application;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
    if (window.getWorkbench().getIntroManager().hasIntro())
    {
      introAction = ActionFactory.INTRO.create(window);
      register(introAction);
    }
  }

  @Override
  protected void fillMenuBar(IMenuManager menuBar)
  {
    menuBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    // Help
    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    menuBar.add(helpMenu);

    if (introAction != null)
    {
      helpMenu.add(introAction);
    }
  }
}
