/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Laurent Redor (Obeo) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;

/**
 * @author Laurent Redor
 */
public class Bugzilla_552043_Test extends AbstractCDOTest
{
  // See bug 552043 comment #2 to understand why this test case is disabled.
  public void _testInverseCrossReferencesAfterRollback() throws Exception
  {
    // Ensure that the model is suitable for this test.
    assertTrue(model4Package.eINSTANCE.getGenRefMultiContained_Elements().isMany());
    assertTrue(model4Package.eINSTANCE.getGenRefMultiContained_Elements().isContainment());
    assertTrue(!model4Package.eINSTANCE.getGenRefMultiContained_Elements().isUnsettable());
    assertTrue(!model4Package.eINSTANCE.getRefSingleNonContainedNPL_Element().isMany());
    assertTrue(!model4Package.eINSTANCE.getRefSingleNonContainedNPL_Element().isUnsettable());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    GenRefMultiContained a = model4Factory.eINSTANCE.createGenRefMultiContained();
    ContainedElementNoOpposite b = model4Factory.eINSTANCE.createContainedElementNoOpposite();
    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();

    // Install a cross referencer on the resource.
    ECrossReferenceAdapter crossReferencer = new ECrossReferenceAdapter();
    resource.eAdapters().add(crossReferencer);

    RefSingleNonContainedNPL c = model4Factory.eINSTANCE.createRefSingleNonContainedNPL();
    a.getElements().add(c);
    c.setElement(b);

    // Ensure that cross referencer has recorded the c --> a cross reference.
    assertEquals("The element c should have one inverse cross reference.", 1, crossReferencer.getInverseReferences(c).size());
    assertEquals("The inverse cross reference of element c should be element a.", a, crossReferencer.getInverseReferences(c).iterator().next().getEObject());

    // Ensure that cross referencer has recorded the b --> c cross reference.
    assertEquals("The element b should have one inverse cross reference.", 1, crossReferencer.getInverseReferences(b).size());
    assertEquals("The inverse cross reference of element b should be element c.", c, crossReferencer.getInverseReferences(b).iterator().next().getEObject());

    TestAdapter aAdapter = new TestAdapter();
    a.eAdapters().add(aAdapter);

    TestAdapter bAdapter = new TestAdapter();
    b.eAdapters().add(bAdapter);

    TestAdapter cAdapter = new TestAdapter();
    c.eAdapters().add(cAdapter);

    // Rollback all the changes until the last successful commit.
    transaction.rollback();

    @SuppressWarnings("unused")
    Notification[] aNotifications = aAdapter.getNotifications();

    @SuppressWarnings("unused")
    Notification[] bNotifications = bAdapter.getNotifications();

    @SuppressWarnings("unused")
    Notification[] cNotifications = cAdapter.getNotifications();

    // The model must be reverted as it was just after the commit.
    assertEquals("The element a should contain 0 elements.", 0, a.getElements().size());
    assertEquals("The element c should reference no element.", null, c.getElement());

    // Ensure that cross referencer has cleaned the c --> a cross reference.
    assertEquals("The element c should have 0 inverse cross reference, because it has been removed during rollback.", 0,
        crossReferencer.getInverseReferences(c).size());

    // Ensure that cross referencer has cleaned the b --> c cross reference.
    assertEquals("The element b should have 0 inverse cross reference, because it has been unset from element c during rollback.", 0,
        crossReferencer.getInverseReferences(b).size());
  }

  public void testRollbackNewObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    GenRefMultiContained a = model4Factory.eINSTANCE.createGenRefMultiContained();
    ContainedElementNoOpposite b = model4Factory.eINSTANCE.createContainedElementNoOpposite();
    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();

    RefSingleNonContainedNPL c = model4Factory.eINSTANCE.createRefSingleNonContainedNPL();
    a.getElements().add(c);
    c.setElement(b);

    // Rollback all the changes until the last successful commit.
    transaction.rollback();

    // Element c must NOT be reverted as it was just after the commit.
    assertEquals("The element c should still reference element b.", b, c.getElement());
  }
}
