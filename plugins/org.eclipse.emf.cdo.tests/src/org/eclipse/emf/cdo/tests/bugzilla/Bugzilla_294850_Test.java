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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.AbstractObjectConflictResolver;

import java.util.List;

/**
 * Bug 294850 - Lock.lock() should invoke conflict resolver in case of conflicts - but doesn't
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_294850_Test extends AbstractCDOTest
{
  private static String RESOURCE_NAME = "/r1";

  public void testConflictResolverOnLock100() throws Exception
  {
    for (int i = 0; i < 100; i++)
    {
      testConflictResolverOnLock();
    }
  }

  public void testConflictResolverOnLock() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    final TestResolver resolver = new TestResolver();
    tx.options().addConflictResolver(resolver);

    CDOResource r1 = tx.getOrCreateResource(RESOURCE_NAME);

    final Company company = Model1Factory.eINSTANCE.createCompany();
    r1.getContents().add(company);
    tx.commit();

    // Touch the company
    company.setName("aaa");

    // Touch in other session also
    doSecondSession();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return resolver.gotCalled && CDOUtil.getCDOObject(company).cdoState() == CDOState.CONFLICT;
      }
    }.assertNoTimeOut();

    // Lock company to trigger a refresh
    CDOUtil.getCDOObject(company).cdoWriteLock().lock(DEFAULT_TIMEOUT);

    // Object should be in conflict state now
    assertSame(CDOState.CONFLICT, CDOUtil.getCDOObject(company).cdoState());

    // And therefore, resolver should have been called
    assertTrue(resolver.gotCalled);

    tx.close();
    session.close();
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(RESOURCE_NAME);
    EList<EObject> contents = r1.getContents();
    Company c = (Company)contents.get(contents.size() - 1);

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
    protected void resolveConflict(CDOObject conflict, CDORevision oldRemoteRevision, CDORevisionDelta localDelta,
        CDORevisionDelta remoteDelta, List<CDORevisionDelta> allRemoteDeltas)
    {
      gotCalled = true;
    }
  }
}
