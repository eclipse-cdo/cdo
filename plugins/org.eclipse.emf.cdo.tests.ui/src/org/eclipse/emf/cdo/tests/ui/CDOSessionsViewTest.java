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

import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;

import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IViewPart;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CDOSessionsViewTest extends AbstractCDOUITest
{
  private static SWTWorkbenchBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    bot = new SWTGefBot();
    bot.viewByTitle("Welcome").close();
  }

  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();
  }

  @Override
  @After
  public void tearDown() throws Exception
  {
    closeAllEditors();
    super.tearDown();
  }

  @Test
  public void openSessionsView() throws Exception
  {
    bot.menu("Window").menu("Show View").menu("Other...").click();

    SWTBotShell shell = bot.shell("Show View");
    shell.activate();
    bot.tree().expandNode("CDO").select("CDO Sessions");
    bot.button("OK").click();

    SWTBotView activeView = bot.activeView();
    assertEquals("CDO Sessions", activeView.getViewReference().getTitle());
    IViewPart view = activeView.getViewReference().getView(false);
    assertInstanceOf(CDOSessionsView.class, view);

    // activeView.toolbarButton(OpenSessionDialog.TITLE).click();
    activeView.toolbarButton(org.eclipse.emf.cdo.internal.ui.messages.Messages.getString("OpenSessionAction.0"))
        .click();

    SWTBotShell openSessionDialog = bot.shell("Open Session");
    openSessionDialog.activate();
    SWTBotCCombo ccomboBox = bot.ccomboBox(0);
    ccomboBox.setFocus();

    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(ccomboBox.widget, null);
    keyboard.typeText("tcp");
    keyboard.pressShortcut(SWT.SHIFT, '.');
    keyboard.pressShortcut(SWT.SHIFT, '7');
    keyboard.pressShortcut(SWT.SHIFT, '7');
    keyboard.typeText("localhost");

    SWTBotCCombo repositoryNameCcomboBox = bot.ccomboBox(1);
    repositoryNameCcomboBox.setFocus();

    typeTextToFocusedWidget("repo1", bot, false);

    bot.button("OK").click();

    activeView.setFocus();
    SWTBotTree tree = bot.tree(0);
    sleep(3000);
    assertEquals(1, tree.getAllItems().length);
    assertEquals("Session repo1 [2]", tree.getAllItems()[0].getText());
  }
}
