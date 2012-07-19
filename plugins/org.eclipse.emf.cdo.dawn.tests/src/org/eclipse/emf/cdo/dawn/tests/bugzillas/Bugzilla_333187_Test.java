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
package org.eclipse.emf.cdo.dawn.tests.bugzillas;

import org.eclipse.emf.cdo.dawn.tests.AbstractDawnGEFTest;
import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnEcoreTestUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
@RunWith(SWTBotJunit4ClassRunner.class)
public class Bugzilla_333187_Test extends AbstractDawnGEFTest
{
  private static final String DOMAIN_FILE_NAME = "Domain file name: ";

  @Test
  public void testCreateNewEcoreToolsDiagram() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_GMF);
    getBot().button("Next >").click();
    sleep(1000);
    getBot().button("Finish").click();
    SWTBotGefEditor editor = getBot().gefEditor("default");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default.ecore"));
      assertEquals(true, resourceExists("/default.ecorediag"));
    }
  }

  @Test
  public void testCreateNewEcoreDiagramWrongResourceName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_GMF);
    getBot().button("Next >").click();
    sleep(6000);
    shell = getBot().shell("New Ecore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(DOMAIN_FILE_NAME);
    fileNameLabel.setText("");

    fileNameLabel.setFocus();
    fileNameLabel.typeText("x", 500);
    assertEquals(false, getBot().button("Next >").isEnabled());
    getBot().button("Cancel").click();
  }

  @Test
  public void testCreateNewEcoreDiagramChangeResourceName() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_GMF);
    getBot().button("Next >").click();
    sleep(6000);
    shell = getBot().shell("New Ecore Diagram");
    shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel(DOMAIN_FILE_NAME);
    fileNameLabel.setText("");

    fileNameLabel.setFocus();
    fileNameLabel.typeText("default2.ecore", 50);

    assertEquals(true, getBot().button("Finish").isEnabled());

    getBot().button("Finish").click();
    SWTBotGefEditor editor = getBot().gefEditor("default2");
    assertNotNull(editor);
    editor.close();
    {
      assertEquals(true, resourceExists("/default2.ecore"));
      assertEquals(true, resourceExists("/default2.ecorediag"));
    }
  }

  @Test
  public void testCreateNewEcoreDiagramSetResourceInDialog() throws Exception
  {
    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_GMF);
    getBot().button("Next >").click();

    shell = getBot().shell("New Ecore Diagram");
    shell.activate();

    getBot().button("Browse...").click();

    // activate the selection window
    // shell = getBot().shells()[3];
    // shell.activate();

    SWTBotText fileNameLabel = getBot().textWithLabel("Resource name:");
    fileNameLabel.setText("test.ecore");
    getBot().button("OK").click();

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(DOMAIN_FILE_NAME);
    assertEquals("test.ecore", fileSemanticNameLabel.getText());

    getBot().button("Finish").click();

    SWTBotGefEditor editor = getBot().gefEditor("test");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/test.ecore"));
      assertEquals(true, resourceExists("/test.ecorediag"));
    }
  }

  @Test
  public void testCreateNewEcoreDiagramSetResourceInDialogAndSelectFolder() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
      transaction.commit();
    }

    getBot().menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = getBot().shell("New");
    shell.activate();
    getBot().tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_GMF);
    getBot().button("Next >").click();

    shell = getBot().shell("New Ecore Diagram");
    shell.activate();

    getBot().button("Browse...").click();

    SWTBotTree tree = getBot().tree(0);
    selectFolder(tree.getAllItems(), "folder", false);

    SWTBotText fileNameLabel = getBot().textWithLabel("Resource name:");
    fileNameLabel.setText("test.ecore");
    getBot().button("OK").click();

    SWTBotText resourcePathLabel = getBot().text(0);
    assertEquals("cdo://repo1/folder/", resourcePathLabel.getText());

    SWTBotText fileSemanticNameLabel = getBot().textWithLabel(DOMAIN_FILE_NAME);
    assertEquals("test.ecore", fileSemanticNameLabel.getText());

    getBot().button("Finish").click();
    SWTBotGefEditor editor = getBot().gefEditor("test");
    assertNotNull(editor);
    editor.close();

    {
      assertEquals(true, resourceExists("/folder/test.ecore"));
      assertEquals(true, resourceExists("/folder/test.ecorediag"));
    }
  }

  @Test
  public void testCreateNewEcoreToolsDiagramAndAddElements() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 150, 250, "C", getBot(), editor);

    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);
      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, epackage.getEClassifiers().size());

      Character name = 'A';

      for (EClassifier aClass : epackage.getEClassifiers())
      {
        assertEquals(name.toString(), aClass.getName());
        name++;
      }
    }
  }

  @Test
  public void testCreateNewDawnDiagramAndAddElementsWithEdges() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 150, 250, "C", getBot(), editor);

    createEdge(DawnEcoreTestUtil.E_REFERENCE, 100, 100, 250, 100, editor);
    createEdge(DawnEcoreTestUtil.E_REFERENCE, 100, 100, 150, 250, editor);

    List<SWTBotGefEditPart> connectionEditParts = DawnEcoreTestUtil.getAllTargetConnections(editor);

    assertEquals(2, connectionEditParts.size());

    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");

      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, epackage.getEClassifiers().size());

      Character name = 'A';

      for (EClassifier aClass : epackage.getEClassifiers())
      {
        assertEquals(name.toString(), aClass.getName());
        name++;
      }
      view.close();
    }
  }

  @Test
  public void testEClassChangeName() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);

    editor.save();
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource semanticResource = view.getResource("/default.ecore");

      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(1, epackage.getEClassifiers().size());

      EClassifier eClassifier = epackage.getEClassifiers().get(0);

      assertEquals("A", eClassifier.getName());

      session.close();
    }

    typeTextToFocusedWidget("B", getBot(), true);
    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource semanticResource = view.getResource("/default.ecore");

      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(1, epackage.getEClassifiers().size());

      EClassifier eClassifier = epackage.getEClassifiers().get(0);

      assertEquals("B", eClassifier.getName());

      session.close();
    }
  }

  @Test
  public void testEClassWithEAttributes() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);

    editor.save();

    editor.activateTool(DawnEcoreTestUtil.E_ATTRIBUTE);
    editor.click(100, 100);
    typeTextToFocusedWidget("name", getBot(), true);
    editor.save();
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      EClass eClass = (EClass)epackage.getEClassifiers().get(0);

      assertEquals("A", eClass.getName());
      EList<EAttribute> eAttributes = eClass.getEAttributes();
      assertEquals(1, eAttributes.size());
      assertEquals("name", eAttributes.get(0).getName());
    }
  }

  @Test
  public void testEClassWithEOperation() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);

    editor.save();

    editor.activateTool(DawnEcoreTestUtil.E_OPERATION);
    editor.click(100, 100);
    typeTextToFocusedWidget("operation", getBot(), true);
    editor.save();
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource semanticResource = view.getResource("/default.ecore");

      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      EClass eClass = (EClass)epackage.getEClassifiers().get(0);

      assertEquals("A", eClass.getName());
      EList<EOperation> eOperations = eClass.getEOperations();
      assertEquals(1, eOperations.size());
      assertEquals("operation", eOperations.get(0).getName());
    }
  }

  @Test
  public void testDiagramWithInheritance() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 150, 250, "C", getBot(), editor);

    createEdge(DawnEcoreTestUtil.INHERITANCE, 100, 100, 250, 100, editor);
    createEdge(DawnEcoreTestUtil.INHERITANCE, 100, 100, 150, 250, editor);

    List<SWTBotGefEditPart> connectionEditParts = DawnEcoreTestUtil.getAllTargetConnections(editor);

    assertEquals(2, connectionEditParts.size());
    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");

      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, epackage.getEClassifiers().size());

      EClass eClass1 = (EClass)epackage.getEClassifiers().get(0);
      EClass eClass2 = (EClass)epackage.getEClassifiers().get(1);
      EClass eClass3 = (EClass)epackage.getEClassifiers().get(2);

      assertEquals(eClass2, eClass1.getEAllSuperTypes().get(0));
      assertEquals(eClass3, eClass1.getEAllSuperTypes().get(1));

      view.close();
    }
  }

  @Test
  public void testEDataType() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_DATATYPE, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_DATATYPE, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_DATATYPE, 150, 250, "C", getBot(), editor);
    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, epackage.getEClassifiers().size());

      Character name = 'A';

      for (EClassifier eType : epackage.getEClassifiers())
      {
        assertEquals(EDataTypeImpl.class, eType.getClass());
        assertEquals(name.toString(), eType.getName());
        name++;
      }
    }
  }

  @Test
  public void testEAnnotation() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_ANNOTATION, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_ANNOTATION, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_ANNOTATION, 150, 250, "C", getBot(), editor);
    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      EList<EAnnotation> eAnnotations = epackage.getEAnnotations();
      assertEquals(3, eAnnotations.size());

      Character name = 'A';

      for (EAnnotation eAnnotation : eAnnotations)
      {
        assertEquals(name.toString(), eAnnotation.getSource());
        name++;
      }
    }
  }

  @Test
  public void testEENum() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_ENUM, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_ENUM, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_ENUM, 150, 250, "C", getBot(), editor);
    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(3, epackage.getEClassifiers().size());

      Character name = 'A';

      for (EClassifier eEnum : epackage.getEClassifiers())
      {
        assertEquals(EEnumImpl.class, eEnum.getClass());
        assertEquals(name.toString(), eEnum.getName());
        name++;
      }
    }
  }

  @Test
  public void testEPackage() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_PACKAGE, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_PACKAGE, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_PACKAGE, 150, 250, "C", getBot(), editor);
    editor.saveAndClose();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      EList<EObject> contents = semanticResource.getContents();
      assertEquals(1, contents.size());

      EPackage ePackage = (EPackage)contents.get(0);
      Character name = 'A';

      for (EPackage eSubPackage : ePackage.getESubpackages())
      {
        assertEquals(name.toString(), eSubPackage.getName());
        assertEquals(ePackage, eSubPackage.getESuperPackage());
        name++;
      }
    }
  }

  @Test
  public void testDiagramWithClassAndEAnnotations() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_CLASS, 100, 100, "A", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_ANNOTATION, 250, 100, "B", getBot(), editor);
    createNodeWithLabel(DawnEcoreTestUtil.E_ANNOTATION, 150, 250, "C", getBot(), editor);

    createEdge(DawnEcoreTestUtil.E_ANNOTATION_LINK, 250, 100, 100, 100, editor);
    createEdge(DawnEcoreTestUtil.E_ANNOTATION_LINK, 150, 250, 100, 100, editor);

    List<SWTBotGefEditPart> connectionEditParts = DawnEcoreTestUtil.getAllTargetConnections(editor);

    assertEquals(2, connectionEditParts.size());
    editor.save();

    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");

      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      Diagram diagram = (Diagram)diagramResource.getContents().get(0);
      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      assertEquals(3, diagram.getChildren().size());
      assertEquals(1, epackage.getEClassifiers().size());

      // EClass eClass1 = (EClass)epackage.getEClassifiers().get(0);
      // EList<EAnnotation> eAnnotations = eClass1.getEAnnotations();
      // assertEquals(2, eAnnotations.size());
      //
      // Character name = 'B';
      //
      // for (EAnnotation annotation : eAnnotations)
      // {
      // assertEquals(name.toString(), annotation.getSource());
      // assertEquals(eClass1, annotation.getEModelElement());
      // name++;
      // }

      view.close();
    }
  }

  @Test
  public void testEAnnotationWithDetailEntry() throws Exception
  {
    SWTBotGefEditor editor = DawnEcoreTestUtil.openNewEcoreToolsEditor("default.ecore", getBot());
    assertNotNull(editor);

    createNodeWithLabel(DawnEcoreTestUtil.E_ANNOTATION, 100, 100, "A", getBot(), editor);
    editor.activateTool(DawnEcoreTestUtil.DETAILS_ENTRY);
    editor.click(100, 100);
    typeTextToFocusedWidget("name", getBot(), true);
    editor.save();
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CDOResource diagramResource = view.getResource("/default.ecorediag");
      CDOResource semanticResource = view.getResource("/default.ecore");

      assertNotNull(diagramResource);
      assertNotNull(semanticResource);

      EPackage epackage = (EPackage)semanticResource.getContents().get(0);

      EAnnotation eAnnotation = epackage.getEAnnotations().get(0);

      assertEquals("A", eAnnotation.getSource());
      assertEquals(1, eAnnotation.getDetails().size());
      view.close();
    }
  }
}
