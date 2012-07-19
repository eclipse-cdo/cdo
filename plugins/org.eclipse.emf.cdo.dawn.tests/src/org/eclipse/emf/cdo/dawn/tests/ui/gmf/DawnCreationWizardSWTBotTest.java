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
package org.eclipse.emf.cdo.dawn.tests.ui.gmf;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
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
public class DawnCreationWizardSWTBotTest extends AbstractDawnGEFTest
{
  private String resourceFieldLabel = org.eclipse.emf.cdo.dawn.ui.messages.Messages.DawnCreateNewResourceWizardPage_6;

  @Test
  public void testCreateNewDawnDiagram() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();
    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("default.acore_diagram");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.acore"));
      assertEquals(true, resourceExists("/default.acore_diagram"));
    }
  }

  @Test
  public void testCreateNewDawnDiagramBothPages() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");

    getBot().button("Next >").click();
    getBot().button("Next >").click();
    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("default.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/default.acore"));
      assertEquals(true, resourceExists("/default.acore_diagram"));
    }
  }

  @Test
  public void testCreateNewDawnDiagramBothPagesSetName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("test.acore_diagram");

    getBot().button("Next >").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();
  }

  @Test
  public void testCreateNewDawnDiagramBothPagesSetDifferenNames() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("notational.acore_diagram");

    getBot().button("Next >").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    assertEquals("notational.acore", fileSemanticNameLabel.getText());

    fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileSemanticNameLabel.setText("semantic.acore");

    fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    assertEquals("semantic.acore", fileSemanticNameLabel.getText());

    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("notational.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/semantic.acore"));
      assertEquals(true, resourceExists("/notational.acore_diagram"));
    }
  }

  @Test
  public void testCreateNewDawnDiagramEmptyNotationalResourceName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("");

    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(fileNameLabel.widget, null);
    fileNameLabel.setFocus();
    fileNameLabel.typeText("x", 500);
    keyboard.pressShortcut(Keystrokes.BS);
    assertEquals(false, getBot().button("Next >").isEnabled());
    getBot().button("Cancel").click();
  }

  @Test
  public void testCreateNewDawnDiagramEmptySemanticResourceName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("notational.acore_diagram");

    getBot().button("Next >").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    Keyboard keyboard = KeyboardFactory.getDefaultKeyboard(fileSemanticNameLabel.widget, null);
    fileSemanticNameLabel.setFocus();
    fileSemanticNameLabel.typeText("x", 500);

    keyboard.pressShortcut(Keystrokes.BS);
    assertEquals(false, getBot().button("Next >").isEnabled());
    getBot().button("Cancel").click();
  }

  @Test
  public void testCreateNewDawnDiagramSelectFolder() throws Exception
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
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("test.acore_diagram");

    SWTBotTree tree = getBot().tree(0);

    selectFolder(tree.getAllItems(), "folder", false);
    getBot().button("Next >").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
      assertEquals(true, resourceExists("/folder/test.acore_diagram"));
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
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("test.acore_diagram");

    SWTBotText folder = getBot().textWithLabel("Enter or select the parent folder: ");
    folder.setText("/folder");
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";

    getBot().button("Next >").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.acore"));
      assertEquals(true, resourceExists("/folder/test.acore_diagram"));
    }
  }

  @Test
  public void testCreateNewDawnDiagramSelectDifferentFolders() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      resourceSet.createResource(URI.createURI("cdo:/folder1/dummy"));
      resourceSet.createResource(URI.createURI("cdo:/folder2/dummy"));

      transaction.commit();
    }

    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    getBot().button("Next >").click();

    shell = getBot().shell("New Acore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(resourceFieldLabel);
    fileNameLabel.setText("test.acore_diagram");

    SWTBotTree tree = getBot().tree(0);

    selectFolder(tree.getAllItems(), "folder1", false);
    getBot().button("Next >").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(resourceFieldLabel);
    assertEquals("test.acore", fileSemanticNameLabel.getText());

    tree = getBot().tree(0);
    selectFolder(tree.getAllItems(), "folder2", false);

    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("test.acore_diagram");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder1/test.acore_diagram"));
      assertEquals(true, resourceExists("/folder2/test.acore"));
    }
  }
}
