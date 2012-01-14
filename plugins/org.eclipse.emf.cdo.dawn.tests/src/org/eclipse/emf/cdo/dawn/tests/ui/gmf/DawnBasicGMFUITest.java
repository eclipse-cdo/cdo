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
package org.eclipse.emf.cdo.dawn.tests.ui.gmf;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.DawnAcoreDiagramEditor;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.DawnAcoreDiagramEditorUtil;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 */
@CleanRepositoriesBefore
public class DawnBasicGMFUITest extends AbstractCDOTest
{
  @Override
  public void doSetUp() throws Exception
  {
    super.doSetUp();
    getRepositoryConfig().getRepositoryProperties().put("overrideUUID", "");
  }

  public void testGetEditorIdForDawnEditor()
  {
    final CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test.acore_diagram"); //$NON-NLS-1$

    String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());
    assertEquals(DawnAcoreDiagramEditor.ID, editorID);
  }

  public void testEditorInput() throws PartInitException
  {
    final CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/sample/test.acore_diagram"); //$NON-NLS-1$

    String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());
    assertEquals(DawnAcoreDiagramEditor.ID, editorID);
    DawnEditorInput editorInput = new DawnEditorInput(resource.getURI());
    editorInput.setResource(resource);

    assertEquals(resource, editorInput.getResource());
    assertEquals(resource.getPath(), editorInput.getResourcePath());
    assertEquals(transaction, editorInput.getView());
    assertEquals(resource.getURI(), editorInput.getURI());
    assertEquals(false, editorInput.isViewOwned());
  }

  public void testOpenEditor() throws PartInitException
  {
    final CDOSession session = openSession();
    DawnAcoreDiagramEditorUtil.createDiagram(URI.createURI("dawn://repo1//test.acore_diagram"),
        URI.createURI("cdo://repo1/test.acore"), new NullProgressMonitor());

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test.acore_diagram");

    String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());
    assertEquals(DawnAcoreDiagramEditor.ID, editorID);

    DawnEditorInput editorInput = new DawnEditorInput(resource.getURI());

    IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite()
        .getPage().openEditor(editorInput, editorID);

    assertInstanceOf(DawnAcoreDiagramEditor.class, editor);
  }
}
