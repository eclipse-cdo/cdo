/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver.TakeRemoteChangesThenApplyLocalChanges;

/**
 * See bug 285441
 * 
 * @author Saulius Tvarijonas
 */
public class Bugzilla_285441_Test extends AbstractCDOTest
{
  public void testBugzilla_285441() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource("/test1");

    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    transaction1.commit();

    company1.getCategories().remove(0);

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.options().addConflictResolver(new TakeRemoteChangesThenApplyLocalChanges());
    CDOResource resource2 = transaction2.getOrCreateResource("/test1");

    Company company2 = (Company)resource2.getContents().get(0);
    Category category2 = getModel1Factory().createCategory();
    company2.getCategories().add(category2);

    transaction1.commit();
    session2.refresh();
    transaction2.commit();

    session1.close();
    session2.close();
  }
}
