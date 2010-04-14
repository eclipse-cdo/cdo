/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.clone.CloneRepository;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class OfflineTest extends AbstractCDOTest
{
  protected static final int SLEEP_MILLIS = 1000;

  public OfflineConfig getOfflineConfig()
  {
    return (OfflineConfig)getRepositoryConfig();
  }

  @Override
  public boolean isValid()
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig instanceof OfflineConfig;
  }

  protected boolean isSqueezedCommitInfos()
  {
    return false;
  }

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(OfflineConfig.PROP_TEST_SQUEEZE_COMMIT_INFOS, isSqueezedCommitInfos());
    return testProperties;
  }

  public void testMasterCommits_ArrivalInClone() throws Exception
  {
    CDOSession session = openSession(getRepository().getName() + "_master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    // 2 * Root resource + folder + resource + company
    int expectedRevisions = 2 + 1 + 1 + 1;

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    checkClone(timeStamp, expectedRevisions);

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 1; // Changed company
      checkClone(timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + new category
      checkClone(timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + detached category
      checkClone(timeStamp, expectedRevisions);
    }

    session.close();
  }

  protected void checkClone(final long timeStamp, int expectedRevisions) throws InterruptedException
  {
    final InternalRepository repository = getRepository();
    long lastCommitTimeStamp = repository.getLastCommitTimeStamp();
    while (lastCommitTimeStamp < timeStamp)
    {
      lastCommitTimeStamp = repository.waitForCommit(DEFAULT_TIMEOUT);
    }

    InternalStore store = repository.getStore();
    if (store instanceof MEMStore)
    {
      Map<CDOBranch, List<CDORevision>> allRevisions = ((MEMStore)store).getAllRevisions();
      System.out.println("\n\n\n\n\n\n\n\n\n\n" + CDORevisionUtil.dumpAllRevisions(allRevisions));

      List<CDORevision> revisions = allRevisions.get(repository.getBranchManager().getMainBranch());
      assertEquals(expectedRevisions, revisions.size());
    }
  }

  public void testMasterCommits_NotificationsFromClone() throws Exception
  {
    CDOSession masterSession = openSession(getRepository().getName() + "_master");
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

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

  protected void checkEvent(TestListener listener, int newPackageUnits, int newObjects, int changedObjects,
      int detachedObjects)
  {
    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);

    IEvent event = events[0];
    if (event instanceof CDOSessionInvalidationEvent)
    {
      CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
      assertEquals(newPackageUnits, e.getNewPackageUnits().size());
      assertEquals(newObjects, e.getNewObjects().size());
      assertEquals(changedObjects, e.getChangedObjects().size());
      assertEquals(detachedObjects, e.getDetachedObjects().size());
    }
    else
    {
      fail("Invalid event: " + event);
    }

    listener.clearEvents();
  }

  public void testClientCommits() throws Exception
  {
    InternalRepository clone = getRepository();
    InternalRepository master = getRepository(clone.getName() + "_master");

    TestListener listener = new TestListener();
    CDOSession masterSession = openSession(master.getName());
    masterSession.addListener(listener);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession cloneSession = openSession();
    waitForOnline(cloneSession.getRepositoryInfo());

    CDOTransaction transaction = cloneSession.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(company);
    transaction.commit();

    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);

    checkRevision(company, master, "master");
    checkRevision(company, clone, "clone");
  }

  protected void checkRevision(EObject object, InternalRepository repository, String location)
  {
    // Check if revision arrived in cache
    checkRevision(object, repository.getRevisionManager().getCache().getAllRevisions(), location + " cache");

    // Check if revision arrived in store
    InternalStore store = repository.getStore();
    if (store instanceof IMEMStore)
    {
      checkRevision(object, ((IMEMStore)store).getAllRevisions(), location + " store");
    }
  }

  protected void checkRevision(EObject object, Map<CDOBranch, List<CDORevision>> allRevisions, String location)
  {
    CDORevision revision = CDOUtil.getCDOObject(object).cdoRevision();
    List<CDORevision> revisions = allRevisions.get(revision.getBranch());
    for (CDORevision rev : revisions)
    {
      if (revision.equals(rev))
      {
        return;
      }
    }

    fail("Revision missing from " + location + ": " + revision);
  }

  public void testDisconnectAndSyncAddition() throws Exception
  {
    TestListener listener = new TestListener();
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession(clone.getName() + "_master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource("/master/resource");

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
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(company);
    transaction.commit();

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

      CDOSession masterSession = openSession(clone.getName() + "_master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource("/master/resource");

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
    CDOResource resource = transaction.getResource("/master/resource");

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

      CDOSession masterSession = openSession(clone.getName() + "_master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource("/master/resource");

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
    CDOResource resource = transaction.getResource("/master/resource");

    assertEquals(0, resource.getContents().size());
  }

  public void testDisconnectAndCommit() throws Exception
  {
    CloneRepository clone = (CloneRepository)getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(company);
    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(true, commitInfo.getBranch().isLocal());
    assertEquals(true, transaction.getBranch().isLocal());
  }

  public void testDisconnectAndCommitAndMerge() throws Exception
  {
    CloneRepository clone = (CloneRepository)getRepository();
    waitForOnline(clone);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    resource.getContents().add(getModel1Factory().createCompany());
    CDOCommitInfo commitInfo = transaction.commit();

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    transaction.setBranch(session.getBranchManager().getMainBranch());
    transaction.merge(commitInfo, new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();
  }

  public void testDisconnectAndCommitAndMergeWithNewPackages() throws Exception
  {
    CloneRepository clone = (CloneRepository)getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    resource.getContents().add(company);
    CDOCommitInfo commitInfo = transaction.commit();

    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    transaction.setBranch(session.getBranchManager().getMainBranch());
    CDOChangeSetData result = transaction.merge(commitInfo, new DefaultCDOMerger.PerFeature.ManyValued());

    transaction.commit();
  }

  public void testSqueezedCommitInfos_Initial() throws Exception
  {
    CloneRepository clone = (CloneRepository)getRepository();
    waitForOnline(clone);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    CDOSession masterSession = openSession(clone.getName() + "_master");
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

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

    final List<CDOCommitInfo> result = new ArrayList<CDOCommitInfo>();
    CDOSession session = openSession();
    session.getCommitInfoManager().getCommitInfos(null, new CDOCommitInfoHandler()
    {
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

    if (isSqueezedCommitInfos())
    {
      assertEquals(2, result.size());
    }
    else
    {
      assertEquals(12, result.size());
    }
  }

  protected static void waitForOnline(CDOCommonRepository repository)
  {
    while (repository.getState() != CDOCommonRepository.State.ONLINE)
    {
      System.out.println("Waiting for ONLINE...");
      sleep(SLEEP_MILLIS);
    }
  }

  protected static void waitForOffline(CDOCommonRepository repository)
  {
    while (repository.getState() == CDOCommonRepository.State.ONLINE)
    {
      System.out.println("Waiting for OFFLINE...");
      sleep(SLEEP_MILLIS);
    }
  }

  protected static CDOCommitInfo commitAndWaitForArrival(CDOTransaction transaction, CDOSession receiver)
  {
    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    while (receiver.getLastUpdateTime() < timeStamp)
    {
      System.out.println("Waiting for arrival of commit " + CDOCommonUtil.formatTimeStamp(timeStamp));
      sleep(SLEEP_MILLIS);
    }

    return commitInfo;
  }

  protected static CDOCommitInfo commitAndWaitForArrival(CDOTransaction transaction, IRepository receiver)
  {
    CDOCommitInfo commitInfo = transaction.commit();
    long timeStamp = commitInfo.getTimeStamp();
    while (receiver.getLastCommitTimeStamp() < timeStamp)
    {
      System.out.println("Waiting for arrival of commit " + CDOCommonUtil.formatTimeStamp(timeStamp));
      sleep(SLEEP_MILLIS);
    }

    return commitInfo;
  }
}
