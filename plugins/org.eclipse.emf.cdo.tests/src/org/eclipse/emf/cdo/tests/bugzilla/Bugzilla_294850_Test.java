/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver;

/**
 * Bug 294850 - Lock.lock() should invoke conflict resolver in case of conflicts - but doesn't
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_294850_Test extends AbstractCDOTest
{
  private static String RESOURCE_NAME = "/r1";

  public void testBugzilla_294850()
  {
    CDOSession session = openModel1Session();
    CDOTransaction tx = session.openTransaction();

    TestResolver resolver = new TestResolver();
    tx.options().addConflictResolver(resolver);

    CDOResource r1 = tx.createResource(RESOURCE_NAME);

    Company company = Model1Factory.eINSTANCE.createCompany();
    r1.getContents().add(company);
    tx.commit();

    // Touch the company
    company.setName("aaa");

    // Touch in other session also
    doSecondSession();

    assertSame(CDOState.DIRTY, ((CDOObject)company).cdoState());

    // Lock company to trigger a refresh
    ((CDOObject)company).cdoWriteLock().lock();

    // Object should be in conflict state now
    assertSame(CDOState.CONFLICT, ((CDOObject)company).cdoState());

    // And therefore, resolver should have been called
    assertTrue(resolver.gotCalled);

    tx.close();
    session.close();
  }

  private void doSecondSession()
  {
    CDOSession session = openModel1Session();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(RESOURCE_NAME);
    Company c = (Company)r1.getContents().get(0);

    // Touch the company
    c.setName("bbb");

    tx.commit();
    tx.close();
    session.close();
  }

  /**
   * @author Caspar De Groot
   */
  private static final class TestResolver extends AbstractObjectConflictResolver
  {
    public boolean gotCalled;

    @Override
    protected void resolveConflict(CDOObject conflict, CDORevisionDelta revisionDelta)
    {
      gotCalled = true;
    }
  }
}
