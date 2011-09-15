/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.emf;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnEMFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

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

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnEMFCreationWizardTest extends AbstractDawnEMFTest
{
  private String resourceFieldLabel = org.eclipse.emf.cdo.dawn.ui.messages.Messages.DawnCreateNewResourceWizardPage_6;

  @Test
  public void testCreateNewDawnAcoreEditor() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATION_WIZARD_NAME_EMF);
    getBot().button("Next >").click();
    getBot().button("Next >").click();

    shell = getBot().shell("New");
    shell.activate();

    SWTBotCombo comboBox = getBot().comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    getBot().button("Finish").click();

    sleep(500);

    SWTBotEditor editor = getBot().editorByTitle("default.acore");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.acore"));
    }
  }

  @Test
  public void testCreateNewDawnEditorSetName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATION_WIZARD_NAME_EMF);
    getBot().button("Next >").click();

    shell = getBot().shell("New");
    shell.activate();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileSemanticNameLabel.setText("test.acore");
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    getBot().button("Next >").click();

    SWTBotCombo comboBox = getBot().comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    getBot().button("Finish").click();

    SWTBotEditor editor = getBot().editorByTitle("test.acore");
    assertNotNull(editor);
    editor.close();
  }

  @Test
  public void testCreateNewDawnEditorWrongResourceName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATION_WIZARD_NAME_EMF);
    getBot().button("Next >").click();

    shell = getBot().shell("New");
    shell.activate();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(fileSemanticNameLabel.widget, null);
    fileSemanticNameLabel.setFocus();
    fileSemanticNameLabel.setText("x");
    // fileSemanticNameLabel.typeText("x", 500);
    keyboard.pressShortcut(Keystrokes.BS);

    assertEquals(false, getBot().button("Next >").isEnabled());
    getBot().button("Cancel").click();
  }

  @Test
  public void testCreateNewDawnEditorSelectFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATION_WIZARD_NAME_EMF);
    getBot().button("Next >").click();

    shell = getBot().shell("New");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("test.acore");

    SWTBotTree tree = getBot().tree(0);

    selectFolder(tree.getAllItems(), "folder", false);

    getBot().button("Next >").click();

    SWTBotCombo comboBox = getBot().comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    getBot().button("Finish").click();

    SWTBotEditor editor = getBot().editorByTitle("test.acore");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
    }
  }

  @Test
  public void testCreateNewDawnDiagramTypeFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnAcoreTestUtil.CREATION_WIZARD_NAME_EMF);
    getBot().button("Next >").click();

    shell = getBot().shell("New");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("test.acore");

    SWTBotText folder = getBot().textWithLabel("Enter or select the parent folder: ");
    folder.setText("/folder");
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

    getBot().button("Next >").click();

    SWTBotCombo comboBox = getBot().comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("ACore Root");

    getBot().button("Finish").click();

    SWTBotEditor editor = getBot().editorByTitle("test.acore");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
    }
  }
}
