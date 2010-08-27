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
package org.eclipse.emf.cdo.dawn.tests.ui;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.dawn.ui.views.DawnExplorer;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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
public class DawnProjectExplorerTest extends AbstractDawnUITest
{
  private static SWTGefBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    bot = new SWTGefBot();
    DawnSWTBotUtil.initTest(bot);
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
  public void testOpenDawnExplorer() throws Exception
  {
    bot.menu("Window").menu("Show View").menu("Other...").click();

    SWTBotShell shell = bot.shell("Show View");
    shell.activate();
    bot.tree().expandNode("Dawn").select("Dawn Explorer");
    bot.button("OK").click();

    SWTBotView activeView = bot.activeView();
    assertEquals("Dawn Explorer", activeView.getViewReference().getTitle());
    IViewPart view = activeView.getViewReference().getView(false);
    assertInstanceOf(DawnExplorer.class, view);
  }
}
