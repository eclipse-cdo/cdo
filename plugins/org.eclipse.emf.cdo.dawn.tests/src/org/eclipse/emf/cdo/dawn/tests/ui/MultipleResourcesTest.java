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
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;

import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class MultipleResourcesTest extends AbstractDawnUITest
{
  private static final String A_CLASS = "AClass";

  private static SWTGefBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    bot = new SWTGefBot();
    // DawnSWTBotUtil.initTest(bot);
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
    bot.button("Next >").click();
    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor("default.acore_diagram");
    assertNotNull(editor);

    editor.activateTool(A_CLASS);
    editor.click(100, 100);
    typeTextToFocusedWidget("A", bot);

    editor.saveAndClose();
    // editor.save();

    // create second diagram
    bot.menu("File").menu("New").menu("Other...").click();

    shell = bot.shell("New");
    shell.activate();
    shell.setFocus();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");

    bot.button("Next >").click();
    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("default2.acore_diagram");
    bot.button("Next >").click();

    fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("default.acore");

    bot.button("Finish").click();

    editor = bot.gefEditor("default2.acore_diagram");

    List<SWTBotGefEditPart> editParts = getAClassEditParts(editor);
    assertEquals(0, editParts.size());

    editor.activateTool(A_CLASS);
    editor.click(100, 100);
    typeTextToFocusedWidget("B", bot);

    editor.activateTool(A_CLASS);
    editor.click(300, 300);
    typeTextToFocusedWidget("C", bot);
    editor.click(200, 200);
    editor.saveAndClose();

    openEditor("/default.acore_diagram");

    editor = bot.gefEditor("default.acore_diagram");
    editParts = getAClassEditParts(editor);

    assertEquals(1, editParts.size());

    SWTBotGefEditPart swtBotGefEditPart = editParts.get(0);
    View view = (View)swtBotGefEditPart.part().getModel();

    assertEquals("A", ((AClass)view.getElement()).getName());
  }

  private List<SWTBotGefEditPart> getAClassEditParts(SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> editParts = editor.editParts(new AbstractMatcher<AClassEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        return item instanceof AClassEditPart;
      }

      public void describeTo(Description description)
      {
      }
    });
    return editParts;
  }
}
