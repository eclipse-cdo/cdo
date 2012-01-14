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
package org.eclipse.emf.cdo.dawn.tests.ui.emf;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;
import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnEMFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnEMFEditorBot;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotEMFEditor;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class DawnEMFHandleEditorTest extends AbstractDawnEMFTest
{
  @Override
  @Before
  public void setUp() throws Exception
  {
    setBot(new DawnEMFEditorBot());
    DawnSWTBotUtil.initTest(getBot());
    super.setUp();
  }

  @Test
  public void testOpenExistingResource() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/default.acore");
      ACoreRoot aCoreRoot = AcoreFactory.eINSTANCE.createACoreRoot();
      aCoreRoot.setTitle("root");
      resource.getContents().add(aCoreRoot);
      AClass aClass = AcoreFactory.eINSTANCE.createAClass();
      aClass.setName("MyClass");

      AInterface aInterface = AcoreFactory.eINSTANCE.createAInterface();
      aInterface.setName("MyInterface");

      aCoreRoot.getClasses().add(aClass);
      aCoreRoot.getInterfaces().add(aInterface);
      transaction.commit();
      transaction.close();
      session.close();
    }
    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOConnectionUtil.instance.openSession();
    DawnSWTBotEMFEditor editor = DawnAcoreTestUtil.openAcoreEMFEditor(URI.createURI("cdo://repo1/default.acore"),
        getBot());
    assertNotNull(editor);
    SWTBotTree tree = editor.getSelectionPageTree();

    sleep(1000);

    SWTBotTreeItem treeItem = tree.getTreeItem("cdo://repo1/default.acore");
    treeItem.expand();

    SWTBotTreeItem root = treeItem.getItems()[0];
    root.expand();

    SWTBotTreeItem swtBotTreeItem1 = root.getItems()[0];
    assertEquals("AClass MyClass", swtBotTreeItem1.getText());
    SWTBotTreeItem swtBotTreeItem2 = root.getItems()[1];
    assertEquals("AInterface MyInterface", swtBotTreeItem2.getText());

    editor.save();

    editor.close();
  }

  @Test
  public void testCreateNewElement() throws Exception
  {
    DawnSWTBotEMFEditor editor = DawnAcoreTestUtil.openNewAcoreEMFEditor("default.acore", getBot());
    assertNotNull(editor);
    SWTBotTree tree = editor.getSelectionPageTree();

    selectFolder(tree.getAllItems(), "ACore Root", false);

    editor.clickContextMenu(tree.widget, "AClass");
    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/default.acore");
      ACoreRoot aCoreRoot = (ACoreRoot)resource.getContents().get(0);

      assertEquals(1, aCoreRoot.getClasses().size());
      assertEquals(null, aCoreRoot.getClasses().get(0).getName());
    }
    editor.close();
  }
}
