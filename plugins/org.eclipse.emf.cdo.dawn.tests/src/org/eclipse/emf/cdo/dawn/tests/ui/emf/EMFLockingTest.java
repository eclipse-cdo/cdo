/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.emf;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnEMFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotEMFEditor;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class EMFLockingTest extends AbstractDawnEMFTest
{
  @Test
  public void testAClassLockRemotely() throws Exception
  {
    DawnSWTBotEMFEditor editor = DawnAcoreTestUtil.openNewAcoreEMFEditor("default.acore", getBot());
    assertNotNull(editor);
    SWTBotTree tree = editor.getSelectionPageTree();

    selectFolder(tree.getAllItems(), "ACore Root", false);

    editor.clickContextMenu(tree.widget, "AClass");
    editor.save();

    IDawnEditor dawnEditor = (IDawnEditor)editor.getReference().getEditor(false);
    CDOResource resource = dawnEditor.getDawnEditorSupport().getView().getResource("/default.acore");
    AClass aClass = ((ACoreRoot)resource.getContents().get(0)).getClasses().get(0);
    aClass.setName("BClass");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore");
      AClass aClass2 = ((ACoreRoot)resource2.getContents().get(0)).getClasses().get(0);
      aClass2.cdoWriteLock().lock();
    }

    sleep(500);

    assertEquals(true, aClass.cdoWriteLock().isLockedByOthers());

    // SWTBotTreeItem treeItem = selectFolder(tree.getAllItems(), "BClass", false);
    // assertEquals(DawnColorConstants.COLOR_LOCKED_REMOTELY, treeItem.foregroundColor());

    editor.close();
  }

  @Test
  public void testAClassLockLocally() throws Exception
  {
    DawnSWTBotEMFEditor editor = DawnAcoreTestUtil.openNewAcoreEMFEditor("default.acore", getBot());
    assertNotNull(editor);
    SWTBotTree tree = editor.getSelectionPageTree();

    selectFolder(tree.getAllItems(), "ACore Root", false);

    editor.clickContextMenu(tree.widget, "AClass");
    editor.save();

    selectFolder(tree.getAllItems(), "AClass", false);

    editor.clickContextMenu(tree.widget, "Lock");
    sleep(500);

    IDawnEditor dawnEditor = (IDawnEditor)editor.getReference().getEditor(false);
    CDOResource resource = dawnEditor.getDawnEditorSupport().getView().getResource("/default.acore");
    AClass aClass = ((ACoreRoot)resource.getContents().get(0)).getClasses().get(0);
    assertEquals(true, aClass.cdoWriteLock().isLocked());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource2 = transaction.getResource("/default.acore");
    AClass aClass2 = ((ACoreRoot)resource2.getContents().get(0)).getClasses().get(0);
    assertEquals(true, aClass2.cdoWriteLock().isLockedByOthers());

    sleep(500);

    editor.clickContextMenu(tree.widget, "Unlock");
    sleep(500);

    assertEquals(false, aClass.cdoWriteLock().isLocked());
    assertEquals(false, aClass2.cdoWriteLock().isLockedByOthers());

    // SWTBotTreeItem treeItem = selectFolder(tree.getAllItems(), "AClass", false);
    // assertEquals(DawnColorConstants.COLOR_LOCKED_LOCALLY, treeItem.foregroundColor());

    editor.close();
  }
}
