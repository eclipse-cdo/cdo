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
package org.eclipse.emf.cdo.dawn.tests.bugzillas;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class Bugzilla_321024_Test extends AbstractDawnUITest
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
    super.tearDown();
  }

  @Test
  public void createNewDawnDiagramEmptySemanticResourceName() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();
    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("default.acore_diagram");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.acore"));
      assertEquals(true, resourceExists("/default.acore_diagram"));
    }
  }
}
