/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * @author Eike Stepper
 */
public class Bugzilla_324544_Test extends AbstractCDOTest
{
  public void testCommitRefresh() throws CommitException
  {
    // user 1
    CDOSession s1 = openSession();
    CDOTransaction t1 = s1.openTransaction();
    CDOResource r1 = t1.createResource(getResourcePath("test"));
    Category c1 = getModel1Factory().createCategory();
    r1.getContents().add(c1);
    t1.commit();
    Product1 p1 = getModel1Factory().createProduct1();
    p1.setName("p1");
    c1.getProducts().add(p1);

    // user 2
    CDOSession s2 = openSession();
    s2.options().setPassiveUpdateEnabled(false);

    CDOTransaction t2 = s2.openTransaction();
    CDOResource r2 = t2.getResource(getResourcePath("test"));

    Category c2 = (Category)r2.getContents().get(0);
    Product1 p2 = getModel1Factory().createProduct1();
    p2.setName("p2");
    c2.getProducts().add(p2);
    c2.eAdapters().add(new AdapterImpl());

    // user 1
    t1.commit();

    // user2
    s2.refresh();
  }
}
