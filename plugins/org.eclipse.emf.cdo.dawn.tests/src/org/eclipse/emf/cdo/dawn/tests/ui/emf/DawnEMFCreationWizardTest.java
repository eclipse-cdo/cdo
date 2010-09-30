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
package org.eclipse.emf.cdo.dawn.tests.ui.emf;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnEMFCreationWizardTest extends AbstractDawnUITest
{
  private static SWTWorkbenchBot bot;

  // @BeforeClass
  // public static void beforeClass() throws Exception
  // {
  // bot = new SWTGefBot();
  // DawnSWTBotUtil.initTest(bot);
  // }

  @Override
  @Before
  public void setUp() throws Exception
  {
    bot = new SWTWorkbenchBot();
    DawnSWTBotUtil.initTest(bot);
    super.setUp();
  }

  @Override
  @After
  public void tearDown() throws Exception
  {
    super.tearDown();
  }

  @Test
  public void createNewDawnAcoreEditor() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATTION_WIZARD_NAME_EMF);
    bot.button("Next >").click();
    bot.button("Next >").click();

    shell = bot.shell("New");
    shell.activate();

    SWTBotCombo comboBox = bot.comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    bot.button("Finish").click();

    sleep(500);

    SWTBotEditor editor = bot.editorByTitle("default.acore");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.acore"));
    }
  }

  @Test
  public void createNewDawnEditorSetName() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATTION_WIZARD_NAME_EMF);
    bot.button("Next >").click();

    shell = bot.shell("New");
    shell.activate();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    fileSemanticNameLabel.setText("test.acore");
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    bot.button("Next >").click();

    SWTBotCombo comboBox = bot.comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    bot.button("Finish").click();

    SWTBotEditor editor = bot.editorByTitle("test.acore");
    assertNotNull(editor);
    editor.close();
  }

  @Test
  public void createNewDawnEditorWrongResourceName() throws Exception
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATTION_WIZARD_NAME_EMF);
    bot.button("Next >").click();

    shell = bot.shell("New");
    shell.activate();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(fileSemanticNameLabel.widget, null);
    fileSemanticNameLabel.setFocus();
    fileSemanticNameLabel.typeText("x", 500);

    keyboard.pressShortcut(Keystrokes.BS);
    assertEquals(false, bot.button("Next >").isEnabled());
    bot.button("Cancel").click();
  }

  @Test
  public void createNewDawnEditorSelectFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATTION_WIZARD_NAME_EMF);
    bot.button("Next >").click();

    shell = bot.shell("New");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("test.acore");

    SWTBotTree tree = bot.tree(0);

    selectFolder(tree.getAllItems(), "folder", false);

    bot.button("Next >").click();

    SWTBotCombo comboBox = bot.comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    bot.button("Finish").click();

    SWTBotEditor editor = bot.editorByTitle("test.acore");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
    }
  }

  @Test
  public void createNewDawnDiagramTypeFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATTION_WIZARD_NAME_EMF);
    bot.button("Next >").click();

    shell = bot.shell("New");
    shell.activate();

    SWTBotText fileNameLabel = bot.textWithLabel("File name:");
    fileNameLabel.setText("test.acore");

    SWTBotText folder = bot.textWithLabel("Enter or select the parent folder: ");
    folder.setText("/folder");
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

    bot.button("Next >").click();

    SWTBotCombo comboBox = bot.comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    bot.button("Finish").click();

    SWTBotEditor editor = bot.editorByTitle("test.acore");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
    }
  }
}
