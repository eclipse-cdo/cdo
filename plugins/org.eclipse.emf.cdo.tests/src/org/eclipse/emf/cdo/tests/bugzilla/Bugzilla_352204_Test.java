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
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_352204_Test extends AbstractCDOTest
{
  private static final String MODEL_LOCATION_PATH = "myResource";

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

    // Step 2.3 : save locally
    pushTransaction.commit();
    pushTransaction.getSession().close();

    // Step 3 : load changes
    pushTransaction = createPushTransaction(fileForStoringChanges, reconstructSavePoints);

    pushTransaction.getSession().close();
  }

  private CDOPushTransaction createPushTransaction(File fileForStoringChanges, boolean reconstructSavePoints) throws IOException
  {
    CDOSession session = openSession();
    CDOTransaction delegate = session.openTransaction();
    return new CDOPushTransaction(delegate, fileForStoringChanges, reconstructSavePoints);
  }

  private Diagram getDiagram(CDOView view)
  {
    CDOResource resource = view.getResource(getResourcePath(MODEL_LOCATION_PATH));
    A a = (A)resource.getContents().get(0);
    D d = a.getOwnedDs().get(0);
    return (Diagram)d.getData();
  }

  /**
   * Creates the test model.
   */
  private EObject createModel()
  {
    A a = getModel6Factory().createA();
    D d = getModel6Factory().createD();

    Diagram diagram = NotationFactory.eINSTANCE.createDiagram();
    diagram.setMeasurementUnit(MeasurementUnit.PIXEL_LITERAL);

    d.setData(diagram);
    a.getOwnedDs().add(d);

    return a;
  }

  @SuppressWarnings("unchecked")
  private void addNewChildren(CDOTransaction transaction)
  {
    Diagram diagram = getDiagram(transaction);
    A a = (A)diagram.eContainer().eContainer();
    diagram.setElement(a);

    Node child2 = NotationFactory.eINSTANCE.createNode();
    child2.setType("type");
    diagram.getPersistedChildren().add(child2);
  }
}
