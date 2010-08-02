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

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.gmf.runtime.notation.Diagram;
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
public class SimpleDiagramTest extends AbstractDawnUITest
{
  private static final String A_CLASS = "AClass";

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
  public void createNewDawnDiagramAndAddElements() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();
    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("default.acore_diagram");
    assertNotNull(editor);

    editor.activateTool(A_CLASS);
    editor.click(100, 100);
    typeTextToFocusedWidget("A", bot);

    editor.activateTool(A_CLASS);
    // editor.drag(400, 50, 450, 100);
    editor.click(250, 100);
    typeTextToFocusedWidget("B", bot);

    editor.activateTool(A_CLASS);
    // editor.drag(250, 250, 100, 100);
    editor.click(150, 250);
    typeTextToFocusedWidget("C", bot);

    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");
      // TODO: Fix this naming after BUG 321024 is solved.
      CDOResource semanticResource = view.getResource("/default.");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, semanticRoot.getClasses().size());

      Character name = 'A';

      for (AClass aClass : semanticRoot.getClasses())
      {
        assertEquals(name.toString(), aClass.getName());
        name++;
      }
    }
  }
}
