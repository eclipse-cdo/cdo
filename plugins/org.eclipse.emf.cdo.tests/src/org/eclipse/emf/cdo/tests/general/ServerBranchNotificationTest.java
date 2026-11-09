/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranch.BranchDeletedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList.TagMovedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList.TagRenamedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.ServerOperationContext;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier.Cancelation;
import org.eclipse.net4j.util.om.monitor.Monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Verifies server branch and tag notifications for independent sessions.
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class ServerBranchNotificationTest extends AbstractCDOTest
{
  private final AtomicInteger barrierCounter = new AtomicInteger();

  public void testSentinelBarrierRecordingIsAtomic() throws Exception
  {
    CDOSession session = openSession();
    EventRecorder recorder = new EventRecorder(session);

    try
    {
      recorder.recordBranchEvent(101, ChangeKind.CREATED, null);
      recorder.armBarrier(101);
      assertTrue("Sentinel recorded before arming was not recognized", recorder.awaitBarrier());
      assertEquals(0, recorder.size());

      recorder.armBarrier(102);
      recorder.recordBranchEvent(102, ChangeKind.CREATED, null);
      assertTrue("Sentinel recorded after arming was not recognized", recorder.awaitBarrier());
      assertEquals(0, recorder.size());

      CountDownLatch ready = new CountDownLatch(2);
      CountDownLatch start = new CountDownLatch(1);
      CountDownLatch finished = new CountDownLatch(2);
      Thread ordinaryEvent = new Thread(
          () -> runRecorderAction(recorder, ready, start, finished, () -> recorder.recordBranchEvent(201, ChangeKind.CREATED, null)));
      Thread armBarrier = new Thread(() -> runRecorderAction(recorder, ready, start, finished, () -> recorder.armBarrier(202)));
      ordinaryEvent.setDaemon(true);
      armBarrier.setDaemon(true);
      ordinaryEvent.start();
      armBarrier.start();
      assertTrue("Recorder workers did not reach the start gate", ready.await(15, TimeUnit.SECONDS));
      start.countDown();
      assertTrue("Concurrent recorder operations did not complete", finished.await(15, TimeUnit.SECONDS));
      assertEquals(Arrays.asList("CREATED:201"), recorder.snapshot());

      recorder.recordBranchEvent(203, ChangeKind.RENAMED, "ordinary-renamed");
      recorder.recordBranchEvent(204, ChangeKind.DELETED, null);
      recorder.recordBranchEvent(202, ChangeKind.CREATED, null);
      assertTrue("Sentinel recorded after concurrent arming was not recognized", recorder.awaitBarrier());
      assertEquals(Arrays.asList("CREATED:201", "RENAMED:203:ordinary-renamed", "DELETED:204"), recorder.snapshot());
    }
    finally
    {
      session.close();
    }
  }

  private void runRecorderAction(EventRecorder recorder, CountDownLatch ready, CountDownLatch start, CountDownLatch finished, Runnable action)
  {
    ready.countDown();
    try
    {
      start.await();
      action.run();
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
    finally
    {
      finished.countDown();
    }
  }

  private void awaitTestGate(CountDownLatch gate)
  {
    try
    {
      if (!gate.await(15, TimeUnit.SECONDS))
      {
        throw new AssertionError("Timed out waiting for the local event test gate");
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }

  private void awaitBlocked(Thread thread)
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
    while (thread.isAlive() && thread.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    assertEquals("Concurrent mutation did not reach the tag event serialization monitor", Thread.State.BLOCKED, thread.getState());
  }

  public void testBranchNotifications() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient1 = openSession();
    CDOSession recipient2 = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipient1Events = new EventRecorder(recipient1);
    EventRecorder recipient2Events = new EventRecorder(recipient2);
    sender.getBranchManager().addListener(senderEvents);
    recipient1.getBranchManager().addListener(recipient1Events);
    recipient2.getBranchManager().addListener(recipient2Events);

    try
    {
      CDOBranch protocolBranch = sender.getBranchManager().getMainBranch().createBranch(getBranchName("protocol"));
      int protocolID = protocolBranch.getID();
      synchronize(sender, senderEvents, recipient1Events, recipient2Events);
      assertBranchEvents(Arrays.asList("CREATED:" + protocolID), recipient1Events.snapshot());
      assertBranchEvents(recipient1Events.snapshot(), recipient2Events.snapshot());
      assertBranchEvents(Arrays.asList("CREATED:" + protocolID), senderEvents.snapshot());
      senderEvents.clear();
      protocolBranch.setName(getBranchName("protocol-renamed"));
      synchronize(sender, senderEvents, recipient1Events, recipient2Events);
      assertBranchEvents(Arrays.asList("CREATED:" + protocolID, "RENAMED:" + protocolID + ":" + getBranchName("protocol-renamed")),
          recipient1Events.snapshot());
      assertBranchEvents(recipient1Events.snapshot(), recipient2Events.snapshot());
      assertBranchEvents(Arrays.asList("RENAMED:" + protocolID + ":" + getBranchName("protocol-renamed")), senderEvents.snapshot());
      senderEvents.clear();
      protocolBranch.delete(null);
      synchronize(sender, senderEvents, recipient1Events, recipient2Events);
      List<String> protocolSequence = Arrays.asList("CREATED:" + protocolID, "RENAMED:" + protocolID + ":" + getBranchName("protocol-renamed"),
          "DELETED:" + protocolID);
      assertBranchEvents(protocolSequence, recipient1Events.snapshot());
      assertBranchEvents(protocolSequence, recipient2Events.snapshot());
      assertEquals(0, senderEvents.size());
      senderEvents.clear();
      recipient1Events.addExpected(protocolSequence);
      recipient2Events.addExpected(protocolSequence);

      int senderBaseline = senderEvents.size();
      List<String> attributed = runAttributed(sender, () -> createBranches("attributed"));
      synchronize(sender, senderEvents, recipient1Events, recipient2Events);
      assertEquals(senderBaseline, senderEvents.size());
      recipient1Events.addExpected(attributed);
      recipient2Events.addExpected(attributed);
      assertBranchEvents(recipient1Events.expected(), recipient1Events.snapshot());
      assertBranchEvents(recipient2Events.expected(), recipient2Events.snapshot());

      int senderBeforeUnattributed = senderEvents.size();
      List<String> unattributed = runUnattributed(() -> createBranches("unattributed"));
      synchronize(sender, senderEvents, recipient1Events, recipient2Events);
      senderEvents.addExpected(unattributed);
      recipient1Events.addExpected(unattributed);
      recipient2Events.addExpected(unattributed);
      assertBranchEvents(unattributed, senderEvents.snapshot().subList(senderBeforeUnattributed, senderEvents.size()));
      assertBranchEvents(recipient1Events.expected(), recipient1Events.snapshot());
      assertBranchEvents(recipient2Events.expected(), recipient2Events.snapshot());
    }
    finally
    {
      sender.close();
      recipient1.close();
      recipient2.close();
    }
  }

  public void testRemoteBranchRenameNotificationIsDeliveredExactlyOnce() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);

    try
    {
      CDOBranch senderBranch = sender.getBranchManager().getMainBranch().createBranch(getBranchName("remote-rename-once"));
      int branchID = senderBranch.getID();
      synchronize(sender, senderEvents, recipientEvents);
      senderEvents.clear();
      recipientEvents.clear();

      CDOBranch recipientBranch = recipient.getBranchManager().getBranch(branchID);
      assertNotNull(recipientBranch);
      CDOBranch serverBranch = getRepository().getBranchManager().getBranch(branchID);
      String newName = getBranchName("remote-rename-once-new");
      CountDownLatch renameReceived = new CountDownLatch(1);
      AtomicInteger renameEventCount = new AtomicInteger();
      AtomicReference<CDOBranchChangedEvent> receivedEvent = new AtomicReference<>();
      IListener renameListener = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
          if (branchEvent.getChangeKind() == ChangeKind.RENAMED && branchEvent.getBranch().getID() == branchID)
          {
            renameEventCount.incrementAndGet();
            receivedEvent.set(branchEvent);
            renameReceived.countDown();
          }
        }
      };
      recipient.getBranchManager().addListener(renameListener);

      try
      {
        runAttributed(sender, () -> {
          serverBranch.setName(newName);
          return null;
        });

        assertTrue("Remote rename notification was not delivered", renameReceived.await(15, TimeUnit.SECONDS));
      }
      finally
      {
        recipient.getBranchManager().removeListener(renameListener);
      }

      assertEquals("Remote rename notification was not delivered exactly once", 1, renameEventCount.get());
      assertSame("Rename event did not use the recipient's canonical branch", recipientBranch, receivedEvent.get().getBranch());
      assertEquals(branchID, receivedEvent.get().getBranch().getID());
      assertEquals(newName, recipientBranch.getName());
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + newName), recipientEvents.snapshot());
      assertEquals("Sender received its own branch notification", 0, senderEvents.size());

      CDOBranch subsequentBranch = recipient.getBranchManager().getMainBranch().createBranch(getBranchName("after-remote-rename"));
      assertNotNull(subsequentBranch);
      assertNotNull(getRepository().getBranchManager().getBranch(subsequentBranch.getID()));
    }
    finally
    {
      sender.getBranchManager().removeListener(senderEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      sender.close();
      recipient.close();
    }
  }

  public void testReentrantBranchRenameNotificationsPreserveCapturedNames() throws Exception
  {
    CDOSession senderA = openSession();
    CDOSession senderB = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderAEvents = new EventRecorder(senderA);
    EventRecorder senderBEvents = new EventRecorder(senderB);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    senderA.getBranchManager().addListener(senderAEvents);
    senderB.getBranchManager().addListener(senderBEvents);
    recipient.getBranchManager().addListener(recipientEvents);

    try
    {
      CDOBranch clientBranch = senderA.getBranchManager().getMainBranch().createBranch(getBranchName("reentrant-rename"));
      int branchID = clientBranch.getID();
      synchronize(senderA, senderAEvents, senderBEvents, recipientEvents);
      senderAEvents.clear();
      senderBEvents.clear();
      recipientEvents.clear();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOBranch serverBranch = serverManager.getBranch(branchID);
      String firstName = getBranchName("reentrant-first");
      String secondName = getBranchName("reentrant-second");
      AtomicBoolean reentered = new AtomicBoolean();
      IListener listener = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
          if (branchEvent.getBranch().getID() == branchID && branchEvent.getChangeKind() == ChangeKind.RENAMED && reentered.compareAndSet(false, true))
          {
            runAttributed(senderB, () -> {
              serverBranch.setName(secondName);
              return null;
            });
          }
        }
      };
      serverManager.addListener(listener);
      try
      {
        runAttributed(senderA, () -> {
          serverBranch.setName(firstName);
          return null;
        });
      }
      finally
      {
        serverManager.removeListener(listener);
      }

      synchronize(senderA, senderAEvents, senderBEvents, recipientEvents);
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + secondName), senderAEvents.snapshot());
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + firstName), senderBEvents.snapshot());
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + firstName, "RENAMED:" + branchID + ":" + secondName), recipientEvents.snapshot());
      assertEquals(secondName, serverBranch.getName());
    }
    finally
    {
      senderA.getBranchManager().removeListener(senderAEvents);
      senderB.getBranchManager().removeListener(senderBEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      senderA.close();
      senderB.close();
      recipient.close();
    }
  }

  public void testConcurrentBranchRenameNotificationsRemainOrdered() throws Exception
  {
    CDOSession senderA = openSession();
    CDOSession senderB = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderAEvents = new EventRecorder(senderA);
    EventRecorder senderBEvents = new EventRecorder(senderB);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    senderA.getBranchManager().addListener(senderAEvents);
    senderB.getBranchManager().addListener(senderBEvents);
    recipient.getBranchManager().addListener(recipientEvents);

    try
    {
      CDOBranch clientBranch = senderA.getBranchManager().getMainBranch().createBranch(getBranchName("concurrent-rename"));
      int branchID = clientBranch.getID();
      synchronize(senderA, senderAEvents, senderBEvents, recipientEvents);
      senderAEvents.clear();
      senderBEvents.clear();
      recipientEvents.clear();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOBranch serverBranch = serverManager.getBranch(branchID);
      String firstName = getBranchName("concurrent-first");
      String secondName = getBranchName("concurrent-second");
      CountDownLatch firstRenameEntered = new CountDownLatch(1);
      CountDownLatch releaseFirstRename = new CountDownLatch(1);
      CountDownLatch secondRenameFinished = new CountDownLatch(1);
      CountDownLatch firstRenameFinished = new CountDownLatch(1);
      AtomicInteger renameEvents = new AtomicInteger();
      AtomicReference<Throwable> failure = new AtomicReference<>();
      IListener listener = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
          if (branchEvent.getBranch().getID() == branchID && branchEvent.getChangeKind() == ChangeKind.RENAMED && renameEvents.getAndIncrement() == 0)
          {
            firstRenameEntered.countDown();
            try
            {
              if (!releaseFirstRename.await(15, TimeUnit.SECONDS))
              {
                throw new AssertionError("First rename was not released");
              }
            }
            catch (InterruptedException ex)
            {
              Thread.currentThread().interrupt();
              throw new AssertionError(ex);
            }
          }
        }
      };
      serverManager.addListener(listener);

      Thread firstRename = new Thread(() -> {
        try
        {
          runAttributed(senderA, () -> {
            serverBranch.setName(firstName);
            return null;
          });
        }
        catch (Throwable ex)
        {
          failure.compareAndSet(null, ex);
        }
        finally
        {
          firstRenameFinished.countDown();
        }
      }, "FirstBranchRename"); //$NON-NLS-1$
      firstRename.setDaemon(true);

      Thread secondRename = new Thread(() -> {
        try
        {
          if (!firstRenameEntered.await(15, TimeUnit.SECONDS))
          {
            throw new AssertionError("First rename did not reach the synchronization point");
          }

          runAttributed(senderB, () -> {
            serverBranch.setName(secondName);
            return null;
          });
        }
        catch (Throwable ex)
        {
          failure.compareAndSet(null, ex);
        }
        finally
        {
          secondRenameFinished.countDown();
        }
      }, "SecondBranchRename"); //$NON-NLS-1$
      secondRename.setDaemon(true);

      try
      {
        firstRename.start();
        secondRename.start();
        assertTrue("Second rename did not complete while the first listener was held", secondRenameFinished.await(15, TimeUnit.SECONDS));
      }
      finally
      {
        releaseFirstRename.countDown();
        serverManager.removeListener(listener);
      }

      assertTrue("First rename did not complete", firstRenameFinished.await(15, TimeUnit.SECONDS));
      if (failure.get() != null)
      {
        throw new AssertionError("Concurrent branch rename failed", failure.get());
      }

      synchronize(senderA, senderAEvents, senderBEvents, recipientEvents);
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + secondName), senderAEvents.snapshot());
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + firstName), senderBEvents.snapshot());
      assertEquals(Arrays.asList("RENAMED:" + branchID + ":" + firstName, "RENAMED:" + branchID + ":" + secondName), recipientEvents.snapshot());
      assertEquals(secondName, serverBranch.getName());
    }
    finally
    {
      senderA.getBranchManager().removeListener(senderAEvents);
      senderB.getBranchManager().removeListener(senderBEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      senderA.close();
      senderB.close();
      recipient.close();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testSubtreeDeletionPublishesEachLocalEventOnce() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);

    try
    {
      CDOTransaction transaction = sender.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("delete-branch-tag"));
      Category category = getModel1Factory().createCategory();
      category.setName("delete branch tag");
      resource.getContents().add(category);
      long timeStamp = transaction.commit().getTimeStamp();
      transaction.close();

      CDOBranch parent = sender.getBranchManager().getMainBranch().createBranch(getBranchName("delete-parent"));
      CDOBranch child = parent.createBranch(getBranchName("delete-child"));
      synchronize(sender, senderEvents, recipientEvents);
      recipientEvents.clear();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOBranch serverParent = serverManager.getBranch(parent.getID());
      CDOBranch serverChild = serverManager.getBranch(child.getID());
      CDOBranch recipientParent = recipient.getBranchManager().getBranch(parent.getID());
      CDOBranch recipientChild = recipient.getBranchManager().getBranch(child.getID());
      String tagName = getBranchName("delete-tag");
      runAttributed(sender, () -> serverManager.createTag(tagName, serverChild.getPoint(timeStamp)));
      assertNotNull(runAttributed(sender, () -> serverManager.getTag(tagName)));

      DeletionRecorder branchEvents = new DeletionRecorder();
      DeletionRecorder managerEvents = new DeletionRecorder();
      DeletionRecorder recipientBranchEvents = new DeletionRecorder();
      AtomicReference<CDOBranch> reentrantBranch = new AtomicReference<>();
      AtomicBoolean reentered = new AtomicBoolean();
      IListener reentrantListener = event -> {
        if (event instanceof BranchDeletedEvent && reentered.compareAndSet(false, true))
        {
          reentrantBranch.set(serverManager.getMainBranch().createBranch(getBranchName("created-during-deletion")));
        }
      };
      serverParent.addListener(branchEvents);
      serverChild.addListener(branchEvents);
      serverParent.addListener(reentrantListener);
      serverChild.addListener(reentrantListener);
      serverManager.addListener(managerEvents);
      recipientParent.addListener(recipientBranchEvents);
      recipientChild.addListener(recipientBranchEvents);

      try
      {
        CDOBranch[] deletedBranches = runAttributed(sender, () -> serverParent.delete(new Monitor()));
        synchronize(sender, senderEvents, recipientEvents);
        assertEquals(2, deletedBranches.length);
        assertDeletedBranches(Arrays.asList(parent.getID(), child.getID()), branchEvents.branchIDs());
        assertDeletedBranches(Arrays.asList(parent.getID(), child.getID()), recipientBranchEvents.branchIDs());
        assertEquals(1, managerEvents.managerEvents().size());
        assertDeletedBranches(Arrays.asList(parent.getID(), child.getID()), managerEvents.managerEvents().get(0));
        assertNotNull(reentrantBranch.get());
        assertBranchEvents(Arrays.asList("DELETED:" + child.getID(), "CREATED:" + reentrantBranch.get().getID()), recipientEvents.snapshot());
        assertNull(runAttributed(sender, () -> serverManager.getTag(tagName)));
      }
      finally
      {
        serverParent.removeListener(branchEvents);
        serverChild.removeListener(branchEvents);
        serverParent.removeListener(reentrantListener);
        serverChild.removeListener(reentrantListener);
        serverManager.removeListener(managerEvents);
        recipientParent.removeListener(recipientBranchEvents);
        recipientChild.removeListener(recipientBranchEvents);
      }
    }
    finally
    {
      sender.close();
      recipient.close();
    }
  }

  private void assertDeletedBranches(List<Integer> expected, List<Integer> actual)
  {
    assertEquals(expected.size(), actual.size());
    assertTrue(actual.containsAll(expected));
    assertTrue(expected.containsAll(actual));
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testTagNotifications() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient1 = openSession();
    CDOSession recipient2 = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder senderBranchEvents = new EventRecorder(sender);
    EventRecorder recipient1Events = new EventRecorder(recipient1);
    EventRecorder recipient2Events = new EventRecorder(recipient2);
    sender.getBranchManager().addListener(senderBranchEvents);
    recipient1.getBranchManager().addListener(recipient1Events);
    recipient2.getBranchManager().addListener(recipient2Events);
    CDOTransaction transaction = sender.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("tag-notification"));
    Category category = getModel1Factory().createCategory();
    category.setName("tag notification");
    resource.getContents().add(category);
    long timeStamp = transaction.commit().getTimeStamp();
    transaction.close();

    InternalCDOBranchManager senderManager = (InternalCDOBranchManager)sender.getBranchManager();
    CDOBranch main = senderManager.getMainBranch();
    CDOBranch target = main.createBranch(getBranchName("tag-target"), timeStamp);
    synchronize(sender, senderBranchEvents, recipient1Events, recipient2Events);
    senderEvents.clear();
    recipient1Events.clear();
    recipient2Events.clear();
    CDOBranchPoint mainPoint = main.getPoint(timeStamp);
    CDOBranchPoint targetPoint = target.getPoint(timeStamp);
    CDOTagList senderTags = senderManager.getTagList();
    CDOTagList recipient1Tags = recipient1.getBranchManager().getTagList();
    CDOTagList recipient2Tags = recipient2.getBranchManager().getTagList();
    senderTags.addListener(senderEvents);
    recipient1Tags.addListener(recipient1Events);
    recipient2Tags.addListener(recipient2Events);

    try
    {
      int senderModCount = senderManager.getTagModCount();
      int recipientModCount = getRepository().getBranchManager().getTagModCount();
      createTags(senderManager, "protocol", mainPoint, targetPoint);
      synchronize(sender, senderBranchEvents, recipient1Events, recipient2Events);
      List<String> protocolSequence = tagSequence("protocol", mainPoint, targetPoint, recipientModCount + 1);
      assertEquals(protocolSequence, recipient1Events.snapshot());
      assertEquals(protocolSequence, recipient2Events.snapshot());
      assertEquals(tagSequence("protocol", mainPoint, targetPoint, senderModCount + 1), senderEvents.snapshot());
      recipient1Events.addExpected(protocolSequence);
      recipient2Events.addExpected(protocolSequence);

      int senderBaseline = senderEvents.size();
      int attributedFirstModCount = getRepository().getBranchManager().getTagModCount() + 1;
      List<String> attributed = runAttributed(sender, () -> createServerTags("attributed", timeStamp, attributedFirstModCount));
      synchronize(sender, senderBranchEvents, recipient1Events, recipient2Events);
      assertEquals(senderBaseline, senderEvents.size());
      recipient1Events.addExpected(attributed);
      recipient2Events.addExpected(attributed);
      assertEquals(recipient1Events.expected(), recipient1Events.snapshot());
      assertEquals(recipient2Events.expected(), recipient2Events.snapshot());

      senderTags.removeListener(senderEvents);
      sender.getBranchManager().removeListener(senderBranchEvents);
      sender.close();
      sender = openSession();
      senderEvents = new EventRecorder(sender);
      senderBranchEvents = new EventRecorder(sender);
      sender.getBranchManager().addListener(senderBranchEvents);
      senderTags = sender.getBranchManager().getTagList();
      senderTags.addListener(senderEvents);

      int senderBeforeUnattributed = senderEvents.size();
      int unattributedFirstModCount = getRepository().getBranchManager().getTagModCount() + 1;
      List<String> unattributed = runUnattributed(() -> createServerTags("unattributed", timeStamp, unattributedFirstModCount));
      synchronize(sender, senderBranchEvents, recipient1Events, recipient2Events);
      senderEvents.addExpected(unattributed);
      recipient1Events.addExpected(unattributed);
      recipient2Events.addExpected(unattributed);
      assertEquals(unattributed, senderEvents.snapshot().subList(senderBeforeUnattributed, senderEvents.size()));
      assertEquals(recipient1Events.expected(), recipient1Events.snapshot());
      assertEquals(recipient2Events.expected(), recipient2Events.snapshot());
    }
    finally
    {
      senderTags.removeListener(senderEvents);
      sender.getBranchManager().removeListener(senderBranchEvents);
      recipient1.getBranchManager().removeListener(recipient1Events);
      recipient2.getBranchManager().removeListener(recipient2Events);
      recipient1Tags.removeListener(recipient1Events);
      recipient2Tags.removeListener(recipient2Events);
      sender.close();
      recipient1.close();
      recipient2.close();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testConcurrentTagNotificationsRemainOrdered() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);
    sender.getBranchManager().getTagList().addListener(senderEvents);
    recipient.getBranchManager().getTagList().addListener(recipientEvents);

    try
    {
      InternalCDOBranchManager branchManager = getRepository().getBranchManager();
      int firstModCount = getRepository().getBranchManager().getTagModCount() + 1;
      CDOTransaction transaction = sender.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("concurrent-tag-notifications"));
      Category category = getModel1Factory().createCategory();
      category.setName("concurrent tag notifications");
      resource.getContents().add(category);
      long timeStamp = transaction.commit().getTimeStamp();
      transaction.close();
      CDOBranchPoint branchPoint = branchManager.getMainBranch().getPoint(timeStamp);
      CountDownLatch ready = new CountDownLatch(2);
      CountDownLatch start = new CountDownLatch(1);
      CountDownLatch finished = new CountDownLatch(2);
      AtomicReference<Throwable> failure = new AtomicReference<>();
      Thread first = createConcurrentTagThread(branchManager, branchPoint, "concurrent-tag-a", ready, start, finished, failure);
      Thread second = createConcurrentTagThread(branchManager, branchPoint, "concurrent-tag-b", ready, start, finished, failure);
      first.setDaemon(true);
      second.setDaemon(true);
      first.start();
      second.start();
      assertTrue("Tag workers did not reach the start barrier", ready.await(15, TimeUnit.SECONDS));
      start.countDown();
      assertTrue("Concurrent tag changes did not complete", finished.await(15, TimeUnit.SECONDS));
      if (failure.get() != null)
      {
        throw new AssertionError("Concurrent tag change failed", failure.get());
      }

      synchronize(sender, senderEvents, recipientEvents);
      List<String> events = recipientEvents.snapshot();
      assertEquals(2, events.size());
      String firstTagName = assertTagCreation(events.get(0), firstModCount, branchPoint);
      String secondTagName = assertTagCreation(events.get(1), firstModCount + 1, branchPoint);
      assertFalse(firstTagName.equals(secondTagName));
      assertTrue(Arrays.asList("concurrent-tag-a", "concurrent-tag-b").contains(firstTagName));
      assertTrue(Arrays.asList("concurrent-tag-a", "concurrent-tag-b").contains(secondTagName));
      assertEquals(events, senderEvents.snapshot());
    }
    finally
    {
      sender.close();
      recipient.close();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testReentrantBranchCreationFromTagListenerPreservesPublicationOrder() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);
    sender.getBranchManager().getTagList().addListener(senderEvents);
    recipient.getBranchManager().getTagList().addListener(recipientEvents);

    try
    {
      CDOTransaction transaction = sender.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("reentrant-tag-branch"));
      Category category = getModel1Factory().createCategory();
      category.setName("reentrant tag branch");
      resource.getContents().add(category);
      long timeStamp = transaction.commit().getTimeStamp();
      transaction.close();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOTagList serverTags = serverManager.getTagList();
      String tagName = getBranchName("reentrant-branch-tag");
      String branchName = getBranchName("branch-from-tag-listener");
      int tagModCount = serverManager.getTagModCount() + 1;
      AtomicReference<CDOBranch> nestedBranch = new AtomicReference<>();
      AtomicBoolean reentered = new AtomicBoolean();
      AtomicInteger localBranchEvents = new AtomicInteger();
      IListener branchListener = event -> {
        if (event instanceof CDOBranchChangedEvent && ((CDOBranchChangedEvent)event).getChangeKind() == ChangeKind.CREATED
            && ((CDOBranchChangedEvent)event).getBranch().getName().equals(branchName))
        {
          localBranchEvents.incrementAndGet();
        }
      };
      IListener tagListener = event -> {
        if (event instanceof IContainerEvent<?> && event.getSource() == serverTags)
        {
          IContainerEvent<?> containerEvent = (IContainerEvent<?>)event;
          CDOBranchTag tag = (CDOBranchTag)containerEvent.getDeltaElement();
          if (containerEvent.getDeltaKind() == IContainerDelta.Kind.ADDED && tagName.equals(tag.getName()) && reentered.compareAndSet(false, true))
          {
            nestedBranch.set(serverManager.getMainBranch().createBranch(branchName));
          }
        }
      };
      getRepository().getBranchManager().addListener(branchListener);
      serverTags.addListener(tagListener);

      try
      {
        runAttributed(sender, () -> serverManager.createTag(tagName, serverManager.getMainBranch().getPoint(timeStamp)));
      }
      finally
      {
        serverTags.removeListener(tagListener);
        getRepository().getBranchManager().removeListener(branchListener);
      }

      assertNotNull(nestedBranch.get());
      synchronize(sender, senderEvents, recipientEvents);
      assertEquals(Arrays.asList("ADD:" + tagName + ":0@" + timeStamp + ":" + tagModCount, "CREATED:" + nestedBranch.get().getID()),
          recipientEvents.snapshot());
      assertEquals(0, senderEvents.size());
      assertEquals(1, localBranchEvents.get());
      assertNotNull(serverManager.getTag(tagName));
      assertNotNull(recipient.getBranchManager().getTag(tagName));
      assertNotNull(recipient.getBranchManager().getBranch(nestedBranch.get().getID()));
    }
    finally
    {
      sender.getBranchManager().removeListener(senderEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      sender.getBranchManager().getTagList().removeListener(senderEvents);
      recipient.getBranchManager().getTagList().removeListener(recipientEvents);
      sender.close();
      recipient.close();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testConcurrentTagMutationsPreserveLocalAndRemoteEventOrder() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);
    sender.getBranchManager().getTagList().addListener(senderEvents);
    recipient.getBranchManager().getTagList().addListener(recipientEvents);

    CountDownLatch localAddEntered = new CountDownLatch(1);
    CountDownLatch allowLocalAdd = new CountDownLatch(1);
    CountDownLatch renameStarted = new CountDownLatch(1);
    AtomicReference<Throwable> workerFailure = new AtomicReference<>();
    List<String> localEvents = new CopyOnWriteArrayList<>();

    try
    {
      CDOTransaction transaction = sender.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("concurrent-tag-event-order"));
      Category category = getModel1Factory().createCategory();
      category.setName("concurrent tag event order");
      resource.getContents().add(category);
      long timeStamp = transaction.commit().getTimeStamp();
      transaction.close();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOTagList serverTags = serverManager.getTagList();
      String oldName = getBranchName("concurrent-event-tag");
      String newName = oldName + "-renamed";
      CDOBranchPoint branchPoint = serverManager.getMainBranch().getPoint(timeStamp);
      IListener listener = event -> {
        if (event instanceof IContainerEvent<?> && event.getSource() == serverTags)
        {
          IContainerEvent<?> containerEvent = (IContainerEvent<?>)event;
          CDOBranchTag tag = (CDOBranchTag)containerEvent.getDeltaElement();
          if (containerEvent.getDeltaKind() == IContainerDelta.Kind.ADDED && oldName.equals(tag.getName()))
          {
            localEvents.add("ADD:" + tag.getName() + ":" + serverManager.getTagModCount());
            localAddEntered.countDown();
            awaitTestGate(allowLocalAdd);
          }
        }
        else if (event instanceof TagRenamedEvent)
        {
          TagRenamedEvent renamed = (TagRenamedEvent)event;
          if (oldName.equals(renamed.getOldName()))
          {
            localEvents.add("RENAME:" + renamed.getOldName() + ":" + renamed.getNewName() + ":" + serverManager.getTagModCount());
          }
        }
      };
      serverTags.addListener(listener);

      Thread createTag = new Thread(() -> {
        try
        {
          runAttributed(sender, () -> serverManager.createTag(oldName, branchPoint));
        }
        catch (Throwable ex)
        {
          workerFailure.set(ex);
        }
      }, "create-tag-for-order-test");
      createTag.setDaemon(true);
      createTag.start();
      assertTrue("Initial local tag event did not start", localAddEntered.await(15, TimeUnit.SECONDS));

      Thread renameTag = new Thread(() -> {
        renameStarted.countDown();
        try
        {
          runAttributed(sender, () -> {
            serverManager.renameTag(oldName, newName);
            return null;
          });
        }
        catch (Throwable ex)
        {
          workerFailure.set(ex);
        }
      }, "rename-tag-for-order-test");
      renameTag.setDaemon(true);
      renameTag.start();
      assertTrue("Rename worker did not start", renameStarted.await(15, TimeUnit.SECONDS));
      awaitBlocked(renameTag);

      allowLocalAdd.countDown();
      createTag.join(TimeUnit.SECONDS.toMillis(15));
      renameTag.join(TimeUnit.SECONDS.toMillis(15));
      assertFalse("Create worker did not complete", createTag.isAlive());
      assertFalse("Rename worker did not complete", renameTag.isAlive());
      assertNull(workerFailure.get());

      int firstModCount = serverManager.getTagModCount() - 1;
      assertEquals(Arrays.asList("ADD:" + oldName + ":" + firstModCount, "RENAME:" + oldName + ":" + newName + ":" + (firstModCount + 1)), localEvents);
      assertEquals(newName, runAttributed(sender, () -> serverManager.getTag(newName).getName()));
      synchronize(sender, senderEvents, recipientEvents);
      assertEquals(Arrays.asList("ADD:" + oldName + ":0@" + timeStamp + ":" + firstModCount, "RENAME:" + oldName + ":" + newName + ":" + (firstModCount + 1)),
          recipientEvents.snapshot());
      assertEquals(0, senderEvents.size());

      serverTags.removeListener(listener);
    }
    finally
    {
      allowLocalAdd.countDown();
      sender.getBranchManager().removeListener(senderEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      sender.getBranchManager().getTagList().removeListener(senderEvents);
      recipient.getBranchManager().getTagList().removeListener(recipientEvents);
      sender.close();
      recipient.close();
    }
  }

  public void testBranchNotificationIsPublishedWhenLocalListenerCancels() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    CountDownLatch recipientNotification = new CountDownLatch(1);
    AtomicReference<String> targetBranchName = new AtomicReference<>();
    IListener recipientWaiter = event -> {
      if (event instanceof CDOBranchChangedEvent)
      {
        CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
        if (branchEvent.getChangeKind() == ChangeKind.CREATED && branchEvent.getBranch().getName().equals(targetBranchName.get()))
        {
          recipientNotification.countDown();
        }
      }
    };
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);
    recipient.getBranchManager().addListener(recipientWaiter);

    try
    {
      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      String branchName = getBranchName("cancelled-local-branch-listener");
      targetBranchName.set(branchName);
      RuntimeException reason = new IllegalStateException("deliberate local listener cancellation");
      AtomicReference<CDOBranch> createdBranch = new AtomicReference<>();
      IListener cancellingListener = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
          if (branchEvent.getChangeKind() == ChangeKind.CREATED && branchName.equals(branchEvent.getBranch().getName()))
          {
            createdBranch.set(branchEvent.getBranch());
            throw new Cancelation(reason);
          }
        }
      };
      getRepository().getBranchManager().addListener(cancellingListener);

      RuntimeException propagated = null;
      try
      {
        runAttributed(sender, () -> serverManager.getMainBranch().createBranch(branchName));
      }
      catch (RuntimeException ex)
      {
        propagated = ex;
      }
      finally
      {
        getRepository().getBranchManager().removeListener(cancellingListener);
      }

      assertSame(reason, propagated);
      CDOBranch created = createdBranch.get();
      assertNotNull("Branch mutation did not complete before local cancellation", created);
      assertNotNull(serverManager.getBranch(created.getID()));
      assertTrue("Registered branch notification was not delivered", recipientNotification.await(15, TimeUnit.SECONDS));
      assertEquals(Arrays.asList("CREATED:" + created.getID()), recipientEvents.snapshot());
      assertEquals(0, senderEvents.size());

      CDOBranch subsequent = runAttributed(sender, () -> serverManager.getMainBranch().createBranch(getBranchName("after-cancel")));
      assertNotNull(subsequent);
    }
    finally
    {
      sender.getBranchManager().removeListener(senderEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      recipient.getBranchManager().removeListener(recipientWaiter);
      sender.close();
      recipient.close();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testReentrantTagRenamePreservesCountsAndPayloads() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);
    sender.getBranchManager().getTagList().addListener(senderEvents);
    recipient.getBranchManager().getTagList().addListener(recipientEvents);

    try
    {
      CDOTransaction transaction = sender.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("reentrant-tag-rename"));
      Category category = getModel1Factory().createCategory();
      category.setName("reentrant tag rename");
      resource.getContents().add(category);
      long timeStamp = transaction.commit().getTimeStamp();
      transaction.close();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOTagList serverTags = serverManager.getTagList();
      String oldName = getBranchName("reentrant-tag-old");
      String newName = getBranchName("reentrant-tag-new");
      CDOBranchPoint point = serverManager.getMainBranch().getPoint(timeStamp);
      int firstModCount = serverManager.getTagModCount() + 1;
      AtomicBoolean reentered = new AtomicBoolean();
      AtomicInteger localCreates = new AtomicInteger();
      AtomicInteger localRenames = new AtomicInteger();
      IListener tagListener = event -> {
        if (event instanceof IContainerEvent<?> && event.getSource() == serverTags)
        {
          IContainerEvent<?> containerEvent = (IContainerEvent<?>)event;
          CDOBranchTag tag = (CDOBranchTag)containerEvent.getDeltaElement();
          if (containerEvent.getDeltaKind() == IContainerDelta.Kind.ADDED && oldName.equals(tag.getName()))
          {
            localCreates.incrementAndGet();
            if (reentered.compareAndSet(false, true))
            {
              serverManager.renameTag(oldName, newName);
            }
          }
        }
        else if (event instanceof TagRenamedEvent)
        {
          TagRenamedEvent tagEvent = (TagRenamedEvent)event;
          if (oldName.equals(tagEvent.getOldName()) && newName.equals(tagEvent.getNewName()))
          {
            localRenames.incrementAndGet();
          }
        }
      };
      serverTags.addListener(tagListener);

      try
      {
        runAttributed(sender, () -> serverManager.createTag(oldName, point));
      }
      finally
      {
        serverTags.removeListener(tagListener);
      }

      synchronize(sender, senderEvents, recipientEvents);
      assertEquals(Arrays.asList("ADD:" + oldName + ":0@" + timeStamp + ":" + firstModCount, "RENAME:" + oldName + ":" + newName + ":" + (firstModCount + 1)),
          recipientEvents.snapshot());
      assertEquals(0, senderEvents.size());
      assertEquals(1, localCreates.get());
      assertEquals(1, localRenames.get());
      assertNull(runAttributed(sender, () -> serverManager.getTag(oldName)));
      assertNotNull(runAttributed(sender, () -> serverManager.getTag(newName)));
      assertNull(recipient.getBranchManager().getTag(oldName));
      CDOBranchTag recipientTag = recipient.getBranchManager().getTag(newName);
      assertNotNull(recipientTag);
      assertEquals(point.getBranch().getID(), recipientTag.getBranch().getID());
      assertEquals(timeStamp, recipientTag.getTimeStamp());
    }
    finally
    {
      sender.getBranchManager().removeListener(senderEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      sender.getBranchManager().getTagList().removeListener(senderEvents);
      recipient.getBranchManager().getTagList().removeListener(recipientEvents);
      sender.close();
      recipient.close();
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testReentrantTagDeletionAfterMovePreservesPublicationOrder() throws Exception
  {
    CDOSession sender = openSession();
    CDOSession recipient = openSession();
    EventRecorder senderEvents = new EventRecorder(sender);
    EventRecorder recipientEvents = new EventRecorder(recipient);
    sender.getBranchManager().addListener(senderEvents);
    recipient.getBranchManager().addListener(recipientEvents);
    sender.getBranchManager().getTagList().addListener(senderEvents);
    recipient.getBranchManager().getTagList().addListener(recipientEvents);

    try
    {
      CDOTransaction transaction = sender.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("reentrant-tag-move-delete"));
      Category category = getModel1Factory().createCategory();
      category.setName("reentrant tag move delete");
      resource.getContents().add(category);
      long timeStamp = transaction.commit().getTimeStamp();
      transaction.close();

      InternalCDOBranchManager serverManager = getRepository().getBranchManager();
      CDOTagList serverTags = serverManager.getTagList();
      CDOBranch target = runAttributed(sender, () -> serverManager.getMainBranch().createBranch(getBranchName("reentrant-move-target"), timeStamp));
      String tagName = getBranchName("reentrant-move-tag");
      CDOBranchPoint oldPoint = serverManager.getMainBranch().getPoint(timeStamp);
      CDOBranchPoint newPoint = target.getPoint(timeStamp);
      runAttributed(sender, () -> serverManager.createTag(tagName, oldPoint));
      synchronize(sender, senderEvents, recipientEvents);
      senderEvents.clear();
      recipientEvents.clear();

      int firstModCount = serverManager.getTagModCount() + 1;
      AtomicBoolean reentered = new AtomicBoolean();
      AtomicBoolean reenteredDelete = new AtomicBoolean();
      AtomicInteger localMoves = new AtomicInteger();
      AtomicInteger localRemovals = new AtomicInteger();
      AtomicInteger localAdds = new AtomicInteger();
      String nestedTagName = getBranchName("tag-created-from-delete-listener");
      CDOBranchTag serverTag = runAttributed(sender, () -> serverManager.getTag(tagName));
      IListener tagListener = event -> {
        if (event instanceof TagMovedEvent && ((TagMovedEvent)event).getTag().getName().equals(tagName))
        {
          localMoves.incrementAndGet();
          if (reentered.compareAndSet(false, true))
          {
            serverManager.deleteTag(serverTag);
          }
        }
        else if (event instanceof IContainerEvent<?> && event.getSource() == serverTags)
        {
          IContainerEvent<?> containerEvent = (IContainerEvent<?>)event;
          CDOBranchTag eventTag = (CDOBranchTag)containerEvent.getDeltaElement();
          if (containerEvent.getDeltaKind() == IContainerDelta.Kind.REMOVED && tagName.equals(eventTag.getName()))
          {
            localRemovals.incrementAndGet();
            if (reenteredDelete.compareAndSet(false, true))
            {
              serverManager.createTag(nestedTagName, newPoint);
            }
          }
          else if (containerEvent.getDeltaKind() == IContainerDelta.Kind.ADDED && nestedTagName.equals(eventTag.getName()))
          {
            localAdds.incrementAndGet();
          }
        }
      };
      serverTags.addListener(tagListener);

      try
      {
        runAttributed(sender, () -> {
          serverManager.moveTag(serverTag, newPoint);
          return null;
        });
      }
      finally
      {
        serverTags.removeListener(tagListener);
      }

      synchronize(sender, senderEvents, recipientEvents);
      assertEquals(Arrays.asList("MOVE:" + tagName + ":" + oldPoint + ":" + newPoint + ":" + firstModCount, "REMOVE:" + tagName + ":" + (firstModCount + 1),
          "ADD:" + nestedTagName + ":" + target.getID() + "@" + timeStamp + ":" + (firstModCount + 2)), recipientEvents.snapshot());
      assertEquals(0, senderEvents.size());
      assertEquals(1, localMoves.get());
      assertEquals(1, localRemovals.get());
      assertEquals(1, localAdds.get());
      assertNull(runAttributed(sender, () -> serverManager.getTag(tagName)));
      assertNull(recipient.getBranchManager().getTag(tagName));
      CDOBranchTag nestedTag = runAttributed(sender, () -> serverManager.getTag(nestedTagName));
      assertNotNull(nestedTag);
      assertEquals(target.getID(), nestedTag.getBranch().getID());
      CDOBranchTag recipientNestedTag = recipient.getBranchManager().getTag(nestedTagName);
      assertNotNull(recipientNestedTag);
      assertEquals(target.getID(), recipientNestedTag.getBranch().getID());
      assertEquals(timeStamp, recipientNestedTag.getTimeStamp());
    }
    finally
    {
      sender.getBranchManager().removeListener(senderEvents);
      recipient.getBranchManager().removeListener(recipientEvents);
      sender.getBranchManager().getTagList().removeListener(senderEvents);
      recipient.getBranchManager().getTagList().removeListener(recipientEvents);
      sender.close();
      recipient.close();
    }
  }

  private Thread createConcurrentTagThread(InternalCDOBranchManager branchManager, CDOBranchPoint branchPoint, String tagName, CountDownLatch ready,
      CountDownLatch start, CountDownLatch finished, AtomicReference<Throwable> failure)
  {
    return new Thread(() -> {
      ready.countDown();
      try
      {
        start.await();
        runUnattributed(() -> branchManager.changeTagWithModCount(null, null, tagName, branchPoint));
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
      finally
      {
        finished.countDown();
      }
    }, "ConcurrentTag-" + tagName);
  }

  private String assertTagCreation(String event, int modCount, CDOBranchPoint branchPoint)
  {
    String[] components = event.split(":");
    assertEquals(4, components.length);
    assertEquals("ADD", components[0]);
    assertEquals(branchPoint.getBranch().getID() + "@" + branchPoint.getTimeStamp(), components[2]);
    assertEquals(String.valueOf(modCount), components[3]);
    return components[1];
  }

  private List<String> createBranches(String prefix)
  {
    CDOBranch branch = getRepository().getBranchManager().getMainBranch().createBranch(getBranchName(prefix));
    int id = branch.getID();
    branch.setName(getBranchName(prefix + "-renamed"));
    String renamed = getBranchName(prefix + "-renamed");
    branch.delete(new Monitor());
    return Arrays.asList("CREATED:" + id, "RENAMED:" + id + ":" + renamed, "DELETED:" + id);
  }

  private List<String> createServerTags(String prefix, long timeStamp, int firstModCount)
  {
    InternalCDOBranchManager manager = getRepository().getBranchManager();
    CDOBranch main = manager.getMainBranch();
    CDOBranch target = main.getBranch(getBranchName("tag-target"));
    CDOBranchPoint mainPoint = main.getPoint(timeStamp);
    CDOBranchPoint targetPoint = target.getPoint(timeStamp);
    createTags(manager, prefix, mainPoint, targetPoint);
    return tagSequence(prefix, mainPoint, targetPoint, firstModCount);
  }

  private void createTags(InternalCDOBranchManager manager, String prefix, CDOBranchPoint mainPoint, CDOBranchPoint targetPoint)
  {
    String name = prefix + "-tag";
    CDOBranchTag tag = manager.createTag(name, mainPoint);
    manager.renameTag(name, prefix + "-renamed");
    tag = manager.getTag(prefix + "-renamed");
    manager.moveTag(tag, targetPoint);
    manager.deleteTag(tag);
  }

  private List<String> tagSequence(String prefix, CDOBranchPoint mainPoint, CDOBranchPoint targetPoint, int modCount)
  {
    String name = prefix + "-tag";
    String renamed = prefix + "-renamed";
    return Arrays.asList("ADD:" + name + ":" + pointID(mainPoint) + ":" + modCount, "RENAME:" + name + ":" + renamed + ":" + (modCount + 1),
        "MOVE:" + renamed + ":" + mainPoint + ":" + targetPoint + ":" + (modCount + 2), "REMOVE:" + renamed + ":" + (modCount + 3));
  }

  private String pointID(CDOBranchPoint point)
  {
    return point.getBranch().getID() + "@" + point.getTimeStamp();
  }

  private <T> T runAttributed(CDOSession sender, Operation<T> operation)
  {
    InternalSession serverSession = serverSession(sender);
    AtomicReference<T> result = new AtomicReference<>();
    StoreThreadLocal.wrap(serverSession, () -> {
      ServerOperationContext.setSession(serverSession);
      try
      {
        result.set(operation.run());
      }
      finally
      {
        ServerOperationContext.unsetSession();
      }
    }).run();
    return result.get();
  }

  private <T> T runUnattributed(Operation<T> operation)
  {
    IStoreAccessor accessor = getRepository().getStore().getWriter(null);
    StoreThreadLocal.setAccessor(accessor);
    try
    {
      return operation.run();
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  private void synchronize(CDOSession sender, EventRecorder senderRecorder, EventRecorder... recipients) throws InterruptedException
  {
    String barrierName = getBranchName("notification-barrier-" + barrierCounter.incrementAndGet());
    CDOBranch barrierBranch = sender.getBranchManager().getMainBranch().createBranch(barrierName);
    senderRecorder.armBarrier(barrierBranch.getID());
    assertTrue("Timed out waiting for the initiating session's local branch event", senderRecorder.awaitBarrier());

    for (EventRecorder recipient : recipients)
    {
      recipient.armBarrier(barrierBranch.getID());
    }

    for (EventRecorder recipient : recipients)
    {
      assertTrue("Timed out waiting for branch notification barrier " + barrierName, recipient.awaitBarrier());
    }
  }

  private void assertBranchEvents(List<String> expected, List<String> actual)
  {
    assertEquals(expected, actual);
  }

  /**
   * @author Eike Stepper
   */
  private static final class EventRecorder implements IListener
  {
    private final InternalCDOBranchManager branchManager;

    private final List<String> events = new ArrayList<>();

    private final List<String> expected = new ArrayList<>();

    private int barrierBranchID = -1;

    private CountDownLatch barrier;

    public EventRecorder(CDOSession session)
    {
      branchManager = (InternalCDOBranchManager)session.getBranchManager();
    }

    @Override
    public synchronized void notifyEvent(IEvent event)
    {
      if (event instanceof CDOBranchChangedEvent)
      {
        CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
        recordBranchEvent(branchEvent.getBranch().getID(), branchEvent.getChangeKind(), branchEvent.getBranch().getName());
      }
      else if (event instanceof IContainerEvent<?> && event.getSource() instanceof CDOTagList)
      {
        IContainerEvent<?> containerEvent = (IContainerEvent<?>)event;
        CDOBranchTag tag = (CDOBranchTag)containerEvent.getDeltaElement();
        if (containerEvent.getDeltaKind() == IContainerDelta.Kind.ADDED)
        {
          events.add("ADD:" + tag.getName() + ":" + tag.getBranch().getID() + "@" + tag.getTimeStamp() + ":" + branchManager.getTagModCount());
        }
        else
        {
          events.add("REMOVE:" + tag.getName() + ":" + branchManager.getTagModCount());
        }
      }
      else if (event instanceof TagRenamedEvent)
      {
        TagRenamedEvent tagEvent = (TagRenamedEvent)event;
        events.add("RENAME:" + tagEvent.getOldName() + ":" + tagEvent.getNewName() + ":" + branchManager.getTagModCount());
      }
      else if (event instanceof TagMovedEvent)
      {
        TagMovedEvent tagEvent = (TagMovedEvent)event;
        events.add("MOVE:" + tagEvent.getTag().getName() + ":" + tagEvent.getOldBranchPoint() + ":" + tagEvent.getNewBranchPoint() + ":"
            + branchManager.getTagModCount());
      }
    }

    public synchronized void recordBranchEvent(int branchID, ChangeKind changeKind, String branchName)
    {
      if (changeKind == ChangeKind.CREATED && branchID == barrierBranchID)
      {
        if (barrier != null)
        {
          barrier.countDown();
        }

        return;
      }

      if (changeKind == ChangeKind.RENAMED)
      {
        events.add("RENAMED:" + branchID + ":" + branchName);
      }
      else
      {
        events.add(changeKind + ":" + branchID);
      }
    }

    public synchronized int size()
    {
      return events.size();
    }

    public synchronized List<String> snapshot()
    {
      return new ArrayList<>(events);
    }

    public synchronized void addExpected(List<String> values)
    {
      expected.addAll(values);
    }

    public synchronized List<String> expected()
    {
      return new ArrayList<>(expected);
    }

    public synchronized void clear()
    {
      events.clear();
      expected.clear();
    }

    public synchronized void armBarrier(int branchID)
    {
      barrier = new CountDownLatch(1);
      barrierBranchID = branchID;

      String event = "CREATED:" + branchID;
      if (events.remove(event))
      {
        barrier.countDown();
      }
    }

    public boolean awaitBarrier() throws InterruptedException
    {
      CountDownLatch currentBarrier;
      synchronized (this)
      {
        currentBarrier = barrier;
      }

      return currentBarrier.await(15, TimeUnit.SECONDS);
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  private interface Operation<T>
  {
    public T run();
  }

  /**
   * @author Eike Stepper
   */
  private static final class DeletionRecorder implements IListener
  {
    private final List<Integer> branchIDs = new CopyOnWriteArrayList<>();

    private final List<List<Integer>> managerEvents = new CopyOnWriteArrayList<>();

    public DeletionRecorder()
    {
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof BranchDeletedEvent)
      {
        branchIDs.add(((BranchDeletedEvent)event).getBranch().getID());
      }
      else if (event instanceof CDOBranchChangedEvent)
      {
        CDOBranchChangedEvent branchEvent = (CDOBranchChangedEvent)event;
        if (branchEvent.getChangeKind() == CDOBranchChangedEvent.ChangeKind.DELETED)
        {
          List<Integer> ids = new ArrayList<>();
          for (int id : branchEvent.getBranchIDs())
          {
            ids.add(id);
          }

          managerEvents.add(ids);
        }
      }
    }

    public List<Integer> branchIDs()
    {
      return new ArrayList<>(branchIDs);
    }

    public List<List<Integer>> managerEvents()
    {
      return new ArrayList<>(managerEvents);
    }
  }

}
