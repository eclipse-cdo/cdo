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

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;
import org.eclipse.emf.cdo.dawn.examples.acore.presentation.DawnAcoreEditor;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 */
public class DawnBasicEMFUITest extends AbstractCDOTest
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
    CDOResource resource = transaction.createResource("/test.acore");

    String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());
    assertEquals(DawnAcoreEditor.ID, editorID);
  }

  public void testEditorInput() throws PartInitException
  {
    final CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/sample/test.acore");

    String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());
    assertEquals(DawnAcoreEditor.ID, editorID);
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

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test.acore");

    ACoreRoot aCoreRoot = AcoreFactory.eINSTANCE.createACoreRoot();

    resource.getContents().add(aCoreRoot);
    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw new RuntimeException(ex);
    }

    String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());
    assertEquals(DawnAcoreEditor.ID, editorID);

    DawnEditorInput editorInput = new DawnEditorInput(resource.getURI());

    // TODO Test case fails because the ConnectionUtil is not initialized. Fake it here or use the DawnExplorer to open
    // the editor.
    IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite()
        .getPage().openEditor(editorInput, editorID);

    sleep(5000);
    assertInstanceOf(DawnAcoreEditor.class, editor);
  }
}
