/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.LockStateRequest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Collections;

/**
 * Bug 466951 to avoid {@link LockStateRequest} for {@link CDOObject} in {@link CDOState#NEW} state.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_466951_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test that no {@link LockStateRequest} is sent to the server when calling {@link CDOObject#cdoLockState()} for {@link CDOObject} in {@link CDOState#NEW} state.
   */
  public void testCDOObjectCDOLockState() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(false);
    transaction.options().setLockNotificationEnabled(true);

    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_NAME));
    Company company1 = getModel1Factory().createCompany();
    resource.getContents().add(company1);

    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    try
    {
      assertEquals(0, signalCounter.removeCountFor(LockStateRequest.class));

      CDOObject cdoCompany1 = CDOUtil.getCDOObject(company1);
      CDOLockState lockState = cdoCompany1.cdoLockState();
      Object expectedLockedObject = cdoCompany1.cdoID();
      if (session.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(cdoCompany1.cdoID(), transaction.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertNull(lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());

      transaction.lockObjects(Collections.singleton(cdoCompany1), LockType.WRITE, -1);

      lockState = cdoCompany1.cdoLockState();
      expectedLockedObject = cdoCompany1.cdoID();
      if (session.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(cdoCompany1.cdoID(), transaction.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertEquals(((InternalCDOTransaction)transaction).getLockOwner(), lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());

      EcoreUtil.delete(company1);
      assertEquals(CDOState.TRANSIENT, cdoCompany1.cdoState());
      lockState = cdoCompany1.cdoLockState();
      assertNull(lockState);

      Company company2 = getModel1Factory().createCompany();
      resource.getContents().add(company2);

      CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);
      lockState = cdoCompany2.cdoLockState();
      expectedLockedObject = cdoCompany2.cdoID();
      if (session.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(cdoCompany2.cdoID(), transaction.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertNull(lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());
      assertEquals(0, signalCounter.removeCountFor(LockStateRequest.class));

      transaction.commit();

      lockState = cdoCompany2.cdoLockState();
      expectedLockedObject = cdoCompany2.cdoID();
      if (session.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(cdoCompany2.cdoID(), transaction.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertNull(lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());
      assertEquals(0, signalCounter.removeCountFor(LockStateRequest.class));
    }
    finally
    {
      signalCounter.dispose();
    }
  }
}
