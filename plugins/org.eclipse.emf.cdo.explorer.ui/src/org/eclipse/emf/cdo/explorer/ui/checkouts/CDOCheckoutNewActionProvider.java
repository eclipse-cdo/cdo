/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.navigator.WizardActionGroup;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutNewActionProvider extends CommonActionProvider
{
  private static final String NEW_MENU_NAME = "common.new.menu"; //$NON-NLS-1$

  private ActionFactory.IWorkbenchAction showDlgAction;

  private WizardActionGroup newWizardActionGroup;

  private boolean contribute = false;

  public CDOCheckoutNewActionProvider()
  {
  }

  @Override
  public void init(ICommonActionExtensionSite anExtensionSite)
  {
    if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite)
    {
      IWorkbenchWindow window = ((ICommonViewerWorkbenchSite)anExtensionSite.getViewSite()).getWorkbenchWindow();
      showDlgAction = ActionFactory.NEW.create(window);

      newWizardActionGroup = new WizardActionGroup(window, PlatformUI.getWorkbench().getNewWizardRegistry(),
          WizardActionGroup.TYPE_NEW, anExtensionSite.getContentService());

      contribute = true;
    }
  }

  @Override
  public void fillContextMenu(IMenuManager menu)
  {
    IMenuManager submenu = new MenuManager("&New", NEW_MENU_NAME);
    if (!contribute)
    {
      return;
    }

    // Fill the menu from the commonWizard contributions.
    newWizardActionGroup.setContext(getContext());
    newWizardActionGroup.fillContextMenu(submenu);

    submenu.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));

    // Add other...
    submenu.add(new Separator());
    submenu.add(showDlgAction);

    // Append the submenu after the GROUP_NEW group.
    menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
  }

  @Override
  public void dispose()
  {
    if (showDlgAction != null)
    {
      showDlgAction.dispose();
      showDlgAction = null;
    }

    super.dispose();
  }
}
