/*
 * Copyright (c) 2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.tests.TestListener2;

import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class Bugzilla_469301_Test extends AbstractCDOTest
{
  public void testLockDetachedObject() throws Exception
  {
    Company company0 = getModel1Factory().createCompany();
    Company company1 = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(company0);
    resource.getContents().add(company1);
    transaction.commit();

    TestListener2 controlListener1 = openControlView(transaction, session, "LOCAL");
    TestListener2 controlListener2 = openControlView(transaction, openSession(), "REMOTE");

    // Detach
    List<? extends CDOObject> companies = CDOUtil.getCDOObjects(company0, company1);
    resource.getContents().removeAll(companies);

    // Lock
    transaction.lockObjects(companies, LockType.WRITE, DEFAULT_TIMEOUT);

    CDOLockState[] lockStates = transaction.getLockStatesOfObjects(companies);
    assertEquals(2, lockStates.length);

    for (CDOLockState lockState : lockStates)
    {
      assertEquals(((InternalCDOTransaction)transaction).getLockOwner(), lockState.getWriteLockOwner());

      LockState<Object, IView> serverLockState = serverLockState(session, lockState);
      assertEquals(serverTransaction(transaction), serverLockState.getWriteLockOwner());
    }

    assertEvent(controlListener1, transaction, CDOLockChangeInfo.Operation.LOCK);
    assertEvent(controlListener2, transaction, CDOLockChangeInfo.Operation.LOCK);

    // Unlock
    transaction.unlockObjects(companies, LockType.WRITE);

    lockStates = transaction.getLockStatesOfObjects(companies);
    assertEquals(2, lockStates.length);

    for (CDOLockState lockState : lockStates)
    {
      assertNull(lockState.getWriteLockOwner());
      assertNull(serverLockState(session, lockState));
    }

    assertEvent(controlListener1, transaction, CDOLockChangeInfo.Operation.UNLOCK);
    assertEvent(controlListener2, transaction, CDOLockChangeInfo.Operation.UNLOCK);
  }

  private TestListener2 openControlView(CDOTransaction transaction, CDOSession session, String name)
  {
    CDOView controlView = session.openView();
    controlView.options().setLockNotificationEnabled(true);

    int counter = 1;
    for (InternalCDOObject object : ((InternalCDOTransaction)transaction).getObjectsList())
    {
      // Load control object and make sure that it is not garbage collected.
      InternalCDOObject controlObject = controlView.getObject(object);
      controlView.properties().put(name + counter++, controlObject);
    }

    TestListener2 controlListener = new TestListener2(CDOViewLocksChangedEvent.class, name);
    controlView.addListener(controlListener);
    return controlListener;
  }

  private void assertEvent(TestListener2 controlListener, CDOView view, CDOLockChangeInfo.Operation operation)
  {
    CDOLockOwner lockOwner = ((InternalCDOView)view).getLockOwner();
    controlListener.waitFor(1, DEFAULT_TIMEOUT);

    CDOViewLocksChangedEvent event = (CDOViewLocksChangedEvent)controlListener.getEvents().get(0);
    assertEquals(operation, event.getOperation());
    assertEquals(LockType.WRITE, event.getLockType());
    assertEquals(lockOwner, event.getLockOwner());
    assertEquals(2, event.getLockStates().length);

    for (CDOLockState lockState : event.getLockStates())
    {
      assertEquals(event.getOperation() == Operation.LOCK ? lockOwner : null, lockState.getWriteLockOwner());
    }

    controlListener.clearEvents();
  }
}
