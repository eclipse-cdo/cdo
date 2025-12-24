/*
 * Copyright (c) 2010-2013, 2016, 2017, 2021, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.AbstractCDORevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_BRANCHING, "MEM" })
public class RevisionManagerTest extends AbstractCDOTest
{
  private static final EClass CLASS = EcorePackage.eINSTANCE.getEAnnotation();

  private static final int DETACH = -1;

  protected InternalRepository repository;

  private MEMStore store;

  protected InternalCDOSession session;

  private InternalSession serverSession;

  private InternalCDOBranchManager branchManager;

  private CDOID objectID;

  private int branchID;

  private CDOBranch branch0;

  private CDOBranch branch1;

  private CDOBranch branch2;

  private CDOBranch branch3;

  private CDOBranch branch4;

  private InternalCDORevision[] revisions0;

  private InternalCDORevision[] revisions1;

  private InternalCDORevision[] revisions4;

  private TestRevisionManager revisionManager;

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    properties.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    if (getRepositoryConfig().idGenerationLocation() == IDGenerationLocation.STORE)
    {
      objectID = CDOIDUtil.createLong(2);
    }
    else
    {
      objectID = CDOIDUtil.createUUID();
    }

    Field disableGC = ReflectUtil.getField(AbstractCDORevisionCache.class, "disableGC");
    ReflectUtil.setValue(disableGC, null, true);

    repository = getRepository();
    store = (MEMStore)repository.getStore();

    session = (InternalCDOSession)openSession();
    serverSession = repository.getSessionManager().getSession(session.getSessionID());
    StoreThreadLocal.setSession(serverSession);

    branchManager = repository.getBranchManager();
    branchID = 0;

    branch0 = branchManager.getMainBranch();
    revisions0 = fillBranch(branch0, 10, 10, 20, 50, 20, DETACH);

    revisions1 = createBranch(revisions0[1], 5, 10, 20, 10, DETACH);
    branch1 = revisions1[0].getBranch();

    branch2 = createBranch(revisions1[3]);

    branch3 = createBranch(revisions1[1]);

    revisions4 = createBranch(branch3, branch3.getBase().getTimeStamp() + 10, 30, DETACH);
    branch4 = revisions4[0].getBranch();

    dumpRevisions("MEMStore", store.getAllRevisions());
    revisionManager = (TestRevisionManager)getRevisionManager(repository, session);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    StoreThreadLocal.release();
    IOUtil.close(session);

    Field disableGC = ReflectUtil.getField(AbstractCDORevisionCache.class, "disableGC");
    ReflectUtil.setValue(disableGC, null, false);

    super.doTearDown();
  }

  protected InternalCDORevisionManager getRevisionManager(InternalRepository repository, InternalCDOSession session)
  {
    return repository.getRevisionManager();
  }

  protected String getLocation()
  {
    return "Server";
  }

  protected void dumpCache(CDOBranchPoint branchPoint)
  {
    dumpRevisions(getLocation() + "Cache: Getting " + branchPoint, revisionManager.getCache().getAllRevisions());
  }

  private InternalCDORevision[] fillBranch(CDOBranch branch, long offset, long... durations)
  {
    InternalCDORevision[] revisions = new InternalCDORevision[durations.length];
    long timeStamp = branch.getBase().getTimeStamp() + offset;
    for (int i = 0; i < durations.length; i++)
    {
      long duration = durations[i];
      CDOBranchPoint branchPoint = branch.getPoint(timeStamp);

      if (duration == DETACH)
      {
        revisions[i] = store.detachObject(objectID, branch, timeStamp - 1);
      }
      else
      {
        timeStamp += duration;

        revisions[i] = new CDORevisionImpl(CLASS);
        revisions[i].setID(objectID);
        revisions[i].setBranchPoint(branchPoint);
        revisions[i].setRevised(timeStamp - 1);
        revisions[i].setVersion(i + 1);
        store.addRevision(revisions[i], false);
      }
    }

    return revisions;
  }

  private InternalCDORevision[] createBranch(CDOBranch baseBranch, long baseTimeStamp, long offset, long... durations)
  {
    CDOBranch branch = doCreateBranch(baseBranch, baseTimeStamp);
    return fillBranch(branch, offset, durations);
  }

  private InternalCDORevision[] createBranch(InternalCDORevision revision, long offset, long... durations)
  {
    CDOBranch baseBranch = revision.getBranch();
    long baseTimeStamp = getMiddleOfValidity(revision);
    return createBranch(baseBranch, baseTimeStamp, offset, durations);
  }

  private CDOBranch createBranch(InternalCDORevision revision)
  {
    CDOBranch baseBranch = revision.getBranch();
    long baseTimeStamp = getMiddleOfValidity(revision);
    return doCreateBranch(baseBranch, baseTimeStamp);
  }

  private CDOBranch doCreateBranch(CDOBranch baseBranch, long baseTimeStamp)
  {
    while (repository.getTimeStamp() < baseTimeStamp)
    {
      sleep(1);
    }

    String name = "branch" + ++branchID;
    if (baseBranch.isMainBranch())
    {
      name = getBranchName(name);
    }

    return baseBranch.createBranch(name, baseTimeStamp);
  }

  private long getMiddleOfValidity(InternalCDORevision revision)
  {
    long timeStamp = revision.getTimeStamp();
    long revised = revision.getRevised();
    if (revised == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      revised = timeStamp + 100;
    }

    return timeStamp / 2 + revised / 2;
  }

  protected InternalCDORevision getRevision(CDOBranch branch, long timeStamp)
  {
    CDOBranchPoint branchPoint = branch.getPoint(timeStamp);
    dumpCache(branchPoint);
    return revisionManager.getRevision(objectID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  private void prefetchBaseline(CDOBranch branch, int levelsUp)
  {
    long timeStamp = 0;
    while (--levelsUp >= 0)
    {
      timeStamp = branch.getBase().getTimeStamp();
      branch = branch.getBase().getBranch();
    }

    getRevision(branch, timeStamp);
    assertLoads(1);
  }

  private static void assertRevision(InternalCDORevision expected, InternalCDORevision actual)
  {
    if (expected == null)
    {
      assertEquals(null, actual);
    }
    else
    {
      assertEquals(expected.getID(), actual.getID());
      assertEquals(expected.getBranch().getID(), actual.getBranch().getID());
      assertEquals(expected.getVersion(), actual.getVersion());
    }
  }

  private void assertLoads(int expected)
  {
    assertEquals(expected, revisionManager.getLoadCounter());
    revisionManager.resetLoadCounter();
  }

  public void testBranch0_Initial() throws Exception
  {
    CDOBranch branch = branch0;
    long timeStamp = revisions0[0].getTimeStamp() - 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1); // TODO Shouldn't this be 0?
  }

  public void testBranch0_Normal() throws Exception
  {
    CDOBranch branch = branch0;
    for (int i = 0; i < revisions0.length - 1; i++)
    {
      InternalCDORevision expected = revisions0[i];
      long timeStamp = getMiddleOfValidity(expected);

      InternalCDORevision revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(1);

      revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(0);
    }
  }

  public void testBranch0_Detached() throws Exception
  {
    CDOBranch branch = branch0;
    long timeStamp = revisions0[4].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch0_Head() throws Exception
  {
    CDOBranch branch = branch0;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch1_Initial() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions0[1];

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch1_Normal() throws Exception
  {
    CDOBranch branch = branch1;
    for (int i = 0; i < revisions1.length - 1; i++)
    {
      InternalCDORevision expected = revisions1[i];
      long timeStamp = getMiddleOfValidity(expected);

      InternalCDORevision revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(1);

      revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(0);
    }
  }

  public void testBranch1_Detached() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[3].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch1_Head() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch2_Initial() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = branch2.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch2_Head() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision1 = getRevision(branch, timeStamp);
    assertRevision(expected, revision1);
    assertLoads(1);

    InternalCDORevision revision2 = getRevision(branch, timeStamp);
    assertRevision(expected, revision2);
    assertLoads(0);
  }

  public void testBranch3_Initial() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = branch3.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = revisions1[1];

    InternalCDORevision revision1 = getRevision(branch, timeStamp);
    assertRevision(expected, revision1);
    assertLoads(1);

    InternalCDORevision revision2 = getRevision(branch, timeStamp);
    assertRevision(expected, revision2);
    assertLoads(0);
  }

  public void testBranch3_Head() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = revisions1[1];

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch1_Initial() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions0[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch1_Normal() throws Exception
  {
    CDOBranch branch = branch1;

    prefetchBaseline(branch, 1);

    for (int i = 0; i < revisions1.length - 1; i++)
    {
      InternalCDORevision expected = revisions1[i];
      long timeStamp = getMiddleOfValidity(expected);

      InternalCDORevision revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(1);

      revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(0);
    }
  }

  public void testAvailableUp1_Branch1_Detached() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[3].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch1_Head() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch2_Initial() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = branch2.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch2_Head() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch3_Initial() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = branch3.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch3_Head() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch2_Initial() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = branch2.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch2_Head() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch3_Initial() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = branch3.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch3_Head() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp3_Branch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 3);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp3_Branch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 3);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp3_Branch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 3);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }
}
