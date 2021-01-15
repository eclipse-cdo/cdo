/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Bug 570123 - CommitConflictException (without conflict) if reattached object modified by another session.
 *
 * @author Eike Stepper
 */
public class Bugzilla_570123_Test extends AbstractCDOTest
{
  // In legacy mode there are no CONTAINER notifications, adjusting cleanRevisions doesn't work there ;-(
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testReattachInvalidate() throws Exception
  {
    // Create session1 and init testModel
    CDOSession session1 = openSession();

    CDOTransaction transaction1 = session1.openTransaction();
    Resource resource = transaction1.getOrCreateResource(getResourcePath("res1"));

    Category category1 = getModel1Factory().createCategory();
    resource.getContents().add(category1);

    Product1 product1 = getModel1Factory().createProduct1();
    product1.setName("Product");
    category1.getProducts().add(product1);

    transaction1.commit();

    // Open session2
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    // find model element in session2
    Category category2 = transaction2.getObject(category1);
    Product1 product2 = category2.getProducts().get(0);

    // detach and re-attach in session2
    category2.getProducts().remove(product2);
    category2.getProducts().add(product2);

    // change product1 name in session1 and commit
    product1.setName("NewName");
    commitAndSync(transaction1, transaction2);

    // detach and re-attach in session2 again
    category2.getProducts().remove(product2);
    category2.getProducts().add(product2);

    // bug: exception on commit, because cleanRevisions map of transaction2 was not updated during invalidation
    transaction2.commit();
  }
}
