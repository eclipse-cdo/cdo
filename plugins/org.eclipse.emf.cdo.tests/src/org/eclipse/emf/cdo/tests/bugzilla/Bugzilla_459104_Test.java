/*
 * Copyright (c) 2015, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.LockStateRequest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;

/**
 * Bug 459104 about {@link CDOObject#cdoLockState()} which should store loaded lock state into the
 * cache after a request to the server when {@link Options#isLockNotificationEnabled()} return true.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_459104_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test 2 consecutive call to {@link CDOObject#cdoLockState()}..
   */
  public void testCDOObject_GetLockState() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setLockNotificationEnabled(true);
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction1.commit();

    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session1).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    CDOObject companyCDOObject = CDOUtil.getCDOObject(company);

    companyCDOObject.cdoLockState();
    int nbLockStateRequest = signalCounter.getCountFor(LockStateRequest.class);
    assertEquals(1, nbLockStateRequest);

    companyCDOObject.cdoLockState();
    nbLockStateRequest = signalCounter.getCountFor(LockStateRequest.class);
    assertEquals(1, nbLockStateRequest);

    companyCDOObject.cdoLockState();
    nbLockStateRequest = signalCounter.getCountFor(LockStateRequest.class);
    assertEquals(1, nbLockStateRequest);

    protocol.removeListener(signalCounter);
  }
}
