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

import org.eclipse.emf.cdo.dawn.preferences.PreferenceConstants;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnEMFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnEMFEditorBot;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnEcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotEMFEditor;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class Bugzilla_333291_Test extends AbstractDawnEMFTest
{
  @Override
  @Before
  public void setUp() throws Exception
  {
    setBot(new DawnEMFEditorBot());
    DawnSWTBotUtil.initTest(getBot());
    super.setUp();
  }

  public void testCreateNewDawnEcoreEditor() throws Exception
  {
    SWTBotEditor editor = DawnEcoreTestUtil.openNewEcoreEMFEditor("default.ecore", getBot());
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.ecore"));
    }
  }

  public void testOpenExistingResource() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/default.ecore");
      EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
      ePackage.setName("myPackage");
      ePackage.setNsPrefix("prefix");
      ePackage.setNsURI("http://testURI/2010");
      resource.getContents().add(ePackage);

      EClass aClass = EcoreFactory.eINSTANCE.createEClass();
      aClass.setName("MyClass");

      ePackage.getEClassifiers().add(aClass);

      transaction.commit();
      transaction.close();
      session.close();
    }

    CDOConnectionUtil.instance.init(PreferenceConstants.getRepositoryName(), PreferenceConstants.getProtocol(),
        PreferenceConstants.getServerName());
    CDOConnectionUtil.instance.openSession();
    DawnSWTBotEMFEditor editor = DawnEcoreTestUtil.openEcoreEMFEditor(URI.createURI("cdo://repo1/default.ecore"),
        getBot());
    assertNotNull(editor);
    SWTBotTree tree = editor.getSelectionPageTree();

    sleep(1000);

    SWTBotTreeItem treeItem = tree.getTreeItem("cdo://repo1/default.ecore");
    treeItem.expand();

    SWTBotTreeItem root = treeItem.getItems()[0];
    assertEquals("myPackage", root.getText());
    root.expand();

    SWTBotTreeItem swtBotTreeItem1 = root.getItems()[0];
    assertEquals("MyClass", swtBotTreeItem1.getText());

    editor.save();

    editor.close();
  }

  public void testCreateNewPackage() throws Exception
  {
    DawnSWTBotEMFEditor editor = DawnEcoreTestUtil.openNewEcoreEMFEditor("default.ecore", getBot());
    assertNotNull(editor);

    SWTBotTree tree = editor.getSelectionPageTree();

    selectFolder(tree.getAllItems(), "", true);

    editor.clickContextMenu(tree.widget, "EClass");
    editor.clickContextMenu(tree.widget, "EData Type");
    editor.clickContextMenu(tree.widget, "EAnnotation");
    editor.clickContextMenu(tree.widget, "EPackage");
    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/default.ecore");
      EPackage ePackage = (EPackage)resource.getContents().get(0);

      assertEquals(2, ePackage.getEClassifiers().size());
      assertEquals(1, ePackage.getESubpackages().size());
    }

    editor.close();
    {
      assertEquals(true, resourceExists("/default.ecore"));
    }
  }
}
