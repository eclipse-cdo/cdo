/**
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
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAggregationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAssociationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassCompositionsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassSubClassesEditPart;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.NeedsCleanRepo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
@NeedsCleanRepo
@RunWith(SWTBotJunit4ClassRunner.class)
public class MultipleResourcesTest extends AbstractDawnGEFTest
{
  @Test
  public void testRemotelyRenameAClass() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);

    editor.save();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource2 = transaction.getResource("/default.acore_diagram");

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 250, 100, "B", getBot(), editor);

    editor.save();

    Diagram diagram = (Diagram)resource2.getContents().get(0);

    assertEquals(2, diagram.getChildren().size());

    View nodeB = (View)diagram.getChildren().get(1);

    AClass classB = (AClass)nodeB.getElement();

    assertEquals("B", classB.getName());

    classB.setName("C");
    transaction.commit();

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(1);
    AClass editorClassB = (AClass)((View)classBEditpart.part().getModel()).getElement();
    assertEquals("C", editorClassB.getName());
  }

  @Test
  public void testRemotelyMoveNode() throws Exception
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

    Bounds bounds = (Bounds)nodeA.getLayoutConstraint();
    bounds.setHeight(40);
    bounds.setWidth(30);
    bounds.setX(200);
    bounds.setY(250);

    transaction.commit();

    sleep(500);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);
    Node editorNodeA = (Node)classBEditpart.part().getModel();
    Bounds editorBounds = (Bounds)editorNodeA.getLayoutConstraint();

    assertEquals(bounds.getX(), editorBounds.getX());
    assertEquals(bounds.getY(), editorBounds.getY());
    assertEquals(bounds.getWidth(), editorBounds.getWidth());
    assertEquals(bounds.getHeight(), editorBounds.getHeight());
  }

  @Test
  public void testCreateNodeRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);
    editor.save();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource2 = transaction.getResource("/default.acore_diagram");

    Diagram diagram = (Diagram)resource2.getContents().get(0);

    ACoreRoot aCoreRoot = (ACoreRoot)diagram.getElement();

    AClass newAClass = AcoreFactory.eINSTANCE.createAClass();
    newAClass.setName("A-Team");

    aCoreRoot.getClasses().add(newAClass);

    Node newNode = DawnAcoreTestUtil.createNewAClassRemote(diagram, newAClass);

    newNode.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
    Bounds bounds = (Bounds)newNode.getLayoutConstraint();
    bounds.setHeight(40);
    bounds.setWidth(30);
    bounds.setX(200);
    bounds.setY(250);

    newNode.setElement(newAClass);

    assertEquals(1, diagram.getChildren().size());
    assertEquals(1, aCoreRoot.getClasses().size());

    transaction.commit();

    sleep(1000);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    assertEquals(1, aClassEditParts.size());

    SWTBotGefEditPart classBEditpart = aClassEditParts.get(0);
    Node editorNewNode = (Node)classBEditpart.part().getModel();
    AClass editorNewAclass = (AClass)editorNewNode.getElement();

    assertEquals("A-Team", editorNewAclass.getName());
    Bounds editorBounds = (Bounds)editorNewNode.getLayoutConstraint();

    assertEquals(bounds.getX(), editorBounds.getX());
    assertEquals(bounds.getY(), editorBounds.getY());
    assertEquals(bounds.getWidth(), editorBounds.getWidth());
    assertEquals(bounds.getHeight(), editorBounds.getHeight());

  }

  @Test
  public void testAddNodeRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);

    editor.save();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource2 = transaction.getResource("/default.acore_diagram");

    Diagram diagram = (Diagram)resource2.getContents().get(0);

    Node newNode = EcoreUtil.copy((Node)diagram.getChildren().get(0));// diagram.createChild(NotationPackage.eINSTANCE.getNode());

    newNode.setLayoutConstraint(NotationFactory.eINSTANCE.createBounds());
    Bounds bounds = (Bounds)newNode.getLayoutConstraint();
    bounds.setHeight(40);
    bounds.setWidth(30);
    bounds.setX(200);
    bounds.setY(250);

    AClass newAClass = AcoreFactory.eINSTANCE.createAClass();
    newAClass.setName("A-Team");
    newNode.setElement(newAClass);

    ACoreRoot aCoreRoot = (ACoreRoot)diagram.getElement();
    aCoreRoot.getClasses().add(newAClass);

    diagram.insertChild(newNode);
    assertEquals(2, diagram.getChildren().size());
    assertEquals(2, aCoreRoot.getClasses().size());

    transaction.commit();

    sleep(1000);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    assertEquals(2, aClassEditParts.size());

    SWTBotGefEditPart classBEditpart = aClassEditParts.get(1);
    Node editorNewNode = (Node)classBEditpart.part().getModel();
    AClass editorNewAclass = (AClass)editorNewNode.getElement();

    assertEquals("A-Team", editorNewAclass.getName());
    Bounds editorBounds = (Bounds)editorNewNode.getLayoutConstraint();

    assertEquals(bounds.getX(), editorBounds.getX());
    assertEquals(bounds.getY(), editorBounds.getY());
    assertEquals(bounds.getWidth(), editorBounds.getWidth());
    assertEquals(bounds.getHeight(), editorBounds.getHeight());
  }

  @Test
  public void testModifyConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aClassEditParts.get(1).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_ASSOCIATION, nodeA, nodeB, editor);
    editor.save();

    {
      List<SWTBotGefEditPart> aaClassAssociationsEditParts = DawnAcoreTestUtil.getAClassAssociationsEditParts(editor);
      assertEquals(1, aaClassAssociationsEditParts.size());
      EditPart part = aaClassAssociationsEditParts.get(0).part();
      Edge editorEdge = (Edge)part.getModel();
      assertEquals(2, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);
      List<?> edges = diagram.getEdges();
      assertEquals(1, edges.size());

      Edge edge = (Edge)edges.get(0);

      RelativeBendpoint relativeBendpoint = new RelativeBendpoint(0, 100, -100, 0);

      RelativeBendpoints relativeBendpoints = (RelativeBendpoints)edge.getBendpoints();

      @SuppressWarnings("unchecked")
      List<RelativeBendpoint> points = relativeBendpoints.getPoints();
      List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>(points);
      newBendPoints.add(1, relativeBendpoint);
      relativeBendpoints.setPoints(newBendPoints);

      transaction.commit();
      sleep(1000);
    }

    List<SWTBotGefEditPart> aaClassAssociationsEditParts = DawnAcoreTestUtil.getAClassAssociationsEditParts(editor);
    assertEquals(1, aaClassAssociationsEditParts.size());
    EditPart part = aaClassAssociationsEditParts.get(0).part();
    Edge editorEdge = (Edge)part.getModel();
    assertEquals(3, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
  }

  @Test
  public void testCreateAssociationConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);

    editor.save();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      Node nodeB = (Node)diagram.getChildren().get(1);

      List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>();

      RelativeBendpoint relativeBendpoint = new RelativeBendpoint(0, 100, -100, 0);
      newBendPoints.add(relativeBendpoint);

      Edge edge = DawnAcoreTestUtil.createNewAssociationRemote(nodeA, nodeB, newBendPoints);

      assertNotNull(edge);
      transaction.commit();
      sleep(1000);
    }
    List<SWTBotGefEditPart> connectionEditParts = DawnSWTBotUtil.getAllConnections(editor);
    assertEquals(1, connectionEditParts.size());
    EditPart part = connectionEditParts.get(0).part();
    assertInstanceOf(AClassAssociationsEditPart.class, part);
    assertInstanceOf(Edge.class, part.getModel());
    Edge editorEdge = (Edge)part.getModel();
    assertEquals(3, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
  }

  @Test
  public void testCreateAggregationConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);
    editor.save();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      Node nodeB = (Node)diagram.getChildren().get(1);

      List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>();

      RelativeBendpoint relativeBendpoint = new RelativeBendpoint(0, 100, -100, 0);
      newBendPoints.add(relativeBendpoint);

      Edge edge = DawnAcoreTestUtil.createNewAggregationRemote(nodeA, nodeB, newBendPoints);

      assertNotNull(edge);
      transaction.commit();
      sleep(1000);
    }
    List<SWTBotGefEditPart> connectionEditParts = DawnSWTBotUtil.getAllConnections(editor);
    assertEquals(1, connectionEditParts.size());
    EditPart part = connectionEditParts.get(0).part();
    assertInstanceOf(AClassAggregationsEditPart.class, part);
    assertInstanceOf(Edge.class, part.getModel());
    Edge editorEdge = (Edge)part.getModel();
    assertEquals(3, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
  }

  @Test
  public void testCreateCompositionConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);

    editor.save();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      Node nodeB = (Node)diagram.getChildren().get(1);

      List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>();

      RelativeBendpoint relativeBendpoint = new RelativeBendpoint(0, 100, -100, 0);
      newBendPoints.add(relativeBendpoint);

      Edge edge = DawnAcoreTestUtil.createNewCompositionRemote(nodeA, nodeB, newBendPoints);

      assertNotNull(edge);
      transaction.commit();
      sleep(1000);
    }
    List<SWTBotGefEditPart> connectionEditParts = DawnSWTBotUtil.getAllConnections(editor);
    assertEquals(1, connectionEditParts.size());
    EditPart part = connectionEditParts.get(0).part();
    assertInstanceOf(AClassCompositionsEditPart.class, part);
    assertInstanceOf(Edge.class, part.getModel());
    Edge editorEdge = (Edge)part.getModel();
    assertEquals(3, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
  }

  @Test
  public void testCreateInheritanceConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);

    editor.save();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      Node nodeB = (Node)diagram.getChildren().get(1);

      List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>();

      RelativeBendpoint relativeBendpoint = new RelativeBendpoint(0, 100, -100, 0);
      newBendPoints.add(relativeBendpoint);

      Edge edge = DawnAcoreTestUtil.createNewInheritanceRelationRemote(nodeA, nodeB, newBendPoints);

      assertNotNull(edge);
      transaction.commit();
      sleep(1000);
    }
    List<SWTBotGefEditPart> connectionEditParts = DawnSWTBotUtil.getAllConnections(editor);
    assertEquals(1, connectionEditParts.size());
    EditPart part = connectionEditParts.get(0).part();
    assertInstanceOf(AClassSubClassesEditPart.class, part);
    assertInstanceOf(Edge.class, part.getModel());
    Edge editorEdge = (Edge)part.getModel();
    assertEquals(3, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
  }

  @Test
  public void testCreateImplementsConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());

    createNodeWithLabel(DawnAcoreTestUtil.A_INTERFACE, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", getBot(), editor);

    editor.save();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      Node nodeB = (Node)diagram.getChildren().get(1);

      List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>();

      RelativeBendpoint relativeBendpoint = new RelativeBendpoint(0, 100, -100, 0);
      newBendPoints.add(relativeBendpoint);

      Edge edge = DawnAcoreTestUtil.createNewImplementsRelationRemote(nodeA, nodeB, newBendPoints);

      assertNotNull(edge);
      transaction.commit();
      sleep(1000);
    }
    List<SWTBotGefEditPart> connectionEditParts = DawnSWTBotUtil.getAllConnections(editor);
    assertEquals(1, connectionEditParts.size());
    EditPart part = connectionEditParts.get(0).part();
    assertInstanceOf(AClassImplementedInterfacesEditPart.class, part);
    assertInstanceOf(Edge.class, part.getModel());
    Edge editorEdge = (Edge)part.getModel();
    assertEquals(3, ((RelativeBendpoints)editorEdge.getBendpoints()).getPoints().size());
  }
}
