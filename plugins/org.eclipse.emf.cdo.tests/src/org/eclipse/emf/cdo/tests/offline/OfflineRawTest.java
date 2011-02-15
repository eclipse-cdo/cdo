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
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.tests.util.TestListener;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.om.monitor.NotifyingMonitor.ProgressEvent;

/**
 * @author Eike Stepper
 */
public class OfflineRawTest extends OfflineTest
{
  @Override
  protected boolean isRawReplication()
  {
    return true;
  }

  public void testMasterCommits_NotificationsFromClone() throws Exception
  {
    masterCommits_NotificationsFromClone();
  }

  /**
   * @since 4.0
   */
  public void _testNotification() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.setCommitComment("resource with one company created on clone");
    transaction.commit();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    TestListener sessionListener = new TestListener();
    session.addListener(sessionListener);

    TestListener transactionListener = new TestListener();
    transaction.addListener(transactionListener);

    {
      CDOSession masterSession = openSession(clone.getName() + "_master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.getResource("/my/resource");

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.setCommitComment("one company added on master");
      masterTransaction.commit();

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.setCommitComment("one company added on master");
      masterTransaction.commit();

      masterTransaction.close();
    }

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);
    sleep(1000);

    IEvent[] sessionEvents = sessionListener.getEvents();
    assertEquals(4, sessionEvents.length); // 3x repo state change + 1x invalidation

    CDOSessionInvalidationEvent sessionInvalidationEvent = (CDOSessionInvalidationEvent)sessionEvents[3];
    assertEquals(2, sessionInvalidationEvent.getNewObjects().size());
    assertEquals(1, sessionInvalidationEvent.getChangedObjects().size());
    assertEquals(0, sessionInvalidationEvent.getDetachedObjects().size());

    IEvent[] transactionEvents = transactionListener.getEvents();
    assertEquals(2, transactionEvents.length); // 1x invalidation + 1x adapters notified

    CDOViewInvalidationEvent viewInvalidationEvent = (CDOViewInvalidationEvent)transactionEvents[0];
    assertEquals(1, viewInvalidationEvent.getDirtyObjects().size());
    assertEquals(1, viewInvalidationEvent.getRevisionDeltas().size());
    assertEquals(0, viewInvalidationEvent.getDetachedObjects().size());

    CDORevisionDelta delta = viewInvalidationEvent.getRevisionDeltas().get(resource);
    assertEquals(null, delta);
    assertEquals(true, viewInvalidationEvent.getRevisionDeltas().containsKey(resource));
  }

  /**
   * @since 4.0
   */
  public void testNotificationAllDeltas() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    session.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.setCommitComment("resource with one company created on clone");
    transaction.commit();

    TestListener sessionListener = new TestListener();
    session.addListener(sessionListener);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    TestListener transactionListener = new TestListener();
    transaction.addListener(transactionListener);

    {
      CDOSession masterSession = openSession(clone.getName() + "_master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.getResource("/my/resource");

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.setCommitComment("one company added on master");
      masterTransaction.commit();

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.setCommitComment("one company added on master");
      masterTransaction.commit();

      masterTransaction.close();
    }

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);
    sleep(1000);

    IEvent[] sessionEvents = sessionListener.getEvents();
    assertEquals(4, sessionEvents.length); // 3x repo state change + 1x invalidation

    CDOSessionInvalidationEvent sessionInvalidationEvent = (CDOSessionInvalidationEvent)sessionEvents[3];
    assertEquals(2, sessionInvalidationEvent.getNewObjects().size());
    assertEquals(1, sessionInvalidationEvent.getChangedObjects().size());
    assertEquals(0, sessionInvalidationEvent.getDetachedObjects().size());

    IEvent[] transactionEvents = transactionListener.getEvents();
    assertEquals(2, transactionEvents.length); // 1x invalidation + 1x adapters notified

    CDOViewInvalidationEvent viewInvalidationEvent = (CDOViewInvalidationEvent)transactionEvents[0];
    assertEquals(1, viewInvalidationEvent.getDirtyObjects().size());
    assertEquals(1, viewInvalidationEvent.getRevisionDeltas().size());
    assertEquals(0, viewInvalidationEvent.getDetachedObjects().size());

    CDORevisionDelta delta = viewInvalidationEvent.getRevisionDeltas().get(resource);
    assertEquals(1, delta.getFeatureDeltas().size());

    CDOListFeatureDelta listDelta = (CDOListFeatureDelta)delta.getFeatureDeltas().get(0);
    assertEquals(2, listDelta.getListChanges().size());
  }

  /**
   * @since 4.0
   */
  public void testSyncProgressEvents() throws Exception
  {
    InternalSynchronizableRepository clone = getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.setCommitComment("resource with one company created on clone");
    transaction.commit();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    {
      CDOSession masterSession = openSession(clone.getName() + "_master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.getResource("/my/resource");

      for (int i = 0; i < 100; i++)
      {
        masterResource.getContents().add(getModel1Factory().createCompany());
        masterTransaction.setCommitComment("one company added on master");
        masterTransaction.commit();
      }

      masterTransaction.close();
    }

    final int[] workPercent = { 0 };
    TestListener listener = new TestListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        super.notifyEvent(event);
        if (event instanceof ProgressEvent)
        {
          ProgressEvent e = (ProgressEvent)event;
          workPercent[0] = (int)e.getWorkPercent();
          msg(e.getTask() + ": " + workPercent[0] + " percent");
        }
      }
    };

    clone.getSynchronizer().addListener(listener);

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);
    assertEquals(100, workPercent[0]);
  }
}
