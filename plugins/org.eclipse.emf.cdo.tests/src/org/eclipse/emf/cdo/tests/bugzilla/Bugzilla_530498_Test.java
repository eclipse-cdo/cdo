/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Maxime Porhel (Obeo) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * Bug 530498: Null revision after transaction rollback on transaction with re-attached objects.
 *
 * @author Maxime Porhel
 */
public class Bugzilla_530498_Test extends AbstractCDOTest
{
  public void testNoNullRevisionAfterTransactionRollback() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Initialize model.
    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();

    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(company1);
    resource.getContents().add(company2);
    company1.getCustomers().add(customer);

    transaction.commit();

    CDOID company1ID = CDOUtil.getCDOObject(company1).cdoID();
    CDOID company2ID = CDOUtil.getCDOObject(company2).cdoID();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();

    // Simulate a Drag and Drop of Customer to another Company: in 2 steps, the drag command is a RemoveCommand
    // and then the drop command is an AddCommand.
    // Step 1: Simulate the first step of the DnD: the drag, a RemoveCommand equivalent.
    company1.getCustomers().remove(customer);

    assertFalse(transaction.getDirtyObjects().containsKey(customerID));
    assertTrue(transaction.getDirtyObjects().containsKey(company1ID));
    assertFalse(transaction.getDirtyObjects().containsKey(company2ID));

    assertTrue(transaction.getDetachedObjects().containsKey(customerID));
    assertTrue(transaction.getLastSavepoint().getAllDetachedObjects().containsKey(customerID));
    assertTrue(transaction.getLastSavepoint().getDetachedObjects().containsKey(customerID));
    assertFalse(transaction.getLastSavepoint().getReattachedObjects().containsKey(customerID));

    // Simulate the second step of the DnD : the drop, an AddCommand equivalent.
    company2.getCustomers().add(customer);

    assertTrue(transaction.getDirtyObjects().containsKey(customerID));
    assertTrue(transaction.getDirtyObjects().containsKey(company1ID));
    assertTrue(transaction.getDirtyObjects().containsKey(company2ID));

    assertFalse(transaction.getDetachedObjects().containsKey(customerID));
    assertFalse(transaction.getLastSavepoint().getAllDetachedObjects().containsKey(customerID));
    assertTrue(transaction.getLastSavepoint().getDetachedObjects().containsKey(customerID));
    assertTrue(transaction.getLastSavepoint().getReattachedObjects().containsKey(customerID));

    // Simulate DnD undo (or rollback of a EMF Transaction command doing the dnd: the ChangeDescription will be applied
    // but
    // the concrete resulting command will result in a simple add (thanks to the EMF containment management, the
    // cancellation of the "Remove" step will do nothing).
    company1.getCustomers().add(customer);

    assertEquals(isConfig(LEGACY), transaction.getDirtyObjects().containsKey(customerID));
    assertFalse(transaction.getDirtyObjects().containsKey(company1ID));
    assertFalse(transaction.getDirtyObjects().containsKey(company2ID));

    assertFalse(transaction.getDetachedObjects().containsKey(customerID));
    assertFalse(transaction.getLastSavepoint().getAllDetachedObjects().containsKey(customerID));
    assertEquals(isConfig(LEGACY), transaction.getLastSavepoint().getDetachedObjects().containsKey(customerID));
    assertEquals(isConfig(LEGACY), transaction.getLastSavepoint().getReattachedObjects().containsKey(customerID));

    // Rollback the transaction
    transaction.rollback();

    assertNotNull("Transaction has been rollbakced, the revision of the car element must not be null", CDOUtil.getCDOObject(customer).cdoRevision());
  }
}
