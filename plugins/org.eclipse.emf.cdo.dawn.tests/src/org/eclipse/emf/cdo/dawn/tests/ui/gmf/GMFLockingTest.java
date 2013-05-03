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
package org.eclipse.emf.cdo.dawn.tests.ui.gmf;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.ui.DawnColorConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class GMFLockingTest extends AbstractDawnGEFTest
{
  @Test
  public void testAClassLockRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    editor.save();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      Node nodeA = (Node)diagram.getChildren().get(0);

      CDOUtil.getCDOObject(nodeA).cdoWriteLock().lock();
    }
    sleep(500);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);

    assertEquals(true, DawnAcoreTestUtil.showsLock(classBEditpart.part(), DawnColorConstants.COLOR_LOCKED_REMOTELY));
  }

  @Test
  public void testAClassLockLocally() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    editor.save();

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);
    EditPart part = classBEditpart.part();

    List<Object> toBeLocked = new ArrayList<Object>();
    toBeLocked.add(part);

    IEditorPart editorPart = editor.getReference().getEditor(false);
    ((IDawnEditor)editorPart).getDawnEditorSupport().lockObjects(toBeLocked);

    assertEquals(true, DawnAcoreTestUtil.showsLock(part, DawnColorConstants.COLOR_LOCKED_LOCALLY));
  }

  @Test
  public void testAClassUnlockRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    editor.save();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource2 = transaction.getResource("/default.acore_diagram");

    Diagram diagram = (Diagram)resource2.getContents().get(0);

    assertEquals(1, diagram.getChildren().size());

    Node nodeA = (Node)diagram.getChildren().get(0);

    CDOUtil.getCDOObject(nodeA).cdoWriteLock().lock();

    sleep(500);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);

    assertEquals(true, DawnAcoreTestUtil.showsLock(classBEditpart.part(), DawnColorConstants.COLOR_LOCKED_REMOTELY));

    CDOUtil.getCDOObject(nodeA).cdoWriteLock().unlock();

    assertEquals(true, DawnAcoreTestUtil.showsNoLock(classBEditpart.part()));
  }
}
