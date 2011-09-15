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
package org.eclipse.emf.cdo.dawn.tests.ui;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnCodeGenerationTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnCodeGenerationTest extends AbstractDawnUITest<SWTWorkbenchBot>
{
  private SWTBotView packageExplorer;

  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();
    prepare();
  }

  @Override
  @After
  public void tearDown() throws Exception
  {
    cleanup();
    super.tearDown();
  }

  private void cleanup()
  {
    deleteProject("org.eclipse.emf.cdo.dawn.examples.acore");
  }

  @Test
  public void testCodeGenerationGMF() throws Exception
  {
    SWTBotView packageExplorer = DawnSWTBotUtil.openView(getBot(), "Java", "Package Explorer");
    packageExplorer.setFocus();

    SWTBotTreeItem modelFolder = getBot().tree().expandNode("org.eclipse.emf.cdo.dawn.examples.acore", "model");

    SWTBotTreeItem gmfgen = modelFolder.expandNode("acore.gmfgen");
    gmfgen.contextMenu("Generate Dawn GenModel").click();

    sleep(5000);

    assertNotNull(modelFolder.getNode("acore.dawngenmodel"));
    SWTBotTreeItem dawngenmodel_gmf = modelFolder.getNode("acore.dawngenmodel_gmf");
    assertNotNull(dawngenmodel_gmf);

    dawngenmodel_gmf.contextMenu("Generate Dawn GMF Fragment").click();

    sleep(10000);

    IProject fragment = ResourcesPlugin.getWorkspace().getRoot()
        .getProject("org.eclipse.emf.cdo.dawn.examples.acore.diagram.dawn");

    assertNotNull(fragment);
    String diagramFolder = "src/org/eclipse/emf/cdo/dawn/examples/acore/diagram";
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, "META-INF/MANIFEST.MF"));
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, "/fragment.xml"));
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, "/build.properties"));

    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/edit/parts/DawnAcoreEditPartFactory.java"));
    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/edit/parts/DawnACoreRootEditPart.java"));
    assertEquals(
        true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder
            + "/edit/policies/DawnACoreRootCanonicalEditPolicy.java"));

    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/part/DawnAcoreCreationWizard.java"));
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/part/DawnAcoreDiagramEditor.java"));
    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/part/DawnAcoreDiagramEditorUtil.java"));
    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/part/DawnAcoreDocumentProvider.java"));

    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/providers/DawnAcoreEditPartProvider.java"));
    assertEquals(true,
        DawnCodeGenerationTestUtil.exists(fragment, diagramFolder + "/providers/DawnAcoreEditPolicyProvider.java"));
    deleteProject("org.eclipse.emf.cdo.dawn.examples.acore.diagram.dawn");
    sleep(2000);
  }

  @Test
  public void testCodeGenerationEMF() throws Exception
  {
    packageExplorer = DawnSWTBotUtil.openView(getBot(), "Java", "Package Explorer");
    packageExplorer.setFocus();

    SWTBotTreeItem modelFolder = getBot().tree().expandNode("org.eclipse.emf.cdo.dawn.examples.acore", "model");

    SWTBotTreeItem gmfgen = modelFolder.expandNode("acore.genmodel");
    gmfgen.contextMenu("Generate Dawn GenModel").click();

    IProject rootProject = ResourcesPlugin.getWorkspace().getRoot()
        .getProject("org.eclipse.emf.cdo.dawn.examples.acore");

    waitUntilExists(rootProject, "model/acore.dawngenmodel", 10000);

    assertEquals(true, DawnCodeGenerationTestUtil.exists(rootProject, "model/acore.dawngenmodel_emf"));

    modelFolder.setFocus();
    SWTBotTreeItem dawngenmodel_emf = modelFolder.getNode("acore.dawngenmodel_emf");

    assertNotNull(dawngenmodel_emf);

    dawngenmodel_emf.contextMenu("Generate Dawn EMF Fragment").click();

    sleep(10000);

    assertNotNull(modelFolder.getNode("acore.dawngenmodel"));
    assertNotNull(modelFolder.getNode("acore.dawngenmodel_emf"));

    IProject fragment = ResourcesPlugin.getWorkspace().getRoot()
        .getProject("org.eclipse.emf.cdo.dawn.examples.acore.editor.dawn");

    assertNotNull(fragment);

    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, "META-INF/MANIFEST.MF"));
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, "/fragment.xml"));
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, "/build.properties"));

    String folder = "src/org/eclipse/emf/cdo/dawn/examples/acore/presentation/";

    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, folder + "DawnAcoreEditor.java"));
    assertEquals(true, DawnCodeGenerationTestUtil.exists(fragment, folder + "DawnAcoreModelWizard.java"));
    deleteProject("org.eclipse.emf.cdo.dawn.examples.acore.editor.dawn");
    sleep(2000);
  }

  private void waitUntilExists(IProject project, String path, int timeout)
  {
    while (timeout > 0)
    {
      if (DawnCodeGenerationTestUtil.exists(project, "model/acore.dawngenmodel"))
      {
        return;
      }
      sleep(1000);
      timeout -= 1000;
    }
    throw new RuntimeException("Could not find " + path + " within " + timeout + " ms.");
  }

  private SWTWorkbenchBot prepare()
  {
    SWTWorkbenchBot bot = getBot();

    SWTBotView pluginsView = DawnSWTBotUtil.openView(bot, "Plug-in Development", "Plug-ins");

    pluginsView.setFocus();

    final SWTBotTree tree = bot.tree();
    final SWTBotTree plugin = tree.select("org.eclipse.emf.cdo.dawn.examples.acore (1.0.0.qualifier)");

    DawnSWTBotUtil.findContextMenu(plugin, "Import As", "Source Project").click();

    // wait here until the import has finished
    sleep(10000);
    return bot;
  }

  private void deleteProject(String projectName)
  {
    SWTWorkbenchBot bot = getBot();
    SWTBotTreeItem project = bot.tree().expandNode(projectName);
    project.contextMenu("Delete").click();
    bot.shell("Delete Resources").activate();
    bot.checkBox().select();
    bot.button("OK").click();
    sleep(2000);
  }
}
