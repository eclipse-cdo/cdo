/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.TestListener;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Bug 297491 - Provide support for storing named branch points, also called tags
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
@CleanRepositoriesBefore(reason = "Querying tags")
public class Bugzilla_297491_Test extends AbstractCDOTest
{
  private InternalRepository repository;

  private CDOSession session1;

  private CDOSession session2;

  private InternalCDOBranchManager repositoryBranchManager;

  private InternalCDOBranchManager branchManager1;

  private InternalCDOBranchManager branchManager2;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    start();
  }

  private long getTimeStamp()
  {
    return repository.getTimeStamp();
  }

  private void start()
  {
    repository = getRepository();
    repositoryBranchManager = repository.getBranchManager();

    session1 = openSession();
    branchManager1 = (InternalCDOBranchManager)session1.getBranchManager();

    reopenSession2(null);
  }

  private boolean restart()
  {
    if (isRestartable())
    {
      session2.close();
      session2 = null;

      session1.close();
      session1 = null;

      IOUtil.OUT().println("RESTARTING...\n");
      restartRepository();
      start();
      return true;
    }

    IOUtil.OUT().println("SKIPPING RESTART!!!\n");
    return false;
  }

  private void reopenSession2(CDOBranchManager branchManager)
  {
    if (session2 != null)
    {
      session2.close();
    }

    if (branchManager != null)
    {
      getTestProperties().put(SessionConfig.PROP_TEST_BRANCH_MANAGER, branchManager);
    }

    session2 = openSession();
    branchManager2 = (InternalCDOBranchManager)session2.getBranchManager();
  }

  private void waitForSession2() throws InterruptedException
  {
    int expected = branchManager1.getTagModCount();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return branchManager2.getTagModCount() >= expected;
      }
    }.assertNoTimeOut();
  }

  @SuppressWarnings("unchecked")
  public void testCreateAndChangeTags() throws Exception
  {
    int modCount = 0;

    /*
     * Create tag1.
     */

    CDOBranchPoint point1 = branchManager1.getMainBranch().getPoint(getTimeStamp());
    CDOBranchTag tag1 = branchManager1.createTag("tag1", point1);
    assertEquals("tag1", tag1.getName());
    assertEquals(point1.getBranch(), tag1.getBranch());
    assertEquals(point1.getTimeStamp(), tag1.getTimeStamp());
    assertEquals(++modCount, branchManager1.getTagModCount());
    assertEquals(modCount, repositoryBranchManager.getTagModCount());

    waitForSession2();
    assertEquals(modCount, branchManager2.getTagModCount());

    if (restart())
    {
      modCount = 0;
    }

    tag1 = branchManager1.getTag("tag1");
    assertEquals("tag1", tag1.getName());

    /*
     * Create tag2.
     */

    sleep(100);

    CDOBranchTag tag2 = branchManager1.createTag("tag2", null);
    assertEquals("tag2", tag2.getName());
    assertEquals(tag1.getBranch(), tag2.getBranch());
    assertTrue(tag1.getTimeStamp() < tag2.getTimeStamp());
    assertEquals(++modCount, branchManager1.getTagModCount());
    assertEquals(modCount, repositoryBranchManager.getTagModCount());

    waitForSession2();
    assertEquals(modCount, branchManager2.getTagModCount());

    CDOTagList tagList1 = branchManager1.getTagList();
    {
      CDOBranchTag[] actual = tagList1.getTags();

      CDOBranchTag[] expected = new CDOBranchTag[] { tag1, tag2 };
      Arrays.sort(expected);
      assertArrayEquals(expected, actual);

      // Check sorting.
      expected = new CDOBranchTag[] { tag2, tag1 };
      Arrays.sort(expected);
      assertArrayEquals(expected, actual);
    }

    CDOTagList tagList2 = branchManager2.getTagList();
    {
      CDOBranchTag[] actual = tagList2.getTags();

      CDOBranchTag[] expected = new CDOBranchTag[] { branchManager2.getTag("tag1"), branchManager2.getTag("tag2") };
      Arrays.sort(expected);
      assertArrayEquals(expected, actual);

      // Check sorting.
      expected = new CDOBranchTag[] { branchManager2.getTag("tag2"), branchManager2.getTag("tag1") };
      Arrays.sort(expected);
      assertArrayEquals(expected, actual);
    }

    /*
     * Create tag3.
     */

    TestListener listener1 = new TestListener(tagList1);
    TestListener listener2 = new TestListener(tagList2);

    CDOBranchTag tag3 = branchManager1.createTag("tag3", null);
    assertEquals(++modCount, branchManager1.getTagModCount());
    assertEquals(modCount, repositoryBranchManager.getTagModCount());

    waitForSession2();
    assertEquals(modCount, branchManager2.getTagModCount());

    listener1.assertEvent(IContainerEvent.class, e -> {
      IContainerEvent<CDOBranchTag> event = (IContainerEvent<CDOBranchTag>)e;
      assertEquals(1, event.getDeltas().length);
      assertEquals(tag3, event.getDeltaElement());
      assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
    });

    listener2.assertEvent(IContainerEvent.class, e -> {
      IContainerEvent<CDOBranchTag> event = (IContainerEvent<CDOBranchTag>)e;
      assertEquals(1, event.getDeltas().length);
      assertEquals(branchManager2.getTag("tag3"), event.getDeltaElement());
      assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
    });

    /*
     * Rename tag3 -> renamed.
     */

    listener1.clearEvents();
    listener2.clearEvents();

    tag3.setName("renamed");
    assertEquals(++modCount, branchManager1.getTagModCount());
    assertEquals(modCount, repositoryBranchManager.getTagModCount());

    waitForSession2();
    assertEquals(modCount, branchManager2.getTagModCount());

    assertEquals("renamed", tag3.getName());
    assertEquals(null, branchManager1.getTag("tag3"));
    assertEquals(tag3, branchManager1.getTag("renamed"));

    assertEquals(null, branchManager2.getTag("tag3"));
    CDOBranchTag renamed2 = branchManager2.getTag("renamed");
    assertEquals(tag3.getTimeStamp(), renamed2.getTimeStamp());

    assertEquals(3, tagList1.getTags().length);
    assertEquals(3, tagList2.getTags().length);

    listener1.assertEvent(CDOTagList.TagRenamedEvent.class, e -> {
      CDOTagList.TagRenamedEvent event = (CDOTagList.TagRenamedEvent)e;
      assertEquals(tag3, event.getTag());
      assertEquals("tag3", event.getOldName());
      assertEquals("renamed", event.getNewName());
    });

    listener2.assertEvent(CDOTagList.TagRenamedEvent.class, e -> {
      CDOTagList.TagRenamedEvent event = (CDOTagList.TagRenamedEvent)e;
      assertEquals(renamed2, event.getTag());
      assertEquals("tag3", event.getOldName());
      assertEquals("renamed", event.getNewName());
    });

    /*
     * Delete tag "renamed".
     */

    listener1.clearEvents();
    listener2.clearEvents();

    tag3.delete();
    assertEquals(++modCount, branchManager1.getTagModCount());
    assertEquals(modCount, repositoryBranchManager.getTagModCount());

    waitForSession2();
    assertEquals(modCount, branchManager2.getTagModCount());

    assertEquals("renamed", tag3.getName());
    assertEquals(null, tag3.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, tag3.getTimeStamp());
    assertEquals(true, tag3.isDeleted());
    assertEquals(null, branchManager1.getTag("renamed"));

    assertEquals("renamed", renamed2.getName());
    assertEquals(null, renamed2.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, renamed2.getTimeStamp());
    assertEquals(true, renamed2.isDeleted());
    assertEquals(null, branchManager1.getTag("renamed"));

    assertEquals(2, tagList1.getTags().length);
    assertEquals(2, tagList2.getTags().length);

    listener1.assertEvent(IContainerEvent.class, e -> {
      IContainerEvent<CDOBranchTag> event = (IContainerEvent<CDOBranchTag>)e;
      assertEquals(1, event.getDeltas().length);
      assertEquals(tag3, event.getDeltaElement());
      assertEquals(IContainerDelta.Kind.REMOVED, event.getDeltaKind());
    });

    listener2.assertEvent(IContainerEvent.class, e -> {
      IContainerEvent<CDOBranchTag> event = (IContainerEvent<CDOBranchTag>)e;
      assertEquals(1, event.getDeltas().length);
      assertEquals(renamed2, event.getDeltaElement());
      assertEquals(IContainerDelta.Kind.REMOVED, event.getDeltaKind());
    });

    reopenSession2(null);
    assertEquals(branchManager1.getTagModCount(), branchManager2.getTagModCount());
  }

  public void testLoadTags() throws Exception
  {
    branchManager1.createTag("tag1", null);
    waitForSession2();

    CountDownLatch running = new CountDownLatch(1);
    CountDownLatch startModCounting = new CountDownLatch(1);
    CountDownLatch modCountingStarted = new CountDownLatch(1);

    Thread thread = new Thread()
    {
      @Override
      public void run()
      {
        reopenSession2(new CDOBranchManagerImpl()
        {
          @Override
          public void setTagModCount(int tagModCount)
          {
            if (modCountingStarted.getCount() == 1)
            {
              running.countDown();
              await(startModCounting);
              IOUtil.OUT().println("Branch manager 2 started with modCount=" + tagModCount);
            }

            super.setTagModCount(tagModCount);
            modCountingStarted.countDown();
          }

          @Override
          public void handleTagChanged(int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
          {
            IOUtil.OUT().println("Scheduling tag change " + modCount + ": " + oldName + " --> " + newName + ", " + branchPoint);
            super.handleTagChanged(modCount, oldName, newName, branchPoint);
          }

          @Override
          protected void executeTagChange(String oldName, String newName, CDOBranchPoint branchPoint)
          {
            IOUtil.OUT().println("Executing tag change: " + oldName + " --> " + newName + ", " + branchPoint);
            super.executeTagChange(oldName, newName, branchPoint);
          }
        });
      }
    };

    thread.start();
    await(running);

    branchManager1.createTag("tag2", null);
    startModCounting.countDown();
    await(modCountingStarted);
    thread.join(DEFAULT_TIMEOUT);

    // TODO
    // sleep(1000);
    // assertEquals(branchManager1.getTagModCount(), branchManager2.getTagModCount());
  }
}
