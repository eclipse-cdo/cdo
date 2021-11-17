/*
 * Copyright (c) 2016, 2018-2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.tests.AbstractLockingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.TestListener2;

/**
 * Bug 387564 - Ensure lock notification sending after invalidation for "lock/unlock objects on commit"
 *
 * @author Eike Stepper
 */
public class Bugzilla_387564_Test extends AbstractLockingTest
{
  private static final boolean DEBUG = false;

  @Override
  protected long getInvalidationDelay()
  {
    return 500L;
  }

  public void testLockEventAfterInvalidationEventSameSession() throws Exception
  {
    CDOSession session = openSession();
    runTest(session, session);
  }

  public void testLockEventAfterInvalidationEventDifferentSession() throws Exception
  {
    CDOSession session = openSession();
    CDOSession controlSession = openSession();
    runTest(session, controlSession);
  }

  private void runTest(CDOSession session, CDOSession controlSession) throws Exception
  {
    TestListener2 controlSessionListener = new TestListener2( //
        CDOSessionInvalidationEvent.class, //
        CDOSessionLocksChangedEvent.class) //
            .setName("SESSION");

    TestListener2 controlViewListener = new TestListener2( //
        CDOViewInvalidationEvent.class, //
        CDOViewLocksChangedEvent.class) //
            .setName("VIEW");

    Company company = getModel1Factory().createCompany();
    company.setName("Initial");

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(company);
    transaction.commit();

    CDOUtil.getCDOObject(company).cdoWriteLock().lock();
    waitForActiveLockNotifications();
    company.setName("Changed");

    controlSession.addListener(controlSessionListener);

    CDOView controlView = controlSession.openView();
    controlView.options().setLockNotificationEnabled(true);
    controlView.addListener(controlViewListener);

    // Load the company into the control view, so that the view emits invalidation events for it.
    Company controlObject = controlView.getObject(company);
    IOUtil.OUT().println(controlObject.getName());
    transaction.commit();
    waitForActiveLockNotifications();

    IEvent[] events = controlSessionListener.waitFor(2);
    assertEquals(1, TestListener2.countEvents(events, CDOSessionInvalidationEvent.class));
    assertEquals(1, TestListener2.countEvents(events, CDOSessionLocksChangedEvent.class));
    if (DEBUG)
    {
      IOUtil.OUT().println(controlSessionListener);
      IOUtil.OUT().println(controlSessionListener.formatEvents("   ", "\n"));
    }

    events = controlViewListener.waitFor(2);
    assertEquals(1, TestListener2.countEvents(events, CDOViewInvalidationEvent.class));
    assertEquals(1, TestListener2.countEvents(events, CDOViewLocksChangedEvent.class));
    if (DEBUG)
    {
      IOUtil.OUT().println(controlViewListener);
      IOUtil.OUT().println(controlViewListener.formatEvents("   ", "\n"));
    }

    IOUtil.OUT().println(controlObject.getName());
  }
}
