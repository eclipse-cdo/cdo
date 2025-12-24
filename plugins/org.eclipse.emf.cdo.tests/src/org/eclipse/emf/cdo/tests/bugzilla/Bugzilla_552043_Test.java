/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Laurent Redor (Obeo) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;

import java.util.HashMap;

/**
 * @author Laurent Redor
 */
public class Bugzilla_552043_Test extends AbstractCDOTest
{
  public void testRollbackInverseCrossReferencer() throws Exception
  {
    runRollbackInverseCrossReferencer(false);
  }

  public void testRollbackInverseCrossReferencer_WithAttachedRevisionsMap() throws Exception
  {
    runRollbackInverseCrossReferencer(true);
  }

  private void runRollbackInverseCrossReferencer(boolean withAttachedRevisionsMap) throws Exception
  {
    // Ensure that the model is suitable for this test.
    assertTrue(getModel4Package().getGenRefMultiContained_Elements().isMany());
    assertTrue(getModel4Package().getGenRefMultiContained_Elements().isContainment());
    assertTrue(!getModel4Package().getGenRefMultiContained_Elements().isUnsettable());
    assertTrue(!getModel4Package().getRefSingleNonContainedNPL_Element().isMany());
    assertTrue(!getModel4Package().getRefSingleNonContainedNPL_Element().isUnsettable());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    if (withAttachedRevisionsMap)
    {
      transaction.options().setAttachedRevisionsMap(new HashMap<CDOID, CDORevision>());
    }

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    GenRefMultiContained a = getModel4Factory().createGenRefMultiContained();
    ContainedElementNoOpposite b = getModel4Factory().createContainedElementNoOpposite();
    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();

    // Install a cross referencer on the resource.
    ECrossReferenceAdapter crossReferencer = new ECrossReferenceAdapter();
    resource.eAdapters().add(crossReferencer);

    RefSingleNonContainedNPL c = getModel4Factory().createRefSingleNonContainedNPL();
    a.getElements().add(c); // ATTACH element c.
    c.setElement(b);

    // Ensure that the cross referencer has recorded the c --> a cross reference.
    assertEquals("The element c should have one inverse cross reference.", 1, crossReferencer.getInverseReferences(c).size());
    assertEquals("The inverse cross reference of element c should be element a.", a, crossReferencer.getInverseReferences(c).iterator().next().getEObject());

    // Ensure that the cross referencer has recorded the b --> c cross reference.
    assertEquals("The element b should have one inverse cross reference.", 1, crossReferencer.getInverseReferences(b).size());
    assertEquals("The inverse cross reference of element b should be element c.", c, crossReferencer.getInverseReferences(b).iterator().next().getEObject());

    TestAdapter aAdapter = new TestAdapter(a);
    TestAdapter bAdapter = new TestAdapter(b);
    TestAdapter cAdapter = new TestAdapter(c);

    // Rollback all the changes until the last successful commit.
    transaction.rollback();

    @SuppressWarnings("unused")
    Notification[] aNotifications = aAdapter.getNotifications();

    @SuppressWarnings("unused")
    Notification[] bNotifications = bAdapter.getNotifications();

    @SuppressWarnings("unused")
    Notification[] cNotifications = cAdapter.getNotifications();

    assertEquals("The element a should contain 0 elements.", 0, a.getElements().size());
    if (withAttachedRevisionsMap)
    {
      assertEquals("The element c should reference no element.", null, c.getElement());
    }
    else
    {
      assertEquals("The element c should reference the b element.", b, c.getElement());
    }

    // Ensure that the cross referencer has cleaned the c --> a cross reference.
    assertEquals("The element c should have 0 inverse cross reference, because it has been removed during rollback.", 0,
        crossReferencer.getInverseReferences(c).size());

    if (withAttachedRevisionsMap)
    {
      // Ensure that the cross referencer has cleaned the b --> c cross reference.
      assertEquals("The element b should have 0 inverse cross reference, because it has been unset from element c during rollback.", 0,
          crossReferencer.getInverseReferences(b).size());
    }
    else
    {
      // Ensure that the cross referencer has NOT cleaned the b --> c cross reference.
      assertEquals("The element b should have 1 inverse cross reference, because it has NOT been unset from element c during rollback.", 1,
          crossReferencer.getInverseReferences(b).size());
      assertEquals("The inverse cross reference of element b should be element c.", c, crossReferencer.getInverseReferences(b).iterator().next().getEObject());
    }
  }

