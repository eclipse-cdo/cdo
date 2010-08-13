/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.ui;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * @author Martin Fluegge
 */

public abstract class AbstractCDOUITest extends AbstractCDOTest
{
  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    SWTBotPreferences.SCREENSHOTS_DIR = System.getProperty("java.io.tmpdir") + "/cdotests";
  }

  protected void closeAllEditors()
  {
    UIThreadRunnable.syncExec(new VoidResult()
    {
      public void run()
      {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
      }
    });
  }

  protected void resetWorkbench()
  {
    UIThreadRunnable.syncExec(new VoidResult()
    {
      public void run()
      {
        try
        {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
          IWorkbenchPage page = workbenchWindow.getActivePage();
          Shell activeShell = Display.getCurrent().getActiveShell();

          if (activeShell != workbenchWindow.getShell())
          {
            activeShell.close();
          }

          page.closeAllEditors(false);
          page.resetPerspective();

          String defaultPerspectiveId = workbench.getPerspectiveRegistry().getDefaultPerspective();
          workbench.showPerspective(defaultPerspectiveId, workbenchWindow);

          page.resetPerspective();
        }
        catch (WorkbenchException e)
        {
          throw new RuntimeException(e);
        }
      }
    });
  }

  /**
   * walks true the tree and selects the first element which matches the name
   */
  protected void selectFolder(SWTBotTreeItem[] items, String name, boolean exactMatch)
  {
    for (SWTBotTreeItem item : items)
    {
      if (exactMatch)
      {
        if (item.getText().equals(name))
        {
          item.select();
          return;
        }
      }
      else
      {
        if (item.getText().contains(name))
        {
          item.select();
          return;
        }
      }
      item.expand();
      selectFolder(item.getItems(), name, exactMatch);
    }
  }

  protected void typeTextToFocusedWidget(String text, SWTBot bot, boolean hitCR)
  {
    Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
    bot.getFocusedWidget();
    keyboard.typeText(text, 100);
    if (hitCR)
    {
      keyboard.pressShortcut(Keystrokes.CR);
    }
  }
}
