/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IViewPart;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CDOSessionsViewTest extends AbstractCDOUITest<SWTWorkbenchBot>
{
  @Override
  @After
  public void tearDown() throws Exception
  {
    closeAllEditors();
    super.tearDown();
  }

  @Test
  public void testOpenSessionsView() throws Exception
  {
    getBot().menu("Window").menu("Show View").menu("Other...").click();

    SWTBotShell shell = getBot().shell("Show View");
    shell.activate();
    getBot().tree().expandNode("CDO").select("CDO Sessions");
    getBot().button("OK").click();

    SWTBotView activeView = getBot().activeView();
    assertEquals("CDO Sessions", activeView.getViewReference().getTitle());
    IViewPart view = activeView.getViewReference().getView(false);
    assertInstanceOf(CDOSessionsView.class, view);

    // activeView.toolbarButton(OpenSessionDialog.TITLE).click();
    activeView.toolbarButton(org.eclipse.emf.cdo.internal.ui.messages.Messages.getString("OpenSessionAction.0"))
        .click();

    SWTBotShell openSessionDialog = getBot().shell("Open Session");
    openSessionDialog.activate();
    SWTBotCCombo ccomboBox = getBot().ccomboBox(0);
    ccomboBox.setFocus();

    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(ccomboBox.widget, null);
    keyboard.typeText("tcp");
    keyboard.pressShortcut(SWT.SHIFT, '.');
    keyboard.pressShortcut(SWT.SHIFT, '7');
    keyboard.pressShortcut(SWT.SHIFT, '7');
    keyboard.typeText("localhost");

    SWTBotCCombo repositoryNameCcomboBox = getBot().ccomboBox(1);
    repositoryNameCcomboBox.setFocus();

    typeTextToFocusedWidget("repo1", getBot(), false);

    getBot().button("OK").click();

    activeView.setFocus();
    SWTBotTree tree = getBot().tree(0);
    sleep(3000);
    assertEquals(1, tree.getAllItems().length);
    assertEquals("Session repo1 [2]", tree.getAllItems()[0].getText());
  }
}