  public void testRollbackNewObject() throws Exception
  {
    runRollbackNewObject(false);
  }

  public void testRollbackNewObject_WithAttachedRevisionsMap() throws Exception
  {
    runRollbackNewObject(true);
  }

  private void runRollbackNewObject(boolean withAttachedRevisionsMap) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    if (withAttachedRevisionsMap)
    {
      transaction.options().setAttachedRevisionsMap(new HashMap<CDOID, CDORevision>());
    }

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    GenRefMultiContained a = getModel4Factory().createGenRefMultiContained();
    ContainedElementNoOpposite b = getModel4Factory().createContainedElementNoOpposite();
    resource.getContents().add(a);
    resource.getContents().add(b);
    transaction.commit();

    RefSingleNonContainedNPL c = getModel4Factory().createRefSingleNonContainedNPL();
    a.getElements().add(c); // ATTACH element c.
    c.setElement(b);

    // Rollback all the changes until the last successful commit.
    transaction.rollback();

    assertEquals("The element c should still reference element b.", withAttachedRevisionsMap ? null : b, c.getElement());
  }

  public void testRollbackMultipleNewObject() throws Exception
  {
    runRollbackMultipleNewObject(false);
  }

  public void testRollbackMultipleNewObject_WithAttachedRevisionsMap() throws Exception
  {
    runRollbackMultipleNewObject(true);
  }

  private void runRollbackMultipleNewObject(boolean withAttachedRevisionsMap) throws Exception
  {
    Category category0 = getModel1Factory().createCategory();
    category0.setName("category0");

    Category category1 = getModel1Factory().createCategory();
    category1.setName("category1");
    category0.getCategories().add(category1);

    Category category2a = getModel1Factory().createCategory();
    category2a.setName("category2a");
    category1.getCategories().add(category2a);

    Category category2b = getModel1Factory().createCategory();
    category2b.setName("category2b");
    category1.getCategories().add(category2b);

    Category category2c = getModel1Factory().createCategory();
    category2c.setName("category2c");
    category1.getCategories().add(category2c);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    if (withAttachedRevisionsMap)
    {
      transaction.options().setAttachedRevisionsMap(new HashMap<CDOID, CDORevision>());
    }

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    company.getCategories().add(category1);
    category2a.setName("xxx2a");
    category2b.setName("xxx2b");
    category2c.setName("xxx2c");
    assertEquals(company, category1.eContainer());
    assertEquals(category1, category2a.eContainer());
    assertEquals(category1, category2b.eContainer());
    assertEquals(category1, category2c.eContainer());
    assertEquals("xxx2a", category2a.getName());
    assertEquals("xxx2b", category2b.getName());
    assertEquals("xxx2c", category2c.getName());

    TestAdapter adapter1 = new TestAdapter(category1);
    TestAdapter adapter2a = new TestAdapter(category2a);
    TestAdapter adapter2b = new TestAdapter(category2b);
    TestAdapter adapter2c = new TestAdapter(category2c);

    transaction.rollback();
    assertEquals(null, category1.eContainer());
    assertEquals(category1, category2a.eContainer());
    assertEquals(category1, category2b.eContainer());
    assertEquals(category1, category2c.eContainer());
    assertEquals(withAttachedRevisionsMap ? "category2a" : "xxx2a", category2a.getName());
    assertEquals(withAttachedRevisionsMap ? "category2b" : "xxx2b", category2b.getName());
    assertEquals(withAttachedRevisionsMap ? "category2c" : "xxx2c", category2c.getName());
    adapter1.assertNotifications(1);
    adapter2a.assertNotifications(withAttachedRevisionsMap ? 1 : 0);
    adapter2b.assertNotifications(withAttachedRevisionsMap ? 1 : 0);
    adapter2c.assertNotifications(withAttachedRevisionsMap ? 1 : 0);
  }
}
