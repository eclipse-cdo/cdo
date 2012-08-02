/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreatedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.AbstractCDORevisionCache;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore
public class BranchingTest extends AbstractCDOTest
{
  protected CDOSession session1;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    Field disableGC = ReflectUtil.getField(AbstractCDORevisionCache.class, "disableGC");
    ReflectUtil.setValue(disableGC, null, true);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    Field disableGC = ReflectUtil.getField(AbstractCDORevisionCache.class, "disableGC");
    ReflectUtil.setValue(disableGC, null, false);
    super.doTearDown();
  }

  protected CDOSession openSession1()
  {
    session1 = openSession();
    return session1;
  }

  protected void closeSession1()
  {
    session1.close();
    session1 = null;
  }

  protected CDOSession openSession2()
  {
    return openSession();
  }

  @CleanRepositoriesBefore
  public void testMainBranch() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    assertEquals(CDOBranch.MAIN_BRANCH_ID, mainBranch.getID());
    assertEquals(CDOBranch.MAIN_BRANCH_NAME, mainBranch.getName());
    assertEquals(null, mainBranch.getBase().getBranch());
    assertEquals(0, mainBranch.getBranches().length);
    closeSession1();

    session = openSession2();
    mainBranch = session.getBranchManager().getBranch(CDOBranch.MAIN_BRANCH_ID);
    assertEquals(CDOBranch.MAIN_BRANCH_ID, mainBranch.getID());
    assertEquals(CDOBranch.MAIN_BRANCH_NAME, mainBranch.getName());
    assertEquals(null, mainBranch.getBase().getBranch());
    assertEquals(0, mainBranch.getBranches().length);
    session.close();
  }

  @CleanRepositoriesBefore
  public void testCreateBranch() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch branch = mainBranch.createBranch("testing");
    assertEquals(CDOBranch.MAIN_BRANCH_ID + 1, branch.getID());
    assertEquals("testing", branch.getName());
    assertEquals(CDOBranch.MAIN_BRANCH_ID, branch.getBase().getBranch().getID());
    assertEquals(0, branch.getBranches().length);
    session.close();
  }

  public void testGetBranch() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch branch = mainBranch.createBranch("testing");
    closeSession1();

    session = openSession2();
    branch = session.getBranchManager().getBranch(CDOBranch.MAIN_BRANCH_ID + 1);
    assertEquals(CDOBranch.MAIN_BRANCH_ID + 1, branch.getID());
    assertEquals("testing", branch.getName());
    assertEquals(CDOBranch.MAIN_BRANCH_ID, branch.getBase().getBranch().getID());
    assertEquals(0, branch.getBranches().length);
    session.close();
  }

  public void testGetSubBranches() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    mainBranch.createBranch("testing1");
    mainBranch.createBranch("testing2");
    closeSession1();

    session = openSession2();
    mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch[] branches = mainBranch.getBranches();
    assertEquals(2, branches.length);
    assertEquals("testing1", branches[0].getName());
    assertEquals(CDOBranch.MAIN_BRANCH_ID, branches[0].getBase().getBranch().getID());
    assertEquals("testing2", branches[1].getName());
    assertEquals(CDOBranch.MAIN_BRANCH_ID, branches[1].getBase().getBranch().getID());
    session.close();
  }

  public void testEvent() throws Exception
  {
    CDOSession session1 = openSession1();
    CDOSession session2 = openSession2();

    final AsyncResult<CDOBranch> result = new AsyncResult<CDOBranch>();
    session2.getBranchManager().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOBranchCreatedEvent)
        {
          CDOBranchCreatedEvent e = (CDOBranchCreatedEvent)event;
          result.setValue(e.getBranch());
        }
      }
    });

    CDOBranch mainBranch = session1.getBranchManager().getMainBranch();
    CDOBranch branch = mainBranch.createBranch("testing");
    CDOBranch resultBranch = result.getValue();
    assertEquals(branch, resultBranch);

    closeSession1();
    session2.close();
  }

  public void testGetPath() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOBranch testing1 = mainBranch.createBranch("testing1");
    CDOBranch testing2 = mainBranch.createBranch("testing2");
    CDOBranch subsub = testing1.createBranch("subsub");
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    assertEquals(mainBranch, branchManager.getBranch("MAIN"));
    assertEquals(testing1, branchManager.getBranch("MAIN/testing1"));
    assertEquals(testing2, branchManager.getBranch("MAIN/testing2"));
    assertEquals(subsub, branchManager.getBranch("MAIN/testing1/subsub"));
    assertEquals(testing1, mainBranch.getBranch("testing1"));
    assertEquals(testing2, mainBranch.getBranch("testing2"));
    assertEquals(subsub, mainBranch.getBranch("testing1/subsub"));
    assertEquals(subsub, testing1.getBranch("subsub"));
    session.close();
  }

  public void testGetPathName() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOBranch testing1 = mainBranch.createBranch("testing1");
    mainBranch.createBranch("testing2");
    testing1.createBranch("subsub");
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    assertEquals("MAIN", branchManager.getBranch("MAIN").getPathName());
    assertEquals("MAIN/testing1", branchManager.getBranch("MAIN/testing1").getPathName());
    assertEquals("MAIN/testing2", branchManager.getBranch("MAIN/testing2").getPathName());
    assertEquals("MAIN/testing1/subsub", branchManager.getBranch("MAIN/testing1/subsub").getPathName());
    assertEquals("MAIN/testing1", mainBranch.getBranch("testing1").getPathName());
    assertEquals("MAIN/testing2", mainBranch.getBranch("testing2").getPathName());
    assertEquals("MAIN/testing1/subsub", mainBranch.getBranch("testing1/subsub").getPathName());
    assertEquals("MAIN/testing1/subsub", testing1.getBranch("subsub").getPathName());
    session.close();
  }

  public void testBasePath() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOBranch testing1 = mainBranch.createBranch("testing1");
    CDOBranch testing2 = mainBranch.createBranch("testing2");
    CDOBranch subsub = testing1.createBranch("subsub");
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    assertEquals(mainBranch.getBasePath(), new CDOBranchPoint[] { mainBranch.getBase() });
    assertEquals(testing1.getBasePath(), new CDOBranchPoint[] { mainBranch.getBase(), testing1.getBase() });
    assertEquals(testing2.getBasePath(), new CDOBranchPoint[] { mainBranch.getBase(), testing2.getBase() });
    assertEquals(subsub.getBasePath(),
        new CDOBranchPoint[] { mainBranch.getBase(), testing1.getBase(), subsub.getBase() });
    session.close();
  }

  public void testAncestor() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getMainBranch();

    CDOBranch testing1 = mainBranch.createBranch("testing1");
    CDOBranch subsub1 = testing1.createBranch("subsub1");

    CDOBranch testing2 = mainBranch.createBranch("testing2");
    CDOBranch subsub2 = testing2.createBranch("subsub2");

    closeSession1();
    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();

    assertAncestor(mainBranch.getBase(), mainBranch.getBase(), mainBranch.getHead());
    assertAncestor(mainBranch.getBase(), mainBranch.getBase(), testing1.getHead());
    assertAncestor(mainBranch.getBase(), mainBranch.getBase(), subsub1.getHead());
    assertAncestor(mainBranch.getBase(), mainBranch.getBase(), testing2.getHead());
    assertAncestor(mainBranch.getBase(), mainBranch.getBase(), subsub2.getHead());

    assertAncestor(testing1.getBase(), testing1.getBase(), testing1.getHead());
    assertAncestor(subsub1.getBase(), subsub1.getBase(), subsub1.getHead());
    assertAncestor(testing2.getBase(), testing2.getBase(), testing2.getHead());
    assertAncestor(subsub2.getBase(), subsub2.getBase(), subsub2.getHead());

    assertAncestor(testing1.getBase(), subsub1.getHead(), subsub2.getHead());
    assertAncestor(subsub2.getBase(), testing2.getHead(), subsub2.getHead());
    assertAncestor(subsub1.getBase(), testing1.getHead(), subsub1.getHead());

    session.close();
  }

  private void assertAncestor(CDOBranchPoint expected, CDOBranchPoint point1, CDOBranchPoint point2)
  {
    assertEquals(expected, CDOBranchUtil.getAncestor(point1, point2));
    assertEquals(expected, CDOBranchUtil.getAncestor(point2, point1));
  }

  public void testContainment() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getMainBranch();

    CDOBranch testing1 = mainBranch.createBranch("testing1");
    CDOBranch subsub1 = testing1.createBranch("subsub1");

    CDOBranch testing2 = mainBranch.createBranch("testing2");
    CDOBranch subsub2 = testing2.createBranch("subsub2");

    closeSession1();
    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();

    assertEquals(true, CDOBranchUtil.isContainedBy(mainBranch.getBase(), mainBranch.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(mainBranch.getBase(), testing1.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(mainBranch.getBase(), subsub1.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(mainBranch.getBase(), testing2.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(mainBranch.getBase(), subsub2.getHead()));

    assertEquals(true, CDOBranchUtil.isContainedBy(testing1.getBase(), testing1.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(subsub1.getBase(), subsub1.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(testing2.getBase(), testing2.getHead()));
    assertEquals(true, CDOBranchUtil.isContainedBy(subsub2.getBase(), subsub2.getHead()));

    session.close();
  }

  public void testCommit() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);
    orderDetail.setPrice(5);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);

    CDOCommitInfo commitInfo = transaction.commit();
    dumpAll(session);
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    orderDetail = (OrderDetail)resource.getContents().get(1);
    assertEquals(5.0f, orderDetail.getPrice());
    product = orderDetail.getProduct();
    assertEquals("CDO", product.getName());

    // Modify
    orderDetail.setPrice(10);
    commitInfo = transaction.commit();
    dumpAll(session);
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime2 = commitInfo.getTimeStamp();

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    check(session, mainBranch, commitTime1, 5, "CDO");
    check(session, mainBranch, commitTime2, 5, "CDO");
    check(session, mainBranch, CDOBranchPoint.UNSPECIFIED_DATE, 5, "CDO");

    check(session, subBranch, commitTime1, 5, "CDO");
    check(session, subBranch, commitTime2, 10, "CDO");
    check(session, subBranch, CDOBranchPoint.UNSPECIFIED_DATE, 10, "CDO");

    session.close();
  }

  public void testCommitAddOrderDetail() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);
    orderDetail.setPrice(5);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);

    CDOCommitInfo commitInfo = transaction.commit();
    dumpAll(session);
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    orderDetail = (OrderDetail)resource.getContents().get(1);
    assertEquals(5.0f, orderDetail.getPrice());
    product = orderDetail.getProduct();
    assertEquals("CDO", product.getName());

    // Modify
    OrderDetail orderDetail2 = getModel1Factory().createOrderDetail();
    orderDetail2.setProduct(product);
    orderDetail2.setPrice(10);
    resource.getContents().add(0, orderDetail2);

    commitInfo = transaction.commit();
    dumpAll(session);
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime2 = commitInfo.getTimeStamp();

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    check(session, mainBranch, commitTime1, 5, "CDO");
    check(session, mainBranch, commitTime2, 5, "CDO");
    check(session, mainBranch, CDOBranchPoint.UNSPECIFIED_DATE, 5, "CDO");

    check(session, subBranch, commitTime1, 5, "CDO");
    check(session, subBranch, commitTime2, 5, 10, "CDO");
    check(session, subBranch, CDOBranchPoint.UNSPECIFIED_DATE, 5, 10, "CDO");

    session.close();
  }

  public void testCommitRemoveOrderDetail() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);
    orderDetail.setPrice(5);

    OrderDetail orderDetail2 = getModel1Factory().createOrderDetail();
    orderDetail2.setProduct(product);
    orderDetail2.setPrice(10);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(orderDetail2);
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);

    CDOCommitInfo commitInfo = transaction.commit();
    dumpAll(session);
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    orderDetail = (OrderDetail)resource.getContents().get(2);
    assertEquals(5.0f, orderDetail.getPrice());
    product = orderDetail.getProduct();
    assertEquals("CDO", product.getName());

    // Modify
    resource.getContents().remove(product.getOrderDetails().remove(1));

    commitInfo = transaction.commit();
    dumpAll(session);
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime2 = commitInfo.getTimeStamp();

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    check(session, mainBranch, commitTime1, 5, 10, "CDO");
    check(session, mainBranch, commitTime2, 5, 10, "CDO");
    check(session, mainBranch, CDOBranchPoint.UNSPECIFIED_DATE, 5, 10, "CDO");

    check(session, subBranch, commitTime1, 5, 10, "CDO");
    check(session, subBranch, commitTime2, 5, "CDO");
    check(session, subBranch, CDOBranchPoint.UNSPECIFIED_DATE, 5, "CDO");

    session.close();
  }

  public void testDetachExisting() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);
    orderDetail.setPrice(5);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    orderDetail = (OrderDetail)resource.getContents().get(1);
    assertEquals(5.0f, orderDetail.getPrice());
    product = orderDetail.getProduct();
    assertEquals("CDO", product.getName());

    // Modify
    orderDetail.setPrice(10);
    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime2 = commitInfo.getTimeStamp();

    // Detach an object that already has revision in subBranch
    resource.getContents().remove(1);

    try
    {
      // product.getOrderDetails() contains pointer to detached orderDetail
      commitInfo = transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      assertInstanceOf(DanglingReferenceException.class, expected.getCause());
    }

    orderDetail.setProduct(null);

    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime3 = commitInfo.getTimeStamp();

    orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(777);

    try
    {
      product.getOrderDetails().set(0, orderDetail);
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Success
    }

    product.getOrderDetails().add(orderDetail);

    try
    {
      // New orderDetail is not attached
      commitInfo = transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      assertInstanceOf(DanglingReferenceException.class, expected.getCause());
    }

    resource.getContents().add(orderDetail);

    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime4 = commitInfo.getTimeStamp();

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    check(session, mainBranch, commitTime1, 5, "CDO");
    check(session, mainBranch, commitTime2, 5, "CDO");
    check(session, mainBranch, commitTime3, 5, "CDO");
    check(session, mainBranch, commitTime4, 5, "CDO");
    check(session, mainBranch, CDOBranchPoint.UNSPECIFIED_DATE, 5, "CDO");

    check(session, subBranch, commitTime1, 5, "CDO");
    check(session, subBranch, commitTime2, 10, "CDO");

    try
    {
      check(session, subBranch, commitTime3, 0, "CDO", 1);
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Success
    }

    check(session, subBranch, commitTime4, 777, "CDO");
    check(session, subBranch, CDOBranchPoint.UNSPECIFIED_DATE, 777, "CDO");

    session.close();
  }

  private void check(CDOSession session, CDOBranch branch, long timeStamp, float price, String name, int size)
  {
    CDOView view = session.openView(branch, timeStamp);
    CDOResource resource = view.getResource(getResourcePath("/res"));

    int actualSize = resource.getContents().size();
    assertEquals(size, actualSize);

    dumpAll(session);
    OrderDetail orderDetail = (OrderDetail)resource.getContents().get(1);
    dumpAll(session);

    float actualPrice = orderDetail.getPrice();
    assertEquals(price, actualPrice);

    Product1 product = orderDetail.getProduct();
    dumpAll(session);

    String actualName = product.getName();
    assertEquals(name, actualName);

    view.close();
  }

  private void check(CDOSession session, CDOBranch branch, long timeStamp, float price, String name)
  {
    check(session, branch, timeStamp, price, name, 2);
  }

  private void check(CDOSession session, CDOBranch branch, long timeStamp, float price, float price2, String name)
  {
    CDOView view = session.openView(branch, timeStamp);
    CDOResource resource = view.getResource(getResourcePath("/res"));
    assertEquals(3, resource.getContents().size());

    dumpAll(session);
    OrderDetail orderDetail2 = (OrderDetail)resource.getContents().get(0);
    OrderDetail orderDetail = (OrderDetail)resource.getContents().get(2);

    dumpAll(session);
    assertEquals(price, orderDetail.getPrice());
    assertEquals(price2, orderDetail2.getPrice());

    Product1 product = orderDetail.getProduct();
    Product1 product2 = orderDetail2.getProduct();
    dumpAll(session);
    assertEquals(name, product.getName());
    assertEquals(name, product2.getName());

    view.close();
  }

  public void testDetachWithoutRevision() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    product = (Product1)resource.getContents().get(0);
    assertEquals("CDO", product.getName());

    // Detach an object that has no revision in subBranch
    resource.getContents().remove(0);

    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime2 = commitInfo.getTimeStamp();

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    check(session, subBranch, commitTime1, "CDO");

    try
    {
      check(session, subBranch, commitTime2, "CDO");
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Success
    }

    try
    {
      check(session, subBranch, CDOBranchPoint.UNSPECIFIED_DATE, "CDO");
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Success
    }

    session.close();
  }

  public void testDetachWithoutRevision_CheckMainBranch() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    product = (Product1)resource.getContents().get(0);
    assertEquals("CDO", product.getName());

    // Detach an object that has no revision in subBranch
    resource.getContents().remove(0);

    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());
    long commitTime2 = commitInfo.getTimeStamp();

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    check(session, mainBranch, commitTime1, "CDO");
    check(session, mainBranch, commitTime2, "CDO");
    check(session, mainBranch, CDOBranchPoint.UNSPECIFIED_DATE, "CDO");

    check(session, subBranch, commitTime1, "CDO");

    try
    {
      check(session, subBranch, commitTime2, "CDO");
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Success
    }

    try
    {
      check(session, subBranch, CDOBranchPoint.UNSPECIFIED_DATE, "CDO");
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Success
    }

    session.close();
  }

  @CleanRepositoriesBefore
  public void testhandleRevisionsAfterDetachInSubBranch() throws Exception
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    // Commit to sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);
    transaction = session.openTransaction(subBranch);
    assertEquals(subBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    resource = transaction.getResource(getResourcePath("/res"));
    product = (Product1)resource.getContents().get(0);
    assertEquals("CDO", product.getName());

    product.setName("handleRevisions");
    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());

    resource.getContents().remove(0);
    commitInfo = transaction.commit();
    assertEquals(subBranch, commitInfo.getBranch());

    transaction.close();
    closeSession1();

    session = openSession2();
    branchManager = session.getBranchManager();
    mainBranch = branchManager.getMainBranch();
    subBranch = mainBranch.getBranch("subBranch");

    final List<CDORevision> revisions = new ArrayList<CDORevision>();

    CDOSessionProtocol sessionProtocol = ((InternalCDOSession)session).getSessionProtocol();
    sessionProtocol.handleRevisions(null, subBranch, false, CDOBranchPoint.UNSPECIFIED_DATE, false,
        new CDORevisionHandler()
        {
          public boolean handleRevision(CDORevision revision)
          {
            if (revision.getEClass() == getModel1Package().getProduct1())
            {
              fail("Product1 has been detached and should not be passed in here");
            }

            revisions.add(revision);
            return true;
          }
        });

    assertEquals(3, revisions.size());
  }

  public void testSwitchViewTarget() throws CommitException
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    // Commit to main branch
    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    assertEquals(mainBranch, transaction.getBranch());
    assertEquals(CDOBranchPoint.UNSPECIFIED_DATE, transaction.getTimeStamp());

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);

    CDOCommitInfo commitInfo = transaction.commit();
    assertEquals(mainBranch, commitInfo.getBranch());
    long commitTime1 = commitInfo.getTimeStamp();
    transaction.close();

    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);

    CDOID id = CDOUtil.getCDOObject(product).cdoID();
    CDOView view = session.openView();
    product = (Product1)CDOUtil.getEObject(view.getObject(id));

    view.setBranch(subBranch);

    assertEquals(false, CDOUtil.getCDOObject(product).cdoState().equals(CDOState.INVALID));
    assertNotNull(product.getName());
  }

  public void testSwitchTransactionTarget() throws CommitException
  {
    CDOSession session = openSession1();
    CDOBranchManager branchManager = session.getBranchManager();

    CDOBranch mainBranch = branchManager.getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    Product1 product = getModel1Factory().createProduct1();
    product.setName("CDO");

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(product);

    // Commit to main branch
    long commitTime1 = transaction.commit().getTimeStamp();

    // Create sub branch
    CDOBranch subBranch = mainBranch.createBranch("subBranch", commitTime1);

    // Switch to sub branch
    transaction.setBranch(subBranch);
    assertEquals("CDO", product.getName());

    // Commit to sub branch
    product.setName("EMF");
    transaction.commit();

    // Switch to main branch
    transaction.setBranch(mainBranch);
    assertEquals("CDO", product.getName());

    // Commit to main branch
    product.setName("EMF");
    transaction.commit();
  }

  private void check(CDOSession session, CDOBranch branch, long timeStamp, String name)
  {
    CDOView view = session.openView(branch, timeStamp);
    CDOResource resource = view.getResource(getResourcePath("/res"));

    dumpAll(session);
    Product1 product = (Product1)resource.getContents().get(0);
    assertEquals(name, product.getName());

    view.close();
  }

  private void dumpAll(CDOSession session)
  {
    IStore store = getRepository().getStore();
    if (store instanceof MEMStore)
    {
      MEMStore memStore = (MEMStore)store;
      dump("MEMStore", memStore.getAllRevisions());
    }

    dump("ServerCache", getRepository().getRevisionManager().getCache().getAllRevisions());
    dump("ClientCache", ((InternalCDOSession)session).getRevisionManager().getCache().getAllRevisions());
  }

  public static void dump(String label, Map<CDOBranch, List<CDORevision>> revisions)
  {
    System.out.println();
    System.out.println();
    System.out.println(label);
    System.out
        .println("===============================================================================================");
    CDORevisionUtil.dumpAllRevisions(revisions, System.out);
    System.out.println();
    System.out.println();
  }
}
