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
package org.eclipse.emf.cdo.dawn.tests.ui.gmf;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class ConflictTest extends AbstractDawnGEFTest
{
  @Test
  public void testAClassConflictMove() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    editor.save();

    editor.drag(100, 100, 200, 200);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      Node nodeA = (Node)diagram.getChildren().get(0);

      DawnSWTBotUtil.moveNodeRemotely(nodeA, 200, 300);

      transaction.commit();
    }
    sleep(500);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);

    assertEquals(true, DawnAcoreTestUtil.showsConflict(classBEditpart.part()));
  }

  @Test
  public void testAClassConflictbyRemoteNameChange() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    editor.save();

    SWTBotGefEditPart swtBotGefEditPart = DawnAcoreTestUtil.getAClassEditParts(editor).get(0);
    final Node node = (Node)swtBotGefEditPart.part().getModel();

    EditingDomain editingDomain = ((IEditingDomainProvider)node.eResource().getResourceSet()).getEditingDomain();

    editingDomain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)editingDomain)
    {
      @Override
      protected void doExecute()
      {
        ((AClass)node.getElement()).setName("myName");
      }
    });

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      Node nodeA = (Node)diagram.getChildren().get(0);
      AClass aClass = (AClass)nodeA.getElement();
      aClass.setName("newName");

      transaction.commit();
    }
    sleep(500);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);

    assertEquals(true, DawnAcoreTestUtil.showsConflict(classBEditpart.part()));
  }

  @Test
  public void testAInterfaceConflictMove() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_INTERFACE, 100, 100, "A", getBot(), editor);
    editor.save();

    editor.drag(100, 100, 200, 200);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      Node nodeA = (Node)diagram.getChildren().get(0);

      DawnSWTBotUtil.moveNodeRemotely(nodeA, 200, 300);

      transaction.commit();
    }
    sleep(500);
    List<SWTBotGefEditPart> interfaceEditParts = DawnAcoreTestUtil.getAInterfaceEditParts(editor);
    SWTBotGefEditPart interfaceEditpart = interfaceEditParts.get(0);

    assertEquals(true, DawnAcoreTestUtil.showsConflict(interfaceEditpart.part()));
  }

  @Test
  public void testAAssociationConflict() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aClassEditParts.get(1).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_ASSOCIATION, nodeA, nodeB, editor);
    editor.save();

    List<SWTBotGefEditPart> aClassAssociationsEditParts = DawnAcoreTestUtil.getAClassAssociationsEditParts(editor);

    DawnSWTBotUtil.addBendPoint((Edge)aClassAssociationsEditParts.get(0).part().getModel(), 0, 100, -100, 0);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      try
      {
        CDOResource resource2 = transaction.getResource("/default.acore_diagram");
        Diagram diagram = (Diagram)resource2.getContents().get(0);

        Edge edge = (Edge)diagram.getEdges().get(0);
        DawnSWTBotUtil.addBendPoint(edge, 0, 100, -100, 0);

        transaction.commit();
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
      finally
      {
        transaction.close();
      }
    }
  }
}
