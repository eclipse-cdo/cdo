/*
 * Copyright (c) 2012, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * Delta Notification gives wrong position (-1) when list feature item is set
 * <p>
 * See bug 384496.
 *
 * @author Eike Stepper
 */
public class Bugzilla_384496_Test extends AbstractCDOTest
{
  public void testSimplest() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    Company root = getModel1Factory().createCompany();
    root.getCategories().add(getModel1Factory().createCategory());
    root.getCategories().add(getModel1Factory().createCategory());
    root.getCategories().add(getModel1Factory().createCategory());

    resource.getContents().add(root);
    transaction.commit();

    CDOView view = session.openView();
    view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    EObject watchedRoot = view.getResource(getResourcePath("res")).getContents().get(0);

    final TestAdapter adapter = new TestAdapter(watchedRoot);

    // Now modify from another session and checkout the adapter got the right position
    // Root has initially 3 children. Create an additional one and replace the one at index 1

    Category replacementChild = getModel1Factory().createCategory();
    adapter.assertNotifications(0);

    root.getCategories().set(1, replacementChild);
    adapter.assertNotifications(0);

    transaction.commit();
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return adapter.getNotifications().length != 0;
      }
    }.assertNoTimeOut();

    if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
    {
      adapter.assertNotifications(1);
    }
    else
    {
      adapter.assertNotifications(2);
    }

    assertEquals(1, adapter.getNotifications()[0].getPosition());
  }
}
