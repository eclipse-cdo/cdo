/*
 * Copyright (c) 2010-2013, 2016, 2018, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.mem.IMEMStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.TestListener;

import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class AbstractSyncingTest extends AbstractCDOTest
{
  protected static final long SLEEP_MILLIS = 100;

  private static final boolean VERBOSE_WAIT = false;

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

  @Override
  public InternalSynchronizableRepository getRepository()
  {
    return (InternalSynchronizableRepository)super.getRepository();
  }

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(OfflineConfig.PROP_TEST_RAW_REPLICATION, isRawReplication());
    testProperties.put(OfflineConfig.PROP_TEST_DELAYED_COMMIT_HANDLING, getTestDelayedCommitHandling());
    testProperties.put(OfflineConfig.PROP_TEST_DELAYED2_COMMIT_HANDLING, getTestDelayed2CommitHandling());
    testProperties.put(OfflineConfig.PROP_TEST_FAILOVER, isFailover());
    testProperties.put(OfflineConfig.PROP_TEST_HINDER_INITIAL_REPLICATION, isHinderInitialReplication());
    return testProperties;
  }

  protected boolean isFailover()
  {
    return false;
  }

  protected boolean isRawReplication()
  {
    return true;
  }

  protected long getTestDelayedCommitHandling()
  {
    return 0L;
  }

  protected long getTestDelayed2CommitHandling()
  {
    return 0L;
  }

  protected boolean isHinderInitialReplication()
  {
    return false;
  }

  protected static void checkEvent(final TestListener listener, final int newPackageUnits, final int newObjects, final int changedObjects,
      final int detachedObjects) throws InterruptedException
  {
    try
    {
      final int[] counts = { 0, 0, 0, 0 };

      assertNoTimeout(() -> {
        IEvent[] events = listener.getEvents();
        for (IEvent event : events)
        {
          if (event instanceof CDOSessionInvalidationEvent)
          {
            CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
            counts[0] += e.getNewPackageUnits().size();
            counts[1] += e.getNewObjects().size();
            counts[2] += e.getChangedObjects().size();
            counts[3] += e.getDetachedObjects().size();
          }
        }

        return counts[0] >= newPackageUnits && counts[1] >= newObjects && counts[2] >= changedObjects && counts[3] >= detachedObjects;
      });

      assertEquals(newPackageUnits, counts[0]);
      assertEquals(newObjects, counts[1]);
      assertEquals(changedObjects, counts[2]);
      assertEquals(detachedObjects, counts[3]);
    }
    finally
    {
      listener.clearEvents();
    }
  }

  protected static void checkRevision(EObject object, InternalRepository repository, String location)
  {
    CDORevision revision = CDOUtil.getCDOObject(object).cdoRevision();
    CDOBranch branch = repository.getBranchManager().getBranch(revision.getBranch().getID());

    // Check if revision arrived in cache
    checkRevision(revision, branch, repository.getRevisionManager().getCache().getAllRevisions(), location + " cache");

    // Check if revision arrived in store
    InternalStore store = repository.getStore();
    if (store instanceof IMEMStore)
    {
      checkRevision(revision, branch, ((IMEMStore)store).getAllRevisions(), location + " store");
    }
  }

  protected static void checkRevision(CDORevision revision, CDOBranch branch, Map<CDOBranch, List<CDORevision>> allRevisions, String location)
  {
    List<CDORevision> revisions = allRevisions.get(branch);
    for (CDORevision rev : revisions)
    {
      if (rev.getID() == revision.getID() && rev.getBranch().getID() == revision.getBranch().getID() && rev.getVersion() == revision.getVersion())
      {
        return;
      }
    }

    fail("Revision missing from " + location + ": " + revision);
  }

  protected static void checkRevisions(InternalRepository repository, long timeStamp, int expectedRevisions) throws InterruptedException
  {
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

  protected static void waitForOnline(CDOCommonRepository repository)
  {
    String what = repository.getName() + " becoming online";

    while (repository.getState() != CDOCommonRepository.State.ONLINE)
    {
      waitFor(what);
    }

    if (VERBOSE_WAIT)
    {
      IOUtil.OUT().println(repository.getName() + " is online.");
    }
  }

  protected static void waitForOffline(CDOCommonRepository repository)
  {
    String what = repository.getName() + " becoming offline";
    while (repository.getState() == CDOCommonRepository.State.ONLINE)
    {
      waitFor(what);
    }

    if (VERBOSE_WAIT)
    {
      IOUtil.OUT().println(repository.getName() + " is offline.");
    }
  }

  protected static CDOCommitInfo commitAndWaitForArrival(CDOTransaction transaction, CDOSession receiver)
  {
    try
    {
      CDOCommitInfo commitInfo = transaction.commit();
      long timeStamp = commitInfo.getTimeStamp();
      while (receiver.getLastUpdateTime() < timeStamp)
      {
        waitFor("arrival of commit " + CDOCommonUtil.formatTimeStamp(timeStamp));
      }

      return commitInfo;
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  protected static CDOCommitInfo commitAndWaitForArrival(CDOTransaction transaction, IRepository receiver)
  {
    try
    {
      CDOCommitInfo commitInfo = transaction.commit();
      long timeStamp = commitInfo.getTimeStamp();
      while (receiver.getLastCommitTimeStamp() < timeStamp)
      {
        waitFor("arrival of commit " + CDOCommonUtil.formatTimeStamp(timeStamp));
      }

      return commitInfo;
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private static void waitFor(String what)
  {
    if (VERBOSE_WAIT)
    {
      IOUtil.OUT().println("Waiting for " + what + "...");
      sleep(SLEEP_MILLIS);
    }
    else
    {
      ConcurrencyUtil.sleep(SLEEP_MILLIS);
    }
  }

  protected static CDOTransaction openTransaction(CDOSession session)
  {
    CDOTransaction tx = session.openTransaction();
    tx.options().setLockNotificationEnabled(true);
    tx.enableDurableLocking();
    return tx;
  }
}
