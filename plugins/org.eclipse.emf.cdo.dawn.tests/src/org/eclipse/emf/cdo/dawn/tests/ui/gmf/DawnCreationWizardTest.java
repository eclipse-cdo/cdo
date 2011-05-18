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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.DawnAcoreCreationWizard;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.ui.composites.CDOResourceNodeChooserComposite.ResourceChooserValidator;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewDiagramResourceWizardPage;
import org.eclipse.emf.cdo.dawn.ui.wizards.DawnCreateNewResourceWizardPage;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Martin Fluegge
 */
public class DawnCreationWizardTest extends AbstractCDOTest
{
  public void testCreationWizardSetWrongDiagramName() throws Exception
  {
    CDOSession session = openSession();
    CDOConnectionUtil.instance.openView(session);

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();

    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    dawnDiagramModelFilePage.setVisible(true);
    assertNotNull(dawnDiagramModelFilePage);
    assertInstanceOf(DawnCreateNewResourceWizardPage.class, dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());
    assertEquals("", dawnDiagramModelFilePage.getResourcePath());
    assertEquals("default", dawnDiagramModelFilePage.getResourceNamePrefix());
    assertEquals("dawn://repo1//default.acore_diagram", dawnDiagramModelFilePage.getURI().toString());

    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());

    dawnDiagramModelFilePage.setResourceNamePrefix("");
    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(false, dawnDiagramModelFilePage.isPageComplete());

