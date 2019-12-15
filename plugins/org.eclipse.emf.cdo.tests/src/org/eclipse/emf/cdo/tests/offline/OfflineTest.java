/*
 * Copyright (c) 2011-2013, 2017-2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.syncing.OfflineClone;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model3.File;
import org.eclipse.emf.cdo.tests.model3.Image;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.NotifyingMonitor.ProgressEvent;
import org.eclipse.net4j.util.tests.TestListener;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class OfflineTest extends AbstractSyncingTest
{
  public void testMasterCommits_ArrivalInClone() throws Exception
  {
    CDOSession session = openSession("master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    // 2 * Root resource + folder + resource + company
    int expectedRevisions = 2 + 1 + 1 + 1;

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    checkRevisions(getRepository(), timeStamp, expectedRevisions);

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 1; // Changed company
      checkRevisions(getRepository(), timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + new category
      checkRevisions(getRepository(), timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + detached category
      checkRevisions(getRepository(), timeStamp, expectedRevisions);
    }

    session.close();
  }

  protected void masterCommits_NotificationsFromClone() throws Exception
  {
    CDOSession masterSession = openSession("master");
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    TestListener listener = new TestListener();
    CDOSession cloneSession = openSession();
    cloneSession.addListener(listener);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    assertEquals(true, cloneSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
    checkEvent(listener, 1, 3, 1, 0);

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
      assertEquals(true, cloneSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
      checkEvent(listener, 0, 0, 1, 0);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
      assertEquals(true, cloneSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
      checkEvent(listener, 0, 1, 1, 0);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
      assertEquals(true, cloneSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
      checkEvent(listener, 0, 0, 1, 1);
    }

    cloneSession.close();
    masterSession.close();
  }

  public void testClientCommits() throws Exception
  {
    InternalRepository clone = getRepository();
    InternalRepository master = getRepository("master");

    TestListener listener = new TestListener();
    CDOSession masterSession = openSession(master.getName());
    masterSession.addListener(listener);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession cloneSession = openSession();
    waitForOnline(cloneSession.getRepositoryInfo());

    CDOTransaction transaction = cloneSession.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    transaction.commit();

    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);

    checkRevision(company, master, "master");
    checkRevision(company, clone, "clone");
  }

  public void testDisconnectAndSyncAddition() throws Exception
  {
    TestListener listener = new TestListener();
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.commit();

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.commit();

      masterTransaction.close();
      masterSession.addListener(listener);

      getOfflineConfig().startMasterTransport();
      waitForOnline(clone);
    }

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    transaction.commit();

    sleep(1000);

    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);
  }

  public void testDisconnectAndSyncChange() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));

      Company comp = getModel1Factory().createCompany();
      masterResource.getContents().add(comp);
      masterTransaction.commit();

      comp.setName("MODIFICATION");
      masterTransaction.commit();
      masterTransaction.close();

      getOfflineConfig().startMasterTransport();
      waitForOnline(clone);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/master/resource"));

    Company company = (Company)resource.getContents().get(0);
    assertEquals("MODIFICATION", company.getName());
  }

  public void testDisconnectAndSyncRemoval() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));

      Company comp = getModel1Factory().createCompany();
      masterResource.getContents().add(comp);
      masterTransaction.commit();

      masterResource.getContents().remove(comp);
      masterTransaction.commit();
      masterTransaction.close();

      getOfflineConfig().startMasterTransport();
      waitForOnline(clone);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/master/resource"));

    assertEquals(0, resource.getContents().size());
  }

  public void testDisconnectAndCommit() throws Exception
  {
    OfflineClone clone = (OfflineClone)getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(true, commitInfo.getBranch().isLocal());
    assertEquals(true, transaction.getBranch().isLocal());
  }

  public void testDisconnectAndCommitAndMerge() throws Exception
  {
    OfflineClone clone = (OfflineClone)getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    resource.getContents().add(getModel1Factory().createCompany());
    CDOCommitInfo commitInfo = transaction.commit();

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    DefaultCDOMerger.PerFeature.ManyValued merger = new DefaultCDOMerger.PerFeature.ManyValued();

    transaction.setBranch(session.getBranchManager().getMainBranch());
    transaction.merge(commitInfo, merger);

    assertEquals(1, transaction.getNewObjects().size());
    CDOObject offlineCompany = transaction.getNewObjects().values().iterator().next();
    if (getRepositoryConfig().idGenerationLocation() != IDGenerationLocation.CLIENT)
    {
      assertEquals(CDOID.Type.TEMP_OBJECT, offlineCompany.cdoID().getType());
    }

    commitInfo = transaction.commit();
    assertEquals(CDOID.Type.OBJECT, offlineCompany.cdoID().getType());
  }

  /**
   * @since 4.0
   */
  public void _testDisconnectAndCommitAndMergeWithNewPackages() throws Exception
  {
    OfflineClone clone = (OfflineClone)getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    CDOCommitInfo commitInfo = transaction.commit();

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    transaction.setBranch(session.getBranchManager().getMainBranch());
    transaction.merge(commitInfo, new DefaultCDOMerger.PerFeature.ManyValued());

    transaction.commit();
  }

  public void testManyCommitInfos_Initial() throws Exception
  {
    OfflineClone clone = (OfflineClone)getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    CDOSession masterSession = openSession("master");
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    for (int i = 0; i < 10; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Company" + i);
      resource.getContents().add(company);
    }

    transaction.setCommitComment("Creation");
    long timeStamp = transaction.commit().getTimeStamp();
    msg(timeStamp);

    for (int k = 0; k < 10; k++)
    {
      sleep(SLEEP_MILLIS);
      for (int i = 0; i < 10; i++)
      {
        Company company = (Company)resource.getContents().get(i);
        company.setName("Company" + i + "_" + transaction.getBranch().getID() + "_" + k);
      }

      transaction.setCommitComment("Modification");
      timeStamp = transaction.commit().getTimeStamp();
      msg(timeStamp);
    }

    masterSession.close();
    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    final List<CDOCommitInfo> result = new ArrayList<>();
    CDOSession session = openSession();
    session.getCommitInfoManager().getCommitInfos(null, 0L, 0L, new CDOCommitInfoHandler()
    {
      @Override
      public void handleCommitInfo(CDOCommitInfo commitInfo)
      {
        result.add(commitInfo);
        commitInfo.getNewPackageUnits();
      }
    });

    for (CDOCommitInfo commitInfo : result)
    {
      System.out.println("-----> " + commitInfo);
    }

    assertEquals(12, result.size());
  }

  /**
   * See bug 364548.
   */
  public void testEmptyCommit() throws Exception
  {
    InternalRepository master = getRepository("master");

    TestListener listener = new TestListener();
    CDOSession masterSession = openSession(master.getName());
    masterSession.addListener(listener);

    CDOSession cloneSession = openSession();
    waitForOnline(cloneSession.getRepositoryInfo());

    CDOTransaction transaction = cloneSession.openTransaction();
    transaction.commit();
  }

  public void _testDisconnectAndSyncBLOB() throws Exception
  {
    TestListener listener = new TestListener();
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));

      InputStream inputStream = null;

      try
      {
        inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
        CDOBlob blob = new CDOBlob(inputStream);

        Image image = getModel3Factory().createImage();
        image.setWidth(320);
        image.setHeight(200);
        image.setData(blob);

        masterResource.getContents().add(image);
      }
      finally
      {
        IOUtil.close(inputStream);
      }

      masterTransaction.commit();
      masterTransaction.close();
      masterSession.addListener(listener);

      getOfflineConfig().startMasterTransport();
      waitForOnline(clone);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/master/resource"));

    Image image = (Image)resource.getContents().get(0);

    InputStream fromDisk = null;

    try
    {
      fromDisk = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      IOUtil.equals(fromDisk, image.getData().getContents());
    }
    finally
    {
      IOUtil.close(fromDisk);
    }
  }

  public void _testDisconnectAndSyncCLOB() throws Exception
  {
    TestListener listener = new TestListener();
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));

      InputStream inputStream = null;

      try
      {
        inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
        CDOClob clob = new CDOClob(new InputStreamReader(inputStream));

        File file = getModel3Factory().createFile();
        file.setName("Ecore.uml");
        file.setData(clob);

        masterResource.getContents().add(file);
      }
      finally
      {
        IOUtil.close(inputStream);
      }

      try
      {
        inputStream = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
        CDOClob clob = new CDOClob(new InputStreamReader(inputStream));

        File file = getModel3Factory().createFile();
        file.setName("plugin.properties");
        file.setData(clob);

        masterResource.getContents().add(file);
      }
      finally
      {
        IOUtil.close(inputStream);
      }

      masterTransaction.commit();
      masterTransaction.close();
      masterSession.addListener(listener);

      getOfflineConfig().startMasterTransport();
      waitForOnline(clone);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/master/resource"));

    File file = (File)resource.getContents().get(0);

    InputStream fromDisk = null;

    try
    {
      fromDisk = OM.BUNDLE.getInputStream("backup-tests/Ecore.uml");
      IOUtil.equals(new InputStreamReader(fromDisk), file.getData().getContents());
    }
    finally
    {
      IOUtil.close(fromDisk);
    }
  }

  public void _testMasterCommits_NotificationsFromClone() throws Exception
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
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

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
      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.getResource(getResourcePath("/my/resource"));

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

    int count = 0;
    for (IEvent sessionEvent : sessionEvents)
    {
      if (sessionEvent instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent sessionInvalidationEvent = (CDOSessionInvalidationEvent)sessionEvent;
        assertEquals(2, sessionInvalidationEvent.getNewObjects().size());
        assertEquals(1, sessionInvalidationEvent.getChangedObjects().size());
        assertEquals(0, sessionInvalidationEvent.getDetachedObjects().size());
        ++count;
      }
    }

    assertEquals(1, count);

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

    CDOSession session = openSession(); // Session2 [repo1]
    session.options().setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

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
      CDOSession masterSession = openSession("master"); // Session3 [master]
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.getResource(getResourcePath("/my/resource"));

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

    int count = 0;
    for (IEvent sessionEvent : sessionEvents)
    {
      if (sessionEvent instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent sessionInvalidationEvent = (CDOSessionInvalidationEvent)sessionEvent;
        assertEquals(2, sessionInvalidationEvent.getNewObjects().size());
        assertEquals(1, sessionInvalidationEvent.getChangedObjects().size());
        assertEquals(0, sessionInvalidationEvent.getDetachedObjects().size());
        ++count;
      }
    }

    assertEquals(1, count);

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
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(getModel1Factory().createCompany());
    transaction.setCommitComment("resource with one company created on clone");
    transaction.commit();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    {
      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.getResource(getResourcePath("/my/resource"));

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
