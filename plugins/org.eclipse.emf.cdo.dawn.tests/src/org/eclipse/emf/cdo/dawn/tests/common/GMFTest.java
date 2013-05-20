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
package org.eclipse.emf.cdo.dawn.tests.common;

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
public class GMFTest extends AbstractCDOTest
{
  public void testDiagram() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/test1");
      Diagram diagram = NotationFactory.eINSTANCE.createDiagram();

      diagram.setName("MyDiagram");
      diagram.setVisible(true);
      diagram.setMutable(false);

      resource.getContents().add(diagram);

      transaction.commit();
    }

    session.close();
    session = openSession();

    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.getResource("/test1");

      Diagram diagram = (Diagram)resource.getContents().get(0);

      assertEquals("MyDiagram", diagram.getName());
      assertEquals(true, diagram.isVisible());
      assertEquals(false, diagram.isMutable());
    }
  }

  public void testSimpleNode() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/test1");
      Node node = createExampleNode();
      MangoValue mangoValue = getMangoFactory().createMangoValue();
      node.setElement(mangoValue);

      resource.getContents().add(node);
      resource.getContents().add(mangoValue);

      transaction.commit();
    }

    session.close();
    session = openSession();

    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.getResource("/test1");

      Node node = (Node)resource.getContents().get(0);

      assertEquals(true, node.isVisible());
      assertEquals(false, node.isMutable());
      assertInstanceOf(Bounds.class, node.getLayoutConstraint());
      Bounds bounds = (Bounds)node.getLayoutConstraint();
      assertEquals(1503, bounds.getHeight());
      assertEquals(1979, bounds.getWidth());
      assertEquals(777, bounds.getX());
      assertEquals(888, bounds.getY());

      assertInstanceOf(MangoValue.class, node.getElement());
    }
  }

  private Node createExampleNode()
  {
    Node node = NotationFactory.eINSTANCE.createNode();
    Bounds bounds = NotationFactory.eINSTANCE.createBounds();
    bounds.setHeight(1503);
    bounds.setWidth(1979);
    bounds.setX(777);
    bounds.setY(888);

    node.setVisible(true);
    node.setMutable(false);
    node.setLayoutConstraint(bounds);
    return node;
  }

  public void testSimpleEdge() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/test1");
      Edge edge = createExampleEdge();

      resource.getContents().add(edge);
      transaction.commit();
    }
    session.close();
    session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.getResource("/test1");

      Edge edge = (Edge)resource.getContents().get(0);

      assertEquals(true, edge.isVisible());
      assertEquals(false, edge.isMutable());

      assertEquals(10, ((RelativeBendpoints)edge.getBendpoints()).getPoints().size());
      for (int i = 0; i < 10; i++)
      {
        assertInstanceOf(RelativeBendpoint.class, ((RelativeBendpoints)edge.getBendpoints()).getPoints().get(i));
      }
    }
  }

  private Edge createExampleEdge()
  {
    Edge edge = NotationFactory.eINSTANCE.createEdge();

    edge.setVisible(true);
    edge.setMutable(false);
    RelativeBendpoints bendpoints = NotationFactory.eINSTANCE.createRelativeBendpoints();

    edge.setBendpoints(bendpoints);

    List<RelativeBendpoint> points = new ArrayList<RelativeBendpoint>();

    for (int i = 0; i < 10; i++)
    {
      RelativeBendpoint bendPoint = new RelativeBendpoint(1, 2, 3, 4);
      points.add(bendPoint);
    }

    bendpoints.setPoints(points);
    edge.setBendpoints(bendpoints);
    return edge;
  }

  public void testSimpleDiagramWithViews() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/test1");
      Diagram diagram = NotationFactory.eINSTANCE.createDiagram();

      diagram.setName("MyDiagram");
      int numberOfNodes = 10;
      for (int i = 0; i < numberOfNodes; i++)
      {
        // Node node = createExampleNode();
        // nodes.add(node);
        diagram.createChild(NotationPackage.eINSTANCE.getNode());
      }

      for (int i = 0; i < 5; i++)
      {
        Node nodeA = (Node)diagram.getChildren().get(i);
        Node nodeB = (Node)diagram.getChildren().get(numberOfNodes - 1 - i);

        Edge edge = diagram.createEdge(NotationPackage.eINSTANCE.getEdge());

        edge.setSource(nodeA);
        edge.setTarget(nodeB);
        System.out.println("break");
      }

      resource.getContents().add(diagram);
      transaction.commit();
    }

    session.close();
    session = openSession();

    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.getResource("/test1");

      Diagram diagram = (Diagram)resource.getContents().get(0);

      assertEquals(true, diagram.isVisible());
      assertEquals(false, diagram.isMutable());

      assertEquals(10, diagram.getChildren().size());
      assertEquals(5, diagram.getEdges().size());
    }
  }

  public void testDiagramFromXMIResource() throws Exception
  {
    CDOSession session = openSession();
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      AcorePackage.eINSTANCE.eClass();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
          .put("acore_diagram", new XMIResourceFactoryImpl());

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("acore", new XMIResourceFactoryImpl());

      System.out.println(GMFTest.class.getResource("."));
      System.out.println(GMFTest.class.getResource("/"));

      URL resourceURI = GMFTest.class.getResource("");
      String resourcePath = resourceURI.toString().substring(0, resourceURI.toString().lastIndexOf("/bin"));

      System.out.println(resourcePath);

      // Resource emfResource = resourceSet.getResource(URI.createURI(resourcePath + "/testdata/simple.acore"), true);
      Resource gmfResource = resourceSet.getResource(URI.createURI(resourcePath + "/testdata/simple.acore_diagram"),
          true);

      Diagram diagram = (Diagram)gmfResource.getContents().get(0);
      ACoreRoot classDiagram = (ACoreRoot)diagram.getElement();

      for (Object o : diagram.getPersistedChildren())
      {
        View view = (View)o;
        System.out.println(view.getElement());
      }

      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(classDiagram);
      resource.getContents().add(diagram);

      transaction.commit();
    }
    session.close();
    session = openSession();
    {
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.getResource("/test1");

      Diagram reloadedDiagram = (Diagram)resource.getContents().get(1);

      assertInstanceOf(ACoreRoot.class, reloadedDiagram.getElement());
      assertEquals(true, reloadedDiagram.isVisible());
      assertEquals(false, reloadedDiagram.isMutable());

      assertEquals(3, reloadedDiagram.getChildren().size());
      assertEquals(2, reloadedDiagram.getEdges().size());

      for (Object o : reloadedDiagram.getEdges())
      {
        Edge edge = (Edge)o;
        RelativeBendpoints bendpoints = (RelativeBendpoints)edge.getBendpoints();
        assertNotNull(bendpoints);
        assertEquals(true, bendpoints.getPoints().size() > 0);
      }
    }
  }
}
