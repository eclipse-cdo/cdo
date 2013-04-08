/*
 * Copyright (c) 2004 - 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Steve Monnier - initial API and implementation
 *    Eike Stepper - adapted for more correct test model definition
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefault;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 404152.
 *
 * @author Steve Monnier
 */
public class Bugzilla_404152_Test extends AbstractCDOTest
{
  /**
   * This scenario validates that null can be set on a String feature with an empty string has default value has.
   */
  public void testSetNonDefaultNullString() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    // Create a company element and set is name has an empty string
    EmptyStringDefault localObject = getModel6Factory().createEmptyStringDefault();
    localObject.setAttribute("");
    resource.getContents().add(localObject);
    transaction.commit();

    CDOTransaction remoteTransaction = openSession().openTransaction();
    EmptyStringDefault remoteObject = remoteTransaction.getObject(localObject);

    // Validate that for another user (another transaction) the name is an empty string
    assertNotNull("Attribute should not be null", remoteObject.getAttribute());

    // Change company name from empty string to null
    assertNotNull("Attribute should be null", localObject.getAttribute());
    localObject.setAttribute(null);
    assertNull("Attribute should be null", localObject.getAttribute());

    // Validate that for another user (another transaction) the name is null
    commitAndSync(transaction, remoteTransaction);
    assertNull(remoteObject.getAttribute());
  }
}
