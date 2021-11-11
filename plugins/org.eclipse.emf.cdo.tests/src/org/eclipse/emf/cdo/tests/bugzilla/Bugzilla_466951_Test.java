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
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setAutoReleaseLocksEnabled(false);
    transaction1.options().setLockNotificationEnabled(true);
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);

    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session1).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    try
    {
      assertEquals(0, signalCounter.removeCountFor(LockStateRequest.class));

      CDOObject companyCDOObject = CDOUtil.getCDOObject(company);
      CDOLockState lockState = companyCDOObject.cdoLockState();
      Object expectedLockedObject = companyCDOObject.cdoID();
      if (session1.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(companyCDOObject.cdoID(), transaction1.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertNull(lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());

      transaction1.lockObjects(Collections.singleton(companyCDOObject), LockType.WRITE, -1);

      lockState = companyCDOObject.cdoLockState();
      expectedLockedObject = companyCDOObject.cdoID();
      if (session1.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(companyCDOObject.cdoID(), transaction1.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertEquals(((InternalCDOTransaction)transaction1).getLockOwner(), lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());

      EcoreUtil.delete(company);
      assertEquals(CDOState.TRANSIENT, companyCDOObject.cdoState());
      lockState = companyCDOObject.cdoLockState();
      assertNull(lockState);

      Company company2 = getModel1Factory().createCompany();
      resource1.getContents().add(company2);

      CDOObject companyCDOObject2 = CDOUtil.getCDOObject(company2);
      lockState = companyCDOObject2.cdoLockState();
      expectedLockedObject = companyCDOObject2.cdoID();
      if (session1.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(companyCDOObject2.cdoID(), transaction1.getBranch());
      }

      assertEquals(expectedLockedObject, lockState.getLockedObject());
      assertTrue(lockState.getReadLockOwners().isEmpty());
      assertNull(lockState.getWriteLockOwner());
      assertNull(lockState.getWriteOptionOwner());
      assertEquals(0, signalCounter.removeCountFor(LockStateRequest.class));

      transaction1.commit();

      lockState = companyCDOObject2.cdoLockState();
      expectedLockedObject = companyCDOObject2.cdoID();
      if (session1.getRepositoryInfo().isSupportingBranches())
      {
        expectedLockedObject = CDOIDUtil.createIDAndBranch(companyCDOObject2.cdoID(), transaction1.getBranch());
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
