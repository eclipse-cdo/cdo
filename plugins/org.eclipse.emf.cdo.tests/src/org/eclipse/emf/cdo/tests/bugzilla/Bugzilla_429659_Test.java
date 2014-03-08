/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alex Lagarde - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.DanglingIntegrityException;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Bug 429659 - BasicEStoreEList.unset() can cause DanglingIntegrityExceptions at commit time.
 *
 * @author Alex Lagarde
 */
public class Bugzilla_429659_Test extends AbstractCDOTest
{
  private CDOSession session;

  private CDOTransaction transaction;

  private Root root;

  private TransactionalEditingDomain editingDomain;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    session = openSession();
    editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
    ResourceSet resourceSet = editingDomain.getResourceSet();
    transaction = session.openTransaction(resourceSet);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("resource"));
    root = getModel6Factory().createRoot();
    resource.getContents().add(root);
  }

  /**
   * Ensures that when creating and then deleting an element from an empty containment list, the {@link CDOTransaction}'s new objects map is empty.
   */
  public void testUndoElementCreationOnEmptyList() throws Exception
  {
    // Testing on an empty containment list
    doTestUndoElementCreation();
  }

  /**
   * Ensures that when creating and then deleting an element from a non-empty containment list, the {@link CDOTransaction}'s new objects map is empty.
   */
  public void testUndoElementCreationOnNonEmptyList() throws Exception
  {
    // Add an element to the list before launching the test
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        root.getListA().add(getModel6Factory().createBaseObject());
      }
    });

    doTestUndoElementCreation();
  }

  /**
   * Ensures that when creating and then deleting an element from a containment list, the {@link CDOTransaction}'s new objects map is empty.
   */
  private void doTestUndoElementCreation() throws Exception
  {
    transaction.commit();

    // Step 1: add an new element to the containment list
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        root.getListA().add(getModel6Factory().createBaseObject());
      }
    });

    assertEquals(1, transaction.getNewObjects().size());

    // Step 2: undo element creation
    editingDomain.getCommandStack().undo();
    assertEquals("New elements for which creation was undone should not appear in the transaction's new objects", 0,
        transaction.getNewObjects().size());

    // Step 3: commit
    transaction.commit();
  }

  /**
   * Ensures that the following scenario does not lead to any {@link DanglingIntegrityException} at commit:
   * <ul>
   * <li>Create 2 objects c1 & c2</li>
   * <li>Create an object referencing c2 inside c1</li>
   * <li>Undo all these operations</li>
   * <li>commit</li>
   * </ul>
   */
  public void testUndoSeveralElementsCreation() throws Exception
  {
    // Step 1: create first element (c1)
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        root.getListA().add(getModel6Factory().createContainmentObject());
      }
    });

    // Step 2: create second element (c2)
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        root.getListA().add(getModel6Factory().createContainmentObject());
      }
    });

    // Step 3: create an element inside c1 which references c2
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      protected void doExecute()
      {
        ReferenceObject referenceObject = getModel6Factory().createReferenceObject();
        referenceObject.setReferenceOptional(root.getListA().get(1));
        ((ContainmentObject)root.getListA().get(0)).getContainmentList().add(referenceObject);
      }
    });

    // Step 4: undo the 3 previous operations (notice that commit will fail without undoing the first one)
    editingDomain.getCommandStack().undo();
    editingDomain.getCommandStack().undo();
    editingDomain.getCommandStack().undo();

    // Step 5: commit: this should not cause any exception
    transaction.commit();
  }
}
