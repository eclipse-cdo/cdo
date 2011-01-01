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

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Martin Fluegge
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class MultipleResourcesDeletionTest extends AbstractDawnGEFTest
{
  private static SWTGefBot bot;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    bot = new SWTGefBot();
    DawnSWTBotUtil.initTest(bot);
  }

  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();
    bot.viewByTitle("CDO Sessions").close();
  }

  @Override
  @After
  public void tearDown() throws Exception
  {
    // closeAllEditors();
    super.tearDown();
  }

  // @Test
  public void testDeleteAClassRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    editor.save();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      EObject aClass = nodeA.getElement();

      diagram.removeChild(nodeA);
      ((ACoreRoot)diagram.getElement()).getClasses().remove(aClass);

      transaction.commit();
      DawnAcoreTestUtil.sleep(1000);
    }

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    assertEquals(0, aClassEditParts.size());
    Diagram diagram = (Diagram)editor.mainEditPart().part().getModel();
    ACoreRoot aCoreRoot = (ACoreRoot)diagram.getElement();

    assertEquals(0, aCoreRoot.getClasses().size());
    editor.close();
  }

  // @Test
  public void testDeleteAInterfaceRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_INTERFACE, 100, 100, "A", bot, editor);
    editor.save();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Node nodeA = (Node)diagram.getChildren().get(0);
      EObject element = nodeA.getElement();

      diagram.removeChild(nodeA);
      ((ACoreRoot)diagram.getElement()).getInterfaces().remove(element);

      transaction.commit();
      sleep(1000);
    }

    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    assertEquals(0, aClassEditParts.size());
    Diagram diagram = (Diagram)editor.mainEditPart().part().getModel();
    ACoreRoot aCoreRoot = (ACoreRoot)diagram.getElement();

    assertEquals(0, aCoreRoot.getInterfaces().size());
    editor.close();
  }

  @Test
  public void testDeleteAssociationConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", bot, editor);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aClassEditParts.get(1).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_ASSOCIATION, nodeA, nodeB, editor);
    editor.save();

    deleteEdge();

    List<Edge> connectionEditParts = getAllConnections(editor.mainEditPart().part());
    assertEquals(0, connectionEditParts.size());
    sleep(1000);
  }

  @SuppressWarnings("unchecked")
  private List<Edge> getAllConnections(EditPart part)
  {
    Diagram diagram = (Diagram)part.getModel();
    return diagram.getEdges();
  }

  @Test
  public void testDeleteCompositionConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", bot, editor);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aClassEditParts.get(1).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_COMPOSITION, nodeA, nodeB, editor);
    editor.save();

    deleteEdge();

    List<Edge> connectionEditParts = getAllConnections(editor.mainEditPart().part());
    assertEquals(0, connectionEditParts.size());
  }

  @Test
  public void testDeleteInheritanceConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", bot, editor);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aClassEditParts.get(1).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_IHERITS, nodeA, nodeB, editor);
    editor.save();

    deleteEdge();

    List<Edge> connectionEditParts = getAllConnections(editor.mainEditPart().part());
    assertEquals(0, connectionEditParts.size());
  }

  @Test
  public void testDeleteImplementsConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_INTERFACE, 200, 200, "B", bot, editor);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);
    List<SWTBotGefEditPart> aInterfaceEditParts = DawnAcoreTestUtil.getAInterfaceEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aInterfaceEditParts.get(0).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_IMPLEMENTS, nodeA, nodeB, editor);
    editor.save();

    deleteEdge();

    List<Edge> connectionEditParts = getAllConnections(editor.mainEditPart().part());
    assertEquals(0, connectionEditParts.size());
  }

  @Test
  public void testDeleteAggregationConnectionRemotely() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", bot);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", bot, editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 200, 200, "B", bot, editor);
    List<SWTBotGefEditPart> aClassEditParts = DawnAcoreTestUtil.getAClassEditParts(editor);

    Node nodeA = (Node)aClassEditParts.get(0).part().getModel();
    Node nodeB = (Node)aClassEditParts.get(1).part().getModel();

    createEdge(DawnAcoreTestUtil.CONNECTION_AGGREGATION, nodeA, nodeB, editor);
    editor.save();

    deleteEdge();

    List<Edge> connectionEditParts = getAllConnections(editor.mainEditPart().part());
    assertEquals(0, connectionEditParts.size());
  }

  private void deleteEdge() throws CommitException
  {
    {
      CDOSession session = openSession("repo1");
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource2 = transaction.getResource("/default.acore_diagram");

      Diagram diagram = (Diagram)resource2.getContents().get(0);

      Edge edge = (Edge)diagram.getEdges().get(0);
      edge.setSource(null);
      edge.setTarget(null);
      diagram.removeEdge(edge);
      transaction.commit();

      sleep(1000);
    }
  }
  /**
   * left for future reference
   */
  // private class EdgeDeletionThread extends Thread
  // {
  // private final CountDownLatch cdl;
  //
  // public EdgeDeletionThread(CountDownLatch cdl)
  // {
  // this.cdl = cdl;
  // setDaemon(true);
  // }
  //
  // @Override
  // public void run()
  // {
  // CDOSession session = openSession();
  // CDOTransaction transaction = session.openTransaction();
  // CDOResource resource2 = transaction.getResource("/default.acore_diagram");
  //
  // Diagram diagram = (Diagram)resource2.getContents().get(0);
  //
  // Edge edge = (Edge)diagram.getEdges().get(0);
  // diagram.removeEdge(edge);
  // try
  // {
  // transaction.commit();
  // }
  // catch (CommitException ex)
  // {
  // throw new RuntimeException(ex);
  // }
  // finally
  // {
  // transaction.close();
  // cdl.countDown();
  // }
  // }
  // }
}
