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

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnAcoreTestUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.NeedsCleanRepo;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
@NeedsCleanRepo
@RunWith(SWTBotJunit4ClassRunner.class)
public class SimpleDiagramTest extends AbstractDawnGEFTest
{
  @Test
  public void testCreateNewDawnDiagramAndAddElements() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 150, 250, "C", getBot(), editor);

    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");
      CDOResource semanticResource = view.getResource("/default.acore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, semanticRoot.getClasses().size());

      Character name = 'A';

      for (AClass aClass : semanticRoot.getClasses())
      {
        assertEquals(name.toString(), aClass.getName());
        name++;
      }
    }
  }

  @Test
  public void testCreateNewDawnDiagramAndAddElementsWithEdges() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 150, 250, "C", getBot(), editor);

    createEdge(DawnAcoreTestUtil.CONNECTION_IHERITS, 100, 100, 250, 100, editor);
    createEdge(DawnAcoreTestUtil.CONNECTION_IHERITS, 100, 100, 150, 250, editor);

    List<SWTBotGefEditPart> connectionEditParts = getAllConnections(editor);

    assertEquals(2, connectionEditParts.size());

    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");

      CDOResource semanticResource = view.getResource("/default.acore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, semanticRoot.getClasses().size());

      Character name = 'A';

      for (AClass aClass : semanticRoot.getClasses())
      {
        assertEquals(name.toString(), aClass.getName());
        name++;
      }
      view.close();
    }

    // for (SWTBotGefEditPart ep : connectionEditParts)
    // {
    // ConnectionEditPart connectionEditPart = (ConnectionEditPart)ep.part();
    // Connection connection = (Connection)connectionEditPart.getFigure();
    //
    // Point midpoint = connection.getPoints().getMidpoint().getCopy();
    //
    // editor.click(midpoint.x, midpoint.y);
    // editor.drag(midpoint.x, midpoint.y, 0, 20);
    // }
    // editor.save();
    // editor.close();
  }

  @Test
  public void testAClassWithAttributes() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);

    editor.activateTool(DawnAcoreTestUtil.A_ATTRIBUTE);
    editor.click(100, 100);

    typeTextToFocusedWidget("public foo:int", getBot(), true);
    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");

      CDOResource semanticResource = view.getResource("/default.acore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      AClass aClass = semanticRoot.getClasses().get(0);

      assertEquals(1, aClass.getAttributes().size());

      AAttribute aAttribute = aClass.getAttributes().get(0);

      assertEquals("public", aAttribute.getAccessright().toString());
      assertEquals("int", aAttribute.getDataType().toString());
      assertEquals("foo", aAttribute.getName());
    }
  }

  @Test
  public void testAClassWithOperations() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);

    editor.activateTool(DawnAcoreTestUtil.A_OPERATION);
    editor.click(100, 100);

    typeTextToFocusedWidget("public foo():int", getBot(), true);
    editor.save();
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");

      CDOResource semanticResource = view.getResource("/default.acore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      AClass aClass = semanticRoot.getClasses().get(0);

      assertEquals(1, aClass.getOperations().size());

      AOperation aOperation = aClass.getOperations().get(0);
      assertEquals("foo", aOperation.getName());
      assertEquals("public", aOperation.getAccessright().toString());
      assertEquals("int", aOperation.getDataType().toString());
    }
  }

  @Test
  public void testAInterfaceWithOperations() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    createNodeWithLabel(DawnAcoreTestUtil.A_INTERFACE, 100, 100, "A", getBot(), editor);

    editor.activateTool(DawnAcoreTestUtil.A_OPERATION);
    editor.click(100, 100);

    typeTextToFocusedWidget("public foo():int", getBot(), true);

    editor.save();
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");

      CDOResource semanticResource = view.getResource("/default.acore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(1, diagram.getChildren().size());

      AInterface aInterface = semanticRoot.getInterfaces().get(0);

      AOperation aOperation = aInterface.getOperations().get(0);
      assertEquals("foo", aOperation.getName());
      assertEquals("public", aOperation.getAccessright().toString());
      assertEquals("int", aOperation.getDataType().toString());
    }
  }

  @Test
  public void testConnections() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 300, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 100, 300, "C", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_CLASS, 300, 300, "D", getBot(), editor);
    createNodeWithLabel(DawnAcoreTestUtil.A_INTERFACE, 200, 200, "Interface", getBot(), editor);

    createEdge(DawnAcoreTestUtil.CONNECTION_IHERITS, 100, 100, 300, 100, editor);
    createEdge(DawnAcoreTestUtil.CONNECTION_ASSOCIATION, 310, 110, 310, 310, editor);
    createEdge(DawnAcoreTestUtil.CONNECTION_COMPOSITION, 300, 300, 100, 300, editor);
    createEdge(DawnAcoreTestUtil.CONNECTION_AGGREGATION, 100, 300, 100, 100, editor);
    createEdge(DawnAcoreTestUtil.CONNECTION_IMPLEMENTS, 100, 100, 200, 200, editor);

    List<SWTBotGefEditPart> connectionEditParts = getAllConnections(editor);

    assertEquals(5, connectionEditParts.size());

    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.acore_diagram");

      CDOResource semanticResource = view.getResource("/default.acore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      ACoreRoot semanticRoot = (ACoreRoot)semanticResource.getContents().get(0);

      assertEquals(5, diagram.getChildren().size());
      assertEquals(4, semanticRoot.getClasses().size());
      assertEquals(1, semanticRoot.getInterfaces().size());

      AClass A = semanticRoot.getClasses().get(0);
      AClass B = semanticRoot.getClasses().get(1);
      AClass C = semanticRoot.getClasses().get(2);
      AClass D = semanticRoot.getClasses().get(3);

      // A
      EList<AInterface> implementedInterfaces = A.getImplementedInterfaces();
      assertEquals(1, implementedInterfaces.size());
      assertEquals(implementedInterfaces.get(0), semanticRoot.getInterfaces().get(0));
      assertEquals(1, A.getSubClasses().size());
      assertEquals(B, A.getSubClasses().get(0));

      // B
      assertEquals(1, B.getAssociations().size());
      assertEquals(D, B.getAssociations().get(0));

      // C
      assertEquals(1, C.getAggregations().size());
      assertEquals(A, C.getAggregations().get(0));

      // D
      assertEquals(1, D.getCompositions().size());
      assertEquals(C, D.getCompositions().get(0));
      view.close();
    }

    editor.close();
  }

  @Test
  public void testExceptionOnClose() throws Exception
  {
    SWTBotGefEditor editor = DawnAcoreTestUtil.openNewAcoreGMFEditor("default.acore_diagram", getBot());
    assertNotNull(editor);
    editor.close();
  }

  private List<SWTBotGefEditPart> getAllConnections(final SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> aClassEditParts = editor.editParts(new AbstractMatcher<AClassEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        if (item instanceof AClassEditPart)
        {
          return true;
        }
        return false;
      }

      public void describeTo(Description description)
      {
      }
    });

    List<SWTBotGefEditPart> ret = new ArrayList<SWTBotGefEditPart>();
    for (SWTBotGefEditPart editPart : aClassEditParts)
    {
      ret.addAll(editPart.sourceConnections());
    }

    return ret;
  }
}
