/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class EMFEditorRollbackTest extends AbstractDawnEMFTest
{
  @Test
  public void testGMFAClassConflictMove() throws Exception
  {
    DawnSWTBotEMFEditor editor = DawnAcoreTestUtil.openNewAcoreEMFEditor("default.acore", getBot());
    assertNotNull(editor);
    SWTBotTree tree = editor.getSelectionPageTree();

    selectFolder(tree.getAllItems(), "ACore Root", false);

    editor.clickContextMenu(tree.widget, "AClass");
    editor.save();
    // selectFolder(tree.getAllItems(), "AClass", false);

    IDawnEditor dawnEditor = (IDawnEditor)editor.getReference().getEditor(false);

    CDOResource resource = dawnEditor.getDawnEditorSupport().getView().getResource("/default.acore");
    AClass aClass = ((ACoreRoot)resource.getContents().get(0)).getClasses().get(0);
    aClass.setName("BClass");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore");
      AClass aClass2 = ((ACoreRoot)resource2.getContents().get(0)).getClasses().get(0);
      aClass2.setName("CClass");
      transaction.commit();
    }
    sleep(500);
    assertEquals(true, aClass.cdoConflict());
    editor.clickContextMenu(tree.widget, "Solve Conflict");

    getBot().button("yes").click();
    assertEquals(false, aClass.cdoConflict());
    assertEquals("CClass", aClass.getName());
    editor.close();

  }
}