    dawnDiagramModelFilePage.setResourceNamePrefix("myDiagram");
    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());
  }

  public void testCreationWizardSetWrongSemanticNameWarn_Default() throws Exception
  {
    CDOSession session = openSession();
    CDOConnectionUtil.instance.openView(session);

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();
    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    DawnCreateNewResourceWizardPage dawnDomainModelFilePage = (DawnCreateNewResourceWizardPage)dawnDiagramModelFilePage
        .getNextPage();

    dawnDomainModelFilePage.setVisible(true);

    callValidatePage(dawnDomainModelFilePage);
    assertEquals(true, dawnDomainModelFilePage.isPageComplete());

    dawnDomainModelFilePage.setResourceNamePrefix("");
    callValidatePage(dawnDomainModelFilePage);
    assertEquals("", getResourceText(dawnDomainModelFilePage).getText());
    assertEquals(true, dawnDomainModelFilePage.isPageComplete());

    dawnDomainModelFilePage.setResourceNamePrefix("something.acore");
    callValidatePage(dawnDomainModelFilePage);
    assertEquals(true, dawnDomainModelFilePage.isPageComplete());

    assertEquals("something.acore", dawnDomainModelFilePage.getDefaultName());
  }

  public void testCreationWizardSetWrongSemanticNameError() throws Exception
  {
    CDOSession session = openSession();
    CDOConnectionUtil.instance.openView(session);

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();
    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    DawnCreateNewResourceWizardPage dawnDomainModelFilePage = (DawnCreateNewResourceWizardPage)dawnDiagramModelFilePage
        .getNextPage();

    dawnDomainModelFilePage.setVisible(true);
    dawnDomainModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_ERROR);

    assertEquals(ResourceChooserValidator.VALIDATION_ERROR, dawnDiagramModelFilePage.getResourceValidationType());

    dawnDomainModelFilePage.setResourceNamePrefix("");
    callValidatePage(dawnDomainModelFilePage);

    assertEquals("", getResourceText(dawnDomainModelFilePage).getText());
    assertEquals(false, dawnDomainModelFilePage.isPageComplete());
  }

  public void testCreationWizardCreateAutomaticName() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      transaction.createResource("/default.acore_diagram");
      transaction.commit();
      transaction.close();
    }

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();
    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    dawnDiagramModelFilePage.setCreateAutomaticResourceName(true);
    dawnDiagramModelFilePage.setVisible(true);

    assertEquals("default2.acore_diagram", getResourceText(dawnDiagramModelFilePage).getText());
  }

  public void testCreationWizardSetExistingResourceError() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      transaction.createResource("/default5.acore_diagram");
      transaction.commit();
      transaction.close();
    }

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();
    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    dawnDiagramModelFilePage.setVisible(true);
    dawnDiagramModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_ERROR);
    assertEquals(ResourceChooserValidator.VALIDATION_ERROR, dawnDiagramModelFilePage.getResourceValidationType());

    dawnDiagramModelFilePage.setResourceNamePrefix("default5.acore_diagram");
    assertEquals("default5.acore_diagram", getResourceText(dawnDiagramModelFilePage).getText());
    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(false, dawnDiagramModelFilePage.isPageComplete());
    assertEquals("A resource with the same name already exists!", dawnDiagramModelFilePage.getErrorMessage());
  }

  public void testCreationWizardSetExistingResourceWarn() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      transaction.createResource("/default5.acore_diagram");
      transaction.commit();
      transaction.close();
    }

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();
    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    dawnDiagramModelFilePage.setVisible(true);
    dawnDiagramModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_WARN);
    assertEquals(ResourceChooserValidator.VALIDATION_WARN, dawnDiagramModelFilePage.getResourceValidationType());

    dawnDiagramModelFilePage.setResourceNamePrefix("default5.acore_diagram");
    assertEquals("default5.acore_diagram", getResourceText(dawnDiagramModelFilePage).getText());
    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());
    assertEquals("A resource with the same name already exists!", dawnDiagramModelFilePage.getMessage());
  }

  public void testCreationWizardSetExistingResourceNone() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      transaction.createResource("/default5.acore_diagram");
      transaction.commit();
      transaction.close();
    }

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();
    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    dawnDiagramModelFilePage.setVisible(true);
    dawnDiagramModelFilePage.setResourceValidationType(ResourceChooserValidator.VALIDATION_NONE);
    assertEquals(ResourceChooserValidator.VALIDATION_NONE, dawnDiagramModelFilePage.getResourceValidationType());

    dawnDiagramModelFilePage.setResourceNamePrefix("default5.acore_diagram");
    assertEquals("default5.acore_diagram", getResourceText(dawnDiagramModelFilePage).getText());
    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());
    assertEquals(null, dawnDiagramModelFilePage.getErrorMessage());
  }

  public void testCreationWizardCreateResources() throws Exception
  {
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      session.openTransaction(resourceSet);

      final URI uri = URI.createURI("cdo:/folder/dummy");
      resourceSet.createResource(uri);
    }

    DawnAcoreCreationWizard creationWizard = new DawnAcoreCreationWizard();

    WizardDialog dialog = new WizardDialog(DawnEditorHelper.getActiveShell(), creationWizard);
    dialog.create();

    IWizardPage[] pages = creationWizard.getPages();

    DawnCreateNewDiagramResourceWizardPage dawnDiagramModelFilePage = (DawnCreateNewDiagramResourceWizardPage)pages[0];
    dawnDiagramModelFilePage.setVisible(true);
    assertNotNull(dawnDiagramModelFilePage);
    assertInstanceOf(DawnCreateNewResourceWizardPage.class, dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());
    assertEquals("", dawnDiagramModelFilePage.getResourcePath());
    assertEquals("default", dawnDiagramModelFilePage.getResourceNamePrefix());
    assertEquals("dawn://repo1//default.acore_diagram", dawnDiagramModelFilePage.getURI().toString());

    // TODO create a folder here first
    // set the resource path as a user would do
    dawnDiagramModelFilePage.setResourcePath("/folder");
    assertEquals("/folder/", dawnDiagramModelFilePage.getResourcePath());
    assertEquals("dawn://repo1//folder//default.acore_diagram", dawnDiagramModelFilePage.getURI().toString());

    callValidatePage(dawnDiagramModelFilePage);
    assertEquals(true, dawnDiagramModelFilePage.isPageComplete());

    DawnCreateNewResourceWizardPage dawnDomainModelFilePage = (DawnCreateNewResourceWizardPage)dawnDiagramModelFilePage
        .getNextPage();
    dawnDiagramModelFilePage.setVisible(false);
    dawnDomainModelFilePage.setVisible(true);

    callValidatePage(dawnDomainModelFilePage);
    assertEquals(true, dawnDomainModelFilePage.isPageComplete());
    assertNotNull(dawnDomainModelFilePage);
    assertInstanceOf(DawnCreateNewResourceWizardPage.class, dawnDomainModelFilePage);
    assertEquals("default.acore", dawnDomainModelFilePage.getDefaultName());
    assertEquals("cdo://repo1/folder/default.acore", dawnDomainModelFilePage.getURI().toString());

    boolean performFinish = creationWizard.performFinish();
    assertEquals(true, performFinish);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource semanticResource = transaction.getResource("/folder/default.acore");

      CDOResource diagramResource = transaction.getResource("/folder/default.acore_diagram");
      assertNotNull(diagramResource);
      Diagram diagramRoot = (Diagram)diagramResource.getContents().get(0);
      assertEquals(semanticResource.getContents().get(0), diagramRoot.getElement());
      transaction.close();
    }
    // cleanup
    DawnEditorHelper.getActiveEditor().getSite().getPage().closeAllEditors(false);
  }

  private void callValidatePage(DawnCreateNewResourceWizardPage page) throws Exception
  {
    Class<DawnCreateNewResourceWizardPage> clazz = DawnCreateNewResourceWizardPage.class;
    java.lang.Class<Object>[] parameterType = null;
    Method method = clazz.getDeclaredMethod("validatePage", parameterType);
    method.setAccessible(true);
    Object[] args = null;
    method.invoke(page, args);
  }

  private Text getResourceText(DawnCreateNewResourceWizardPage dawnDomainModelFilePage) throws Exception
  {
    Class<DawnCreateNewResourceWizardPage> clazz = DawnCreateNewResourceWizardPage.class;
    Field field = clazz.getDeclaredField("resourceText");
    field.setAccessible(true);
    return (Text)field.get(dawnDomainModelFilePage);
  }
}
