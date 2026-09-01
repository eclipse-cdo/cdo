/*
 * Copyright (c) 2008-2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionScope;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Simon McDuff
 */
/**
 * @author Eike Stepper
 */
public class SavePointTest extends AbstractCDOTest
{
  /**
   * A retained savepoint reverts only a portion of an active transaction. It
   * must therefore not report the root transaction as finished or invoke the
   * root rollback handler callback.
   */
  public void testRollbackDoesNotFinishRootTransaction() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();
    AtomicInteger finishedEvents = new AtomicInteger();
    AtomicInteger rollbackCallbacks = new AtomicInteger();
    IListener listener = event -> {
      if (event instanceof CDOTransactionFinishedEvent)
      {
        finishedEvents.incrementAndGet();
      }
    };

    transaction.addListener(listener);
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void rolledBackTransaction(CDOTransaction transaction)
      {
        rollbackCallbacks.incrementAndGet();
      }
    });

    CDOResource resource = transaction.createResource(getResourcePath("/testRollbackDoesNotFinishRootTransaction"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    CDOUserSavepoint savepoint = transaction.setSavepoint();
    company.setName("discarded");
    savepoint.rollback();

    assertEquals(0, finishedEvents.get());
    assertEquals(0, rollbackCallbacks.get());
    assertTrue(transaction.isDirty());

    transaction.rollback();

    assertEquals(1, finishedEvents.get());
    assertEquals(1, rollbackCallbacks.get());
  }

  public void testRollbackWithNewObject_Collection() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction1 = session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));

    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    assertEquals(0, CDOUtil.getCDOObject(company1).cdoRevision().getVersion());
    transaction1.setSavepoint();
    assertEquals(0, CDOUtil.getCDOObject(company1).cdoRevision().getVersion());

    Category category2 = getModel1Factory().createCategory();
    company1.getCategories().add(category2);

    CDOUserSavepoint savePoint2 = transaction1.setSavepoint();
    assertEquals(0, CDOUtil.getCDOObject(company1).cdoRevision().getVersion());

    Category category3 = getModel1Factory().createCategory();
    company1.getCategories().add(category3);

    transaction1.setSavepoint();
    assertEquals(0, CDOUtil.getCDOObject(company1).cdoRevision().getVersion());
    savePoint2.rollback();
    assertEquals(0, CDOUtil.getCDOObject(company1).cdoRevision().getVersion());

    assertNew(category1, transaction1);
    assertNew(company1, transaction1);
    assertNew(resource1, transaction1);
    assertEquals(2, company1.getCategories().size());

    transaction1.commit();
    assertEquals(1, CDOUtil.getCDOObject(company1).cdoRevision().getVersion());
  }

  public void testRollbackWithNewObject_Commit() throws Exception
  {
    flow1(false, true);
  }

  public void testRollbackWithNewObject_Rollback() throws Exception
  {
    flow1(false, false);
  }

  public void testRollbackWithPersistedObject_Commit() throws Exception
  {
    flow1(true, true);
  }

  public void testRollbackWithPersistedObject_Rollback() throws Exception
  {
    flow1(true, false);
  }

  public void testWrongSavePoint() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction1 = session.openTransaction();
    // Client1
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    CDOUserSavepoint savePoint1 = transaction1.setSavepoint();
    CDOUserSavepoint savePoint2 = transaction1.setSavepoint();
    savePoint1.rollback();

    try
    {
      savePoint2.rollback();
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  public void testIsDirty() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();
    CDOUserSavepoint savePoint0 = transaction.setSavepoint();

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    Category categoryA = getModel1Factory().createCategory();
    company.getCategories().add(categoryA);

    CDOUserSavepoint savePoint1 = transaction.setSavepoint();
    Category categoryB = getModel1Factory().createCategory();
    company.getCategories().add(categoryB);

    CDOUserSavepoint savePoint2 = transaction.setSavepoint();
    CDOUserSavepoint savePoint3 = transaction.setSavepoint();

    assertEquals(true, transaction.isDirty());

    savePoint3.rollback();
    assertEquals(true, transaction.isDirty());

    savePoint2.rollback();
    assertEquals(true, transaction.isDirty());

    savePoint1.rollback();
    assertEquals(true, transaction.isDirty());

    // Didn`t make any modification prior to savepoint0
    savePoint0.rollback();
    assertEquals(false, transaction.isDirty());

    transaction.rollback();
    assertEquals(false, transaction.isDirty());
  }

  public void testPersistentDetachReattachRollbackAcrossSavepoints() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("original");
    resource.getContents().add(company);
    transaction.commit();

    CDOUserSavepoint beforeDetach = transaction.setSavepoint();
    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    assertPersistentCleanState(company, id, transaction);
    assertEquals(false, transaction.isDirty());
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    resource.getContents().remove(company);
    assertTransient(company);
    assertEquals(true, transaction.isDirty());
    assertEquals(true, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    transaction.setSavepoint();
    resource.getContents().add(company);
    assertPersistentCleanState(company, id, transaction);
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    beforeDetach.rollback();
    assertPersistentCleanState(company, id, transaction);
    assertEquals(true, resource.getContents().contains(company));
    assertEquals("original", company.getName());
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    company.setName("usable");
    assertDirty(company, transaction);
    CDOUserSavepoint usable = transaction.setSavepoint();
    company.setName("changed again");
    usable.rollback();
    assertEquals("usable", company.getName());
    assertDirty(company, transaction);
    transaction.rollback();
    assertEquals("original", company.getName());
    assertClean(company, transaction);
  }

  public void testPersistentDetachReattachRollbackWithinOneSegment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("original");
    resource.getContents().add(company);
    transaction.commit();

    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    assertPersistentCleanState(company, id, transaction);

    resource.getContents().remove(company);
    assertTransient(company);
    assertEquals(true, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    resource.getContents().add(company);
    assertPersistentCleanState(company, id, transaction);
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    transaction.rollback();
    assertPersistentCleanState(company, id, transaction);
    assertEquals(true, resource.getContents().contains(company));
    assertEquals("original", company.getName());
    company.setName("usable");
    assertDirty(company, transaction);
    transaction.rollback();
    assertEquals("original", company.getName());
    assertPersistentCleanState(company, id, transaction);
  }

  public void testPersistentChangedReattachRollbackRestoresOriginalRevision() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    company.setName("original");
    resource.getContents().add(company);
    transaction.commit();

    CDOUserSavepoint beforeDetach = transaction.setSavepoint();
    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    resource.getContents().remove(company);
    company.setName("changed while detached");
    resource.getContents().add(company);

    beforeDetach.rollback();
    assertEquals(id, CDOUtil.getCDOObject(company).cdoID());
    assertEquals("original", company.getName());
    assertEquals(true, resource.getContents().contains(company));
    assertClean(company, transaction);
  }

  public void testNewObjectDetachReattachRollbackAcrossSavepoints() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    assertNewState(company, id, transaction);
    assertEquals(true, transaction.getNewObjects().containsKey(id));
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    CDOUserSavepoint beforeDetach = transaction.setSavepoint();
    resource.getContents().remove(company);
    assertTransient(company);
    assertEquals(false, transaction.getNewObjects().containsKey(id));
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    transaction.setSavepoint();
    resource.getContents().add(company);
    assertNewState(company, id, transaction);
    assertEquals(true, transaction.getNewObjects().containsKey(id));
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    beforeDetach.rollback();
    assertNewState(company, id, transaction);
    assertEquals(true, resource.getContents().contains(company));
    assertEquals(true, transaction.getNewObjects().containsKey(id));
    assertEquals(false, transaction.getDetachedObjects().containsKey(id));
    assertEquals(false, transaction.getDirtyObjects().containsKey(id));
    assertEquals(false, transaction.getRevisionDeltas().containsKey(id));

    company.setName("usable");
    assertNewState(company, id, transaction);
    CDOUserSavepoint usable = transaction.setSavepoint();
    company.setName("changed again");
    usable.rollback();
    assertEquals("usable", company.getName());
    assertNewState(company, id, transaction);
  }

  public void testReattachDoesNotClearGlobalDirtyStateFromOlderSegment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company companyA = getModel1Factory().createCompany();
    Company companyB = getModel1Factory().createCompany();
    resource.getContents().add(companyA);
    resource.getContents().add(companyB);
    transaction.commit();

    companyA.setName("changed");
    transaction.setSavepoint();
    CDOID idB = CDOUtil.getCDOObject(companyB).cdoID();
    resource.getContents().remove(companyB);
    resource.getContents().add(companyB);

    CDOSavepoint lastSavepoint = transaction.getLastSavepoint();
    assertEquals(true, lastSavepoint.getReattachedObjects().containsKey(idB));
    // The remove/add pair changes the resource's containment list. Reattach processing removes the no-op delta for B,
    // but it cannot remove the resource delta, so this public sequence does not reach the empty-current-delta branch.
    assertEquals(false, lastSavepoint.getRevisionDeltas2().isEmpty());
    assertEquals(true, transaction.isDirty());
    assertEquals(true, transaction.getDirtyObjects().containsKey(CDOUtil.getCDOObject(companyA).cdoID()));
    assertEquals(false, transaction.getDetachedObjects().containsKey(idB));

    transaction.setSavepoint().rollback();
    assertEquals(true, transaction.isDirty());
    assertEquals("changed", companyA.getName());

    transaction.rollback();
    assertEquals(false, transaction.isDirty());
  }

  public void testRetainedSavepointAllNewObjectsDoesNotIncludeLaterSegments() throws Exception
  {
    RetainedSavepointScenario scenario = createRetainedSavepointScenario();
    assertEquals(false, scenario.first.getAllNewObjects().containsKey(scenario.idC));
  }

  public void testRetainedSavepointAllRevisionDeltasDoNotIncludeLaterSegments() throws Exception
  {
    RetainedSavepointScenario scenario = createRetainedSavepointScenario();
    assertEquals(true, scenario.first.getAllRevisionDeltas().containsKey(scenario.idA));
    assertEquals(false, scenario.first.getAllRevisionDeltas().containsKey(scenario.idB));
  }

  public void testRetainedSavepointAllDetachedObjectsDoNotIncludeLaterSegments() throws Exception
  {
    RetainedSavepointScenario scenario = createRetainedSavepointScenario();
    assertEquals(false, scenario.first.getAllDetachedObjects().containsKey(scenario.idA));
  }

  public void testRetainedSavepointAllDirtyObjectsDoNotIncludeLaterSegments() throws Exception
  {
    RetainedSavepointScenario scenario = createRetainedSavepointScenario();
    assertEquals(true, scenario.first.getAllDirtyObjects().containsKey(scenario.idA));
    assertEquals(false, scenario.first.getAllDirtyObjects().containsKey(scenario.idB));
  }

  public void testRetainedSavepointAllBaseNewObjectsDoNotIncludeLaterSegments() throws Exception
  {
    RetainedSavepointScenario scenario = createRetainedSavepointScenario();
    assertEquals(false, scenario.first.getAllBaseNewObjects().containsKey(scenario.idC));
  }

  public void testRetainedSavepointAllChangeSetDataDoesNotIncludeLaterSegments() throws Exception
  {
    RetainedSavepointScenario scenario = createRetainedSavepointScenario();
    CDOChangeSetData data = scenario.first.getAllChangeSetData();
    assertEquals(false, data.getNewObjects().stream().anyMatch(value -> value.getID() == scenario.idC));
    assertEquals(true, data.getChangedObjects().stream().anyMatch(value -> value.getID() == scenario.idA));
    assertEquals(false, data.getChangedObjects().stream().anyMatch(value -> value.getID() == scenario.idB));
    assertEquals(false, data.getDetachedObjects().stream().anyMatch(value -> value.getID() == scenario.idA));
  }

  public void testAggregationAcrossInvisibleScopeBoundary() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/aggregationScope"));
    Company first = getModel1Factory().createCompany();
    resource.getContents().add(first);
    transaction.commit();

    first.setName("p1");
    InternalCDOSavepoint p1 = (InternalCDOSavepoint)transaction.setSavepoint();
    Company second = getModel1Factory().createCompany();
    resource.getContents().add(second);
    second.setName("s1");
    CDOTransactionScope scope = transaction.openScope();
    Company third = getModel1Factory().createCompany();
    resource.getContents().add(third);
    InternalCDOSavepoint p2 = (InternalCDOSavepoint)transaction.setSavepoint();
    Company fourth = getModel1Factory().createCompany();
    resource.getContents().add(fourth);

    CDOID firstID = CDOUtil.getCDOObject(first).cdoID();
    CDOID secondID = CDOUtil.getCDOObject(second).cdoID();
    CDOID thirdID = CDOUtil.getCDOObject(third).cdoID();
    CDOID fourthID = CDOUtil.getCDOObject(fourth).cdoID();
    assertTrue(p1.getAllDirtyObjects().containsKey(firstID));
    assertFalse(p1.getAllDirtyObjects().containsKey(secondID));
    assertFalse(p1.getAllDirtyObjectsIncludingCurrent().containsKey(secondID));
    assertTrue(p2.getAllNewObjects().containsKey(thirdID));
    assertFalse(p2.getAllNewObjects().containsKey(fourthID));
    assertTrue(p2.getAllNewObjectsIncludingCurrent().containsKey(fourthID));
    assertTrue(transaction.getNewObjects().containsKey(fourthID));
    scope.close();
  }

  private RetainedSavepointScenario createRetainedSavepointScenario() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    Company companyA = getModel1Factory().createCompany();
    Company companyB = getModel1Factory().createCompany();
    resource.getContents().add(companyA);
    resource.getContents().add(companyB);
    transaction.commit();

    transaction.setSavepoint();
    companyA.setName("first");
    CDOSavepoint first = transaction.setSavepoint();
    CDOID idA = CDOUtil.getCDOObject(companyA).cdoID();
    CDOID idB = CDOUtil.getCDOObject(companyB).cdoID();

    Company companyC = getModel1Factory().createCompany();
    resource.getContents().add(companyC);
    CDOID idC = CDOUtil.getCDOObject(companyC).cdoID();
    companyB.setName("later");
    resource.getContents().remove(companyA);
    transaction.setSavepoint();
    return new RetainedSavepointScenario(first, idA, idB, idC);
  }

  private static void assertPersistentCleanState(Company company, CDOID id, CDOTransaction transaction)
  {
    assertClean(company, transaction);
    assertEquals(id, CDOUtil.getCDOObject(company).cdoID());
    assertEquals(false, id.isTemporary());
    assertEquals(transaction, CDOUtil.getCDOObject(company).cdoView());
    assertNotNull(CDOUtil.getCDOObject(company).cdoRevision());
  }

  private static void assertNewState(Company company, CDOID id, CDOTransaction transaction)
  {
    assertNew(company, transaction);
    CDOID actualID = CDOUtil.getCDOObject(company).cdoID();
    assertEquals(id, actualID);
    assertEquals(id.isTemporary(), actualID.isTemporary());
    assertEquals(transaction, CDOUtil.getCDOObject(company).cdoView());
    assertNotNull(CDOUtil.getCDOObject(company).cdoRevision());
    assertEquals(0, CDOUtil.getCDOObject(company).cdoRevision().getVersion());
  }

  private static final class RetainedSavepointScenario
  {
    private final CDOSavepoint first;

    private final CDOID idA;

    private final CDOID idB;

    private final CDOID idC;

    private RetainedSavepointScenario(CDOSavepoint first, CDOID idA, CDOID idB, CDOID idC)
    {
      this.first = first;
      this.idA = idA;
      this.idB = idB;
      this.idC = idC;
    }
  }

  private void flow1(boolean commitBegin, boolean commitEnd) throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();

    // Client1
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));
    Category category3, category2, category4;

    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);

    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);

    CDOUserSavepoint savePoint1 = transaction.setSavepoint();

    // Modification for savePoint1
    Company company2 = getModel1Factory().createCompany();
    resource1.getContents().add(company2);
    company1.setCity("CITY1");

    assertEquals(2, resource1.getContents().size());

    // Rollback
    savePoint1.rollback();

    if (commitBegin)
    {
      transaction.commit();
    }

    {
      assertEquals(null, company1.getCity());
      assertEquals(1, resource1.getContents().size());
      company1.setCity("CITY1");
      category2 = getModel1Factory().createCategory();
      company1.getCategories().add(category2);
    }

    CDOUserSavepoint savePoint2 = transaction.setSavepoint();

    {
      company1.setCity("CITY2");
      category3 = getModel1Factory().createCategory();
      company1.getCategories().add(category3);
    }

    transaction.setSavepoint();

    {
      company1.setCity("CITY3");
      assertEquals(3, company1.getCategories().size());
      category4 = getModel1Factory().createCategory();
      company1.getCategories().add(category4);
    }

    savePoint2.rollback();
    assertEquals(true, transaction.isDirty());

    // Test NEW TO NEW
    assertEquals(false, FSMUtil.isTransient(CDOUtil.getCDOObject(company1)));

    // Test NEW TO TRANSIENT (2 step back)
    assertEquals(true, FSMUtil.isTransient(CDOUtil.getCDOObject(category3)));
    assertEquals(false, transaction.getNewObjects().containsKey(CDOUtil.getCDOObject(category3).cdoID()));

    // Test NEW TO TRANSIENT (1 step back)
    assertEquals(true, FSMUtil.isTransient(CDOUtil.getCDOObject(category4)));
    assertEquals(false, transaction.getNewObjects().containsKey(CDOUtil.getCDOObject(category4).cdoID()));

    // Test NEW TO NEW
    assertEquals(false, FSMUtil.isTransient(CDOUtil.getCDOObject(category2)));
    assertEquals(true, transaction.getNewObjects().containsKey(CDOUtil.getCDOObject(category2).cdoID()));

    // Test rollback NEW
    assertEquals("CITY1", company1.getCity());
    assertEquals(2, company1.getCategories().size());
    if (commitEnd)
    {
      transaction.commit();
      assertClean(company1, transaction);
      assertClean(category2, transaction);
      assertEquals("CITY1", company1.getCity());
      assertEquals(2, company1.getCategories().size());
      assertEquals(null, transaction.getLastSavepoint().getPreviousSavepoint());
    }
    else
    {
      transaction.rollback();
      assertEquals(false, transaction.isDirty());
      assertEquals(null, transaction.getLastSavepoint().getNextSavepoint());
      assertEquals(null, transaction.getLastSavepoint().getPreviousSavepoint());
      assertEquals(commitBegin, !FSMUtil.isTransient(CDOUtil.getCDOObject(company1)));
      assertEquals(commitBegin, !FSMUtil.isTransient(resource1));
    }
  }

  /**
   * Compare http://www.eclipse.org/newsportal/article.php?id=41697&group=eclipse.tools.emf#41697
   *
   * <pre>
   * Passive update is enabled.
   * client1 sets a save point
   * client1 write locks object1
   * client1 modifies object1
   * client2 modifies object2
   * client2 commits
   * client1 rolls back to save point
   * Result:
   * CDORepositoryInfo: object1 not modified, object2 is modified
   * client1: same as repository
   * client2: same as repository
   * </pre>
   */
  public void _testScenario1() throws Exception
  {
    CDOSession client1 = openSession();
    CDOTransaction transaction1 = client1.openTransaction();
    CDOResource object1X = transaction1.createResource(getResourcePath("/object1"));
    CDOResource object2X = transaction1.createResource(getResourcePath("/object2"));
    transaction1.commit();

    // client1 sets a save point
    CDOUserSavepoint savepoint = transaction1.setSavepoint();

    // client1 write locks object1
    object1X.cdoWriteLock().lock(DEFAULT_TIMEOUT);

    // client1 modifies object1
    object1X.getContents().add(getModel1Factory().createCompany());

    // client2 modifies object2
    CDOSession client2 = openSession();
    CDOTransaction transaction2 = client2.openTransaction();
    CDOResource object2Y = transaction2.getResource(getResourcePath("/object2"));
    object2Y.getContents().add(getModel1Factory().createPurchaseOrder());

    // client2 commits
    transaction2.commit();

    // client1 rolls back to save point
    savepoint.rollback();

    // CDORepositoryInfo: object1 not modified, object2 is modified
    CDOSession client3 = openSession();
    CDOView view = client3.openView();
    CDOResource object1Test = view.getResource(getResourcePath("/object1"));
    assertEquals(0, object1Test.getContents().size());
    CDOResource object2Test = view.getResource(getResourcePath("/object2"));
    assertEquals(1, object2Test.getContents().size());

    // client1: same as repository
    assertEquals(object1Test.getContents().size(), object1X.getContents().size());
    assertEquals(object2Test.getContents().size(), object2X.getContents().size());

    // client2: same as repository
    CDOResource object1Y = transaction2.getResource(getResourcePath("/object1"));
    assertEquals(object1Test.getContents().size(), object1Y.getContents().size());
    assertEquals(object2Test.getContents().size(), object2Y.getContents().size());
  }

  /**
   * Bug 283131
   */
  public void testNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    company.setCity("CITY1");
    CDOUserSavepoint savePoint1 = transaction.setSavepoint();

    company.setCity("CITY2");

    TestAdapter adapter = new TestAdapter(company);

    savePoint1.rollback();
    assertEquals("CITY1", company.getCity());
    adapter.assertNotifications(1);
  }
}
