/*
 * Copyright (c) 2011, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.D;
import org.eclipse.emf.cdo.tests.model6.E;
import org.eclipse.emf.cdo.tests.model6.F;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;

import java.io.File;
import java.io.IOException;

/**
 * Ensures that :
 * <ul>
 * <li>Specific legacy models (as described in <a href="http://www.eclipse.org/forums/index.php/t/244583">Issues when
 * trying to import changes from PushTransaction in legacy mode</a>) can be correctly committed. See Bug 352204:
 * [Legacy] Failing event PREPARE in state CLEAN: state machine issue with legacy mode</li>
 * <li>When importing changes through a Push Transaction, features of non committed new elements are correctly set. See
 * Bug 359966: [Legacy] Issues when trying to import changes from PushTransaction in legacy mode</li>
 * </ul>
 *
 * @author Alex Lagarde
 */
public class Bugzilla_359966_Test extends AbstractCDOTest
{
  private static final String MODEL_LOCATION_PATH = "myResource";

  public void testWithoutPushTransaction() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(MODEL_LOCATION_PATH));
    resource.getContents().add(createModel());
    transaction.commit();

    addNewChildren(transaction);
    checkDiagramAsCorrectlyBeenModified(transaction);

    transaction.commit();
    checkDiagramAsCorrectlyBeenModified(transaction);
  }

  public void testWithReconstructSavepoints() throws Exception
  {
    importWithNewLegacyElements(true);
  }

  public void testWithoutReconstructSavepoints() throws Exception
  {
    importWithNewLegacyElements(false);
  }

  private void importWithNewLegacyElements(boolean reconstructSavePoints) throws Exception
  {
    // Step 1 : create model
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(MODEL_LOCATION_PATH));
    resource.getContents().add(createModel());
    transaction.commit();
    session.close();

    // Step 2 : open a push transaction a save locally a modification
    // Step 2.1 : open a push transaction
    File fileForStoringChanges = createTempFile();
    CDOPushTransaction pushTransaction = createPushTransaction(fileForStoringChanges, reconstructSavePoints);

    // Step 2.2 : create a new element
    addNewChildren(pushTransaction);

    // => make sure that diagram is modified as expected
    checkDiagramAsCorrectlyBeenModified(pushTransaction);

    // Step 2.3 : save locally
    pushTransaction.commit();
    pushTransaction.getSession().close();

    // Step 3 : load changes
    pushTransaction = createPushTransaction(fileForStoringChanges, reconstructSavePoints);

    // => check that diagram is modified as expected
    checkDiagramAsCorrectlyBeenModified(pushTransaction);

    pushTransaction.getSession().close();
  }

  /**
   * Creates a Push Transaction with the given options.
   *
   * @param fileForStoringChanges
   *          the file that CDOPushTransaction should use to store changes (can be null)
   * @param reconstructSavePoints
   *          if CDOSavePoints should be reconstructed when creating the PushTransaction
   */
  private CDOPushTransaction createPushTransaction(File fileForStoringChanges, boolean reconstructSavePoints) throws IOException
  {
    CDOSession session = openSession();
    CDOTransaction delegate = session.openTransaction();
    return new CDOPushTransaction(delegate, fileForStoringChanges, reconstructSavePoints);
  }

  /**
   * Ensures that the given diagram has correctly been modified :
   * <ul>
   * <li>it should contains 2 Nodes</li>
   * <li>the Second Node should have a "type" Type and its associated element should be the diagram's container's
   * container</li>
   * </ul>
   */
  @SuppressWarnings("rawtypes")
  private void checkDiagramAsCorrectlyBeenModified(CDOView view)
  {
    Diagram diagram = getDiagram(view);
    EList children = diagram.getPersistedChildren();

    assertEquals("Failure when loading changes: New elements have not been created.", 2, children.size());

    Node newElement = (Node)children.get(1);
    assertEquals("Attribute not correctly set.", "type", newElement.getType());
    assertEquals("Cross reference not correctly set.", diagram.eContainer().eContainer(), newElement.getElement());
  }

  /**
   * Returns the diagram model to use for this test.
   */
  private Diagram getDiagram(CDOView view)
  {
    CDOResource resource = view.getResource(getResourcePath(MODEL_LOCATION_PATH));
    F f = (F)resource.getContents().get(0);
    E e = f.getOwnedEs().get(0);
    A a = e.getOwnedAs().get(0);
    D d = a.getOwnedDs().get(0);
    return (Diagram)d.getData();
  }

  /**
   * Creates the test model.
   */
  @SuppressWarnings("unchecked")
  private EObject createModel()
  {
    F f = getModel6Factory().createF();
    E e = getModel6Factory().createE();
    A a = getModel6Factory().createA();
    D d = getModel6Factory().createD();

    Node child1 = NotationFactory.eINSTANCE.createNode();
    child1.setElement(a);

    Diagram diagram = NotationFactory.eINSTANCE.createDiagram();
    diagram.setMeasurementUnit(MeasurementUnit.PIXEL_LITERAL);
    diagram.getPersistedChildren().add(child1);

    d.setData(diagram);
    a.getOwnedDs().add(d);
    e.getOwnedAs().add(a);
    f.getOwnedEs().add(e);

    return f;
  }

  /**
   * Modifies the model by adding a new children of type Node to the Diagram.
   */
  @SuppressWarnings("unchecked")
  private void addNewChildren(CDOTransaction transaction)
  {
    Diagram diagram = getDiagram(transaction);
    A a = (A)diagram.eContainer().eContainer();

    Node child2 = NotationFactory.eINSTANCE.createNode();
    child2.setElement(a);
    child2.setType("type");
    diagram.getPersistedChildren().add(child2);
  }
}
