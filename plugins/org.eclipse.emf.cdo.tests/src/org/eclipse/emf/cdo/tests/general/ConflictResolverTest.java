/*
 * Copyright (c) 2008-2013, 2016-2018, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta.Type;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOChangeSubscriptionAdapter;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Simon McDuff
 */
public class ConflictResolverTest extends AbstractCDOTest
{
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testInterruptedSetterWithMergingConflictResolver() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().addConflictResolver(new CDOMergingConflictResolver());

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1);
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(orderDetail);
    transaction.commit();

    CDOObject cdoObject = CDOUtil.getCDOObject(orderDetail);
    AtomicReference<Throwable> workerFailure = new AtomicReference<>();

    Thread worker = new Thread(() -> {
      Thread.currentThread().interrupt();

      try
      {
        orderDetail.setPrice(2);
      }
      catch (Throwable failure)
      {
        workerFailure.set(failure);
      }
    }, "interrupted-price-setter");
    worker.start();
    worker.join();

    String stateAfterSetter = describeTransactionState(transaction, cdoObject, orderDetail);
    Throwable emptyCommitFailure = captureFailure(transaction::commit);
    Throwable normalModificationFailure = captureFailure(() -> {
      orderDetail.setPrice(3);
      transaction.commit();
    });

    float freshPrice = Float.NaN;
    Throwable freshReadFailure = null;

    try
    {
      CDOSession freshSession = openSession();
      CDOTransaction freshTransaction = freshSession.openTransaction();
      OrderDetail freshOrderDetail = (OrderDetail)freshTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
      freshPrice = freshOrderDetail.getPrice();
    }
    catch (Throwable failure)
    {
      freshReadFailure = failure;
    }

    assertNotNull("The interrupted setter must propagate its exception", workerFailure.get());
    assertTrue("Expected an interruption-related cause: " + describeCauseChain(workerFailure.get()), hasCause(workerFailure.get(), InterruptedException.class));
    assertEquals(
        "price=1.0, state=CLEAN, dirty=false, dirtyObjects=[], revisionDeltas=[]; emptyCommit=<none>; normalModification=<none>; freshPrice=3.0; freshRead=<none>",
        stateAfterSetter + "; emptyCommit=" + describeCauseChain(emptyCommitFailure) + "; normalModification=" + describeCauseChain(normalModificationFailure)
            + "; freshPrice=" + freshPrice + "; freshRead=" + describeCauseChain(freshReadFailure));
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testInterruptedChangeSubscriptionAttachmentCanRetry() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1);
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(orderDetail);
    transaction.commit();

    CDOObject cdoObject = CDOUtil.getCDOObject(orderDetail);
    CDOChangeSubscriptionAdapter adapter = new CDOChangeSubscriptionAdapter(transaction);
    AtomicBoolean modifyingCallbackEntered = new AtomicBoolean();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
      {
        modifyingCallbackEntered.set(true);
        adapter.attach(object);
      }
    });

    AtomicReference<Throwable> workerFailure = new AtomicReference<>();
    Thread worker = new Thread(() -> {
      Thread.currentThread().interrupt();

      try
      {
        orderDetail.setPrice(2);
      }
      catch (Throwable failure)
      {
        workerFailure.set(failure);
      }
    }, "interrupted-subscription-attachment");
    worker.start();
    worker.join();

    assertNotNull("The interrupted subscription request must propagate its exception", workerFailure.get());
    assertTrue("Expected an interruption-related cause: " + describeCauseChain(workerFailure.get()), hasCause(workerFailure.get(), InterruptedException.class));
    assertTrue("The modifyingObject callback must reach subscription attachment", modifyingCallbackEntered.get());
    assertFalse("Failed attachment must be compensated on the object", orderDetail.eAdapters().contains(adapter));
    assertFalse("Failed attachment must not remain in adapter bookkeeping", adapter.getNotifiers().contains(cdoObject));
    assertEquals(1.0f, orderDetail.getPrice());
    assertEquals(CDOState.CLEAN, cdoObject.cdoState());
    assertFalse(transaction.isDirty());
    assertTrue(transaction.getDirtyObjects().isEmpty());
    assertTrue(transaction.getRevisionDeltas().isEmpty());

    orderDetail.setPrice(3);

    assertTrue("A later attachment must succeed", orderDetail.eAdapters().contains(adapter));
    assertTrue(adapter.getNotifiers().contains(cdoObject));
    transaction.commit();
    adapter.dispose();

    assertFalse("Disposal must remove the successful attachment", orderDetail.eAdapters().contains(adapter));
    assertFalse("Disposal must clear notifier bookkeeping", adapter.getNotifiers().contains(cdoObject));
  }

  private String describeTransactionState(CDOTransaction transaction, CDOObject cdoObject, OrderDetail orderDetail)
  {
    return "price=" + orderDetail.getPrice() + ", state=" + cdoObject.cdoState() + ", dirty=" + transaction.isDirty() + ", dirtyObjects="
        + transaction.getDirtyObjects().keySet() + ", revisionDeltas=" + transaction.getRevisionDeltas().keySet();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testThrowingModifyingHandlerOnCleanObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1);
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(orderDetail);
    transaction.commit();

    CDOObject cdoObject = CDOUtil.getCDOObject(orderDetail);
    IllegalStateException expectedFailure = new IllegalStateException("modifyingObject veto");
    transaction.addTransactionHandler(new FailingModifyingObjectHandler(cdoObject, expectedFailure));

    Throwable failure = captureFailure(() -> orderDetail.setPrice(2));

    assertSame(expectedFailure, failure);
    assertEquals(1.0f, orderDetail.getPrice());
    assertEquals(CDOState.CLEAN, cdoObject.cdoState());
    assertEquals(false, transaction.isDirty());
    assertTrue(transaction.getDirtyObjects().isEmpty());
    assertTrue(transaction.getRevisionDeltas().isEmpty());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testThrowingModifyingHandlerOnDirtyObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1);
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(orderDetail);
    transaction.commit();

    orderDetail.setPrice(2);
    CDOObject cdoObject = CDOUtil.getCDOObject(orderDetail);
    String previousDelta = transaction.getRevisionDeltas().get(cdoObject.cdoID()).toString();
    IllegalStateException expectedFailure = new IllegalStateException("modifyingObject veto");
    transaction.addTransactionHandler(new FailingModifyingObjectHandler(cdoObject, expectedFailure));

    Throwable failure = captureFailure(() -> orderDetail.setPrice(3));

    assertSame(expectedFailure, failure);
    assertEquals(2.0f, orderDetail.getPrice());
    assertEquals(CDOState.DIRTY, cdoObject.cdoState());
    assertEquals(true, transaction.isDirty());
    assertSame(cdoObject, transaction.getDirtyObjects().get(cdoObject.cdoID()));
    assertEquals(previousDelta, transaction.getRevisionDeltas().get(cdoObject.cdoID()).toString());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testThrowingModifyingHandlerOnNewObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1);
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(orderDetail);

    CDOObject cdoObject = CDOUtil.getCDOObject(orderDetail);
    String newObjects = transaction.getNewObjects().keySet().toString();
    String dirtyObjects = transaction.getDirtyObjects().keySet().toString();
    String revisionDeltas = transaction.getRevisionDeltas().toString();
    boolean dirty = transaction.isDirty();
    IllegalStateException expectedFailure = new IllegalStateException("modifyingObject veto");
    transaction.addTransactionHandler(new FailingModifyingObjectHandler(cdoObject, expectedFailure));

    Throwable failure = captureFailure(() -> orderDetail.setPrice(2));

    assertSame(expectedFailure, failure);
    assertEquals(1.0f, orderDetail.getPrice());
    assertEquals(CDOState.NEW, cdoObject.cdoState());
    assertEquals(dirty, transaction.isDirty());
    assertEquals(newObjects, transaction.getNewObjects().keySet().toString());
    assertEquals(dirtyObjects, transaction.getDirtyObjects().keySet().toString());
    assertEquals(revisionDeltas, transaction.getRevisionDeltas().toString());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testReentrantModifyingHandlerOnSameObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    Address address = getModel1Factory().createAddress();
    address.setCity("before");
    address.setName("before");
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOObject cdoObject = CDOUtil.getCDOObject(address);
    transaction.addTransactionHandler(new ReentrantModifyingObjectHandler(cdoObject, "city", () -> address.setName("nested"), null));

    address.setCity("outer");

    assertEquals("outer", address.getCity());
    assertEquals("nested", address.getName());
    assertEquals(CDOState.DIRTY, cdoObject.cdoState());
    assertEquals(2, transaction.getRevisionDeltas().get(cdoObject.cdoID()).getFeatureDeltas().size());
    transaction.commit();

    CDOTransaction freshTransaction = session.openTransaction();
    Address freshAddress = (Address)freshTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    assertEquals("outer", freshAddress.getCity());
    assertEquals("nested", freshAddress.getName());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testThrowingReentrantModifyingHandlerPreservesNestedWrite() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    Address address = getModel1Factory().createAddress();
    address.setCity("before");
    address.setName("before");
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOObject cdoObject = CDOUtil.getCDOObject(address);
    IllegalStateException expectedFailure = new IllegalStateException("outer modifyingObject veto");
    transaction.addTransactionHandler(new ReentrantModifyingObjectHandler(cdoObject, "city", () -> address.setName("nested"), expectedFailure));

    Throwable failure = captureFailure(() -> address.setCity("rejected"));

    assertSame(expectedFailure, failure);
    assertEquals("before", address.getCity());
    assertEquals("nested", address.getName());
    assertEquals(CDOState.DIRTY, cdoObject.cdoState());
    assertEquals(true, transaction.isDirty());
    assertSame(cdoObject, transaction.getDirtyObjects().get(cdoObject.cdoID()));
    assertEquals(1, transaction.getRevisionDeltas().get(cdoObject.cdoID()).getFeatureDeltas().size());
    assertEquals("name", transaction.getRevisionDeltas().get(cdoObject.cdoID()).getFeatureDeltas().get(0).getFeature().getName());

    transaction.commit();
    CDOTransaction freshTransaction = session.openTransaction();
    Address freshAddress = (Address)freshTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    assertEquals("before", freshAddress.getCity());
    assertEquals("nested", freshAddress.getName());
  }

  private Throwable captureFailure(ThrowingRunnable operation)
  {
    try
    {
      operation.run();
      return null;
    }
    catch (Throwable failure)
    {
      return failure;
    }
  }

  private boolean hasCause(Throwable failure, Class<? extends Throwable> type)
  {
    for (Throwable cause = failure; cause != null; cause = cause.getCause())
    {
      if (type.isInstance(cause))
      {
        return true;
      }
    }

    return false;
  }

  private String describeCauseChain(Throwable failure)
  {
    if (failure == null)
    {
      return "<none>";
    }

    StringBuilder builder = new StringBuilder();
    for (Throwable cause = failure; cause != null; cause = cause.getCause())
    {
      if (builder.length() != 0)
      {
        builder.append(" <- ");
      }

      builder.append(cause.getClass().getName()).append(": ").append(cause.getMessage());
    }

    return builder.toString();
  }

  private static final class FailingModifyingObjectHandler extends CDODefaultTransactionHandler
  {
    private final CDOObject object;

    private final RuntimeException failure;

    public FailingModifyingObjectHandler(CDOObject object, RuntimeException failure)
    {
      this.object = object;
      this.failure = failure;
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      if (object == this.object)
      {
        throw failure;
      }
    }
  }

  private static final class ReentrantModifyingObjectHandler extends CDODefaultTransactionHandler
  {
    private final CDOObject object;

    private final String triggerFeature;

    private final Runnable nestedModification;

    private final RuntimeException failure;

    private final AtomicBoolean invoked = new AtomicBoolean();

    public ReentrantModifyingObjectHandler(CDOObject object, String triggerFeature, Runnable nestedModification, RuntimeException failure)
    {
      this.object = object;
      this.triggerFeature = triggerFeature;
      this.nestedModification = nestedModification;
      this.failure = failure;
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      if (object == this.object && triggerFeature.equals(featureDelta.getFeature().getName()) && invoked.compareAndSet(false, true))
      {
        nestedModification.run();
        if (failure != null)
        {
          throw failure;
        }
      }
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable
  {
    void run() throws Throwable;
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testMergeLocalChangesPerFeature_Basic() throws Exception
  {
    Address address = getModel1Factory().createAddress();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);

    address2.setCity("OTTAWA");

    address.setName("NAME1");

    // Resolver should be triggered.
    commitAndSync(transaction, transaction2);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  // Does not work in legacy as long as there is not getter interception
  public void testMergeLocalChangesPerFeature_BasicException() throws Exception
  {
    Address address = getModel1Factory().createAddress();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    final Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);

    address2.setCity("OTTAWA");

    address.setCity("NAME1");
    commitAndSync(transaction, transaction2);

    assertEquals(true, transaction2.hasConflict());
    assertEquals(true, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals("OTTAWA", address2.getCity());
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testCDOMergingConflictResolver() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Address address = getModel1Factory().createAddress();
    transaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(address);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());

    Address address2 = (Address)transaction2.getOrCreateResource(getResourcePath("/res1")).getContents().get(0);
    address2.setCity("OTTAWA");

    address.setName("NAME1");

    // Resolver should be triggered.
    commitAndSync(transaction, transaction2);

    assertEquals(false, CDOUtil.getCDOObject(address2).cdoConflict());
    assertEquals(false, transaction2.hasConflict());

    assertEquals("NAME1", address2.getName());
    assertEquals("OTTAWA", address2.getCity());

    transaction2.commit();
  }

  public void testMergeLocalChangesPerFeature_Bug1() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    contents1.add(getModel1Factory().createAddress());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    // ----------------------------
    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);
  }

  public void testMergeLocalChangesPerFeature_Bug2() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    contents1.add(getModel1Factory().createAddress());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction1, transaction2);
    commitAndSync(transaction2, transaction1);

    contents1.add(getModel1Factory().createAddress());
    contents2.add(getModel1Factory().createAddress());

    // Resolver should be triggered.
    commitAndSync(transaction2, transaction1);
    commitAndSync(transaction1, transaction2);
  }

  public void testMergeLocalChangesPerFeature_Bug3() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    CDOResource resource1 = transaction1.getOrCreateResource(getResourcePath("/res1"));
    EList<EObject> contents1 = resource1.getContents();
    transaction1.commit();
    contents1.add(getModel1Factory().createAddress());
    contents1.add(getModel1Factory().createAddress());

    // ----------------------------

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/res1"));
    EList<EObject> contents2 = resource2.getContents();
    contents2.add(getModel1Factory().createAddress());
    contents2.remove(0);

    // ----------------------------

    CDOTransaction transaction3 = session.openTransaction();
    transaction3.options().addConflictResolver(createConflictResolver());
    CDOResource resource3 = transaction3.getOrCreateResource(getResourcePath("/res1"));
    EList<EObject> contents3 = resource3.getContents();
    contents3.add(getModel1Factory().createAddress());
    contents3.add(getModel1Factory().createAddress());

    // Resolvers should be triggered.
    commitAndSync(transaction3, transaction2, transaction1);
    commitAndSync(transaction2, transaction1, transaction3);
    commitAndSync(transaction1, transaction2, transaction3);
  }

  public void testMerge_ManyValue() throws Exception
  {
    CDOSession session = openSession();

    // CLIENT-1 creates sales order
    CDOTransaction transaction1 = session.openTransaction();
    transaction1.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents1 = transaction1.getOrCreateResource(getResourcePath("/res1")).getContents();

    SalesOrder salesOrder1 = getModel1Factory().createSalesOrder();
    EList<OrderDetail> orderDetails1 = salesOrder1.getOrderDetails();

    contents1.add(salesOrder1);
    transaction1.commit();

    // CLIENT-2 loads sales order
    CDOTransaction transaction2 = session.openTransaction();
    transaction2.options().addConflictResolver(createConflictResolver());
    EList<EObject> contents2 = transaction2.getOrCreateResource(getResourcePath("/res1")).getContents();

    SalesOrder salesOrder2 = (SalesOrder)contents2.get(0);
    EList<OrderDetail> orderDetails2 = salesOrder2.getOrderDetails();

    // CLIENT-1 adds order detail
    OrderDetail orderDetail1 = getModel1Factory().createOrderDetail();
    orderDetails1.add(orderDetail1);

    // CLIENT-2 adds order detail
    OrderDetail orderDetail2 = getModel1Factory().createOrderDetail();
    orderDetails2.add(orderDetail2);

    // CLIENT-1 commits and waits for CLIENT-2's conflict resolver
    commitAndSync(transaction1, transaction2);

    // CLIENT-2 commits and waits for CLIENT-1's conflict resolver (nothing to do there)
    commitAndSync(transaction2, transaction1);

    assertEquals(2, orderDetails1.size());
    assertEquals(CDOUtil.getCDOObject(orderDetail1).cdoID(), CDOUtil.getCDOObject(orderDetails1.get(0)).cdoID());
    assertEquals(CDOUtil.getCDOObject(orderDetail2).cdoID(), CDOUtil.getCDOObject(orderDetails1.get(1)).cdoID());
  }

  public void testMergeLocalSetAndRemoteRemove() throws Exception
  {
    testMergeSetAndRemove(true);
  }

  public void testMergeLocalRemoveAndRemoteSet() throws Exception
  {
    testMergeSetAndRemove(false);
  }

  public void testMergeLocalMoveAndRemoteAdd() throws Exception
  {
    assertMerge(category -> category.getTopProducts().move(1, 3), category -> category.getTopProducts().add(2, category.getProducts().get(5)),
        new Type[] { Type.MOVE }, new Type[] { Type.ADD }, "A", "D", "B", "X", "C", "E");
  }

  public void testMergeLocalMoveAndRemoteRemove() throws Exception
  {
    assertMerge(category -> category.getTopProducts().move(1, 3), category -> category.getTopProducts().remove(1), new Type[] { Type.MOVE },
        new Type[] { Type.REMOVE }, "A", "D", "C", "E");
  }

  public void testMergeLocalMoveAndRemoteMoveDifferentElements() throws Exception
  {
    assertMerge(category -> category.getTopProducts().move(1, 3), category -> category.getTopProducts().move(2, 4), new Type[] { Type.MOVE },
        new Type[] { Type.MOVE }, "A", "D", "B", "E", "C");
  }

  public void testMergeLocalMoveAndRemoteMoveSameElement() throws Exception
  {
    assertMerge(category -> category.getTopProducts().move(1, 3), category -> category.getTopProducts().move(4, 3), new Type[] { Type.MOVE },
        new Type[] { Type.MOVE }, "A", "D", "B", "C", "E");
  }

  public void testMergeMultipleLocalMovesAndRemoteAdd() throws Exception
  {
    assertMerge(category -> {
      category.getTopProducts().move(1, 4);
      category.getTopProducts().move(2, 4);
    }, category -> category.getTopProducts().add(2, category.getProducts().get(5)), new Type[] { Type.MOVE, Type.MOVE }, new Type[] { Type.ADD }, "A", "E", "D",
        "B", "X", "C");
  }

  public void testMergeMultipleLocalMovesOfSameElementAndRemoteAdd() throws Exception
  {
    // The local moves leave D between B and C. The independent remote change only prepends X.
    assertMerge(category -> {
      category.getTopProducts().move(1, 3);
      category.getTopProducts().move(2, 1);
    }, category -> category.getTopProducts().add(0, category.getProducts().get(5)), new Type[] { Type.MOVE, Type.MOVE }, new Type[] { Type.ADD },
        new int[][] { { 3, 1 }, { 1, 2 } }, "X", "A", "B", "D", "C", "E");
  }

  public void testMergeLocalAddAndMultipleRemoteMovesOfSameElement() throws Exception
  {
    assertMerge(category -> category.getTopProducts().add(0, category.getProducts().get(5)), category -> {
      category.getTopProducts().move(1, 3);
      category.getTopProducts().move(2, 1);
    }, new Type[] { Type.ADD }, new Type[] { Type.MOVE, Type.MOVE }, "X", "A", "B", "D", "C", "E");
  }

  public void testMergeMultipleLocalAndRemoteMoves() throws Exception
  {
    assertMerge(category -> {
      category.getTopProducts().move(1, 4);
      category.getTopProducts().move(2, 4);
    }, category -> {
      category.getTopProducts().move(4, 1);
      category.getTopProducts().move(3, 0);
    }, new Type[] { Type.MOVE, Type.MOVE }, new Type[] { Type.MOVE, Type.MOVE }, "E", "D", "C", "A", "B");
  }

  public void testMergeLocalMoveAndRemoteSet() throws Exception
  {
    // Placement and replacement are orthogonal: the remote replacement inherits D's locally moved placement.
    assertMerge(category -> category.getTopProducts().move(1, 3), category -> category.getTopProducts().set(3, category.getProducts().get(5)),
        new Type[] { Type.MOVE }, new Type[] { Type.SET }, "A", "X", "B", "C", "E");
  }

  public void testMergeLocalClearAndRemoteAdd() throws Exception
  {
    // OBSERVED_REMOVE CLEAR semantics remove the ancestor occurrences but preserve the concurrent, unobserved ADD.
    assertMerge(category -> category.getTopProducts().clear(), category -> category.getTopProducts().add(0, category.getProducts().get(5)),
        new Type[] { Type.CLEAR }, new Type[] { Type.ADD }, "X");
  }

  public void testMergeLocalClearAndRemoteMove() throws Exception
  {
    // CLEAR observed every ancestor occurrence, including B, and therefore removes it despite its concurrent MOVE.
    assertMerge(category -> category.getTopProducts().clear(), category -> category.getTopProducts().move(4, 1), new Type[] { Type.CLEAR },
        new Type[] { Type.MOVE });
  }

  public void testMergeConcurrentEquivalentAddsToUniqueList() throws Exception
  {
    // The ADDs create distinct semantic occurrences, but hard feature uniqueness coalesces their equivalent values.
    assertMerge(category -> category.getTopProducts().add(2, category.getProducts().get(5)),
        category -> category.getTopProducts().add(2, category.getProducts().get(5)), new Type[] { Type.ADD }, new Type[] { Type.ADD }, "A", "B", "X", "C", "D",
        "E");
  }

  public void testMergeDistinctEqualValuedOccurrencesInNonUniqueList() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction remoteTransaction = session.openTransaction();

    PolygonWithDuplicates remotePolygon = getModel3Factory().createPolygonWithDuplicates();
    remotePolygon.getPoints().add(new Point(1, 1));
    remotePolygon.getPoints().add(new Point(1, 1));
    remotePolygon.getPoints().add(new Point(2, 2));
    remoteTransaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(remotePolygon);
    remoteTransaction.commit();

    CDOTransaction localTransaction = session.openTransaction();
    PolygonWithDuplicates localPolygon = (PolygonWithDuplicates)localTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    localPolygon.getPoints().move(2, 1);
    remotePolygon.getPoints().remove(0);
    localTransaction.options().addConflictResolver(new CDOMergingConflictResolver());

    commitAndSync(remoteTransaction, localTransaction);

    // REMOVE addresses A0 while MOVE preserves A1; equal values never collapse occurrence identity for unique=false.
    assertEquals(Arrays.asList(new Point(2, 2), new Point(1, 1)), localPolygon.getPoints());
  }

  public void testMergeRemoteUnsetAndCompatibleLocalRemove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction remoteTransaction = session.openTransaction();

    Class1 remoteOwner = getModel3Factory().createClass1();
    Class2 first = SubpackageFactory.eINSTANCE.createClass2();
    Class2 second = SubpackageFactory.eINSTANCE.createClass2();
    remoteOwner.getClass2().add(first);
    remoteOwner.getClass2().add(second);

    EList<EObject> contents = remoteTransaction.getOrCreateResource(getResourcePath("/res1")).getContents();
    contents.add(remoteOwner);
    contents.add(first);
    contents.add(second);
    remoteTransaction.commit();

    CDOTransaction localTransaction = session.openTransaction();
    Class1 localOwner = (Class1)localTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    localOwner.getClass2().remove(0);
    remoteOwner.unsetClass2();
    localTransaction.options().addConflictResolver(new CDOMergingConflictResolver());

    commitAndSync(remoteTransaction, localTransaction);

    // UNSET [] satisfies the local removal as well; no concurrent surviving mutation requires policy resolution.
    assertTrue(localOwner.getClass2().isEmpty());
    assertFalse(localOwner.isSetClass2());
  }

  public void testMergeUnsetWinsConcurrentAdd() throws Exception
  {
    testMergeUnsetAndConcurrentAdd(DefaultCDOMerger.PerFeature.ManyValued.UnsetSemanticPolicy.UNSET_WINS, false);
  }

  public void testMergeUnsetAsClearConcurrentAdd() throws Exception
  {
    testMergeUnsetAndConcurrentAdd(DefaultCDOMerger.PerFeature.ManyValued.UnsetSemanticPolicy.MERGE_AS_CLEAR, true);
  }

  private void testMergeUnsetAndConcurrentAdd(DefaultCDOMerger.PerFeature.ManyValued.UnsetSemanticPolicy policy, boolean expectAdded) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction remoteTransaction = session.openTransaction();

    Class1 remoteOwner = getModel3Factory().createClass1();
    Class2 ancestorElement = SubpackageFactory.eINSTANCE.createClass2();
    Class2 concurrentElement = SubpackageFactory.eINSTANCE.createClass2();
    remoteOwner.getClass2().add(ancestorElement);

    EList<EObject> contents = remoteTransaction.getOrCreateResource(getResourcePath("/res1")).getContents();
    contents.add(remoteOwner);
    contents.add(ancestorElement);
    contents.add(concurrentElement);
    remoteTransaction.commit();

    CDOTransaction localTransaction = session.openTransaction();
    Class1 localOwner = (Class1)localTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    Class2 localConcurrentElement = (Class2)localTransaction.getObject(CDOUtil.getCDOObject(concurrentElement).cdoID());
    localOwner.getClass2().add(localConcurrentElement);
    remoteOwner.unsetClass2();
    localTransaction.options().addConflictResolver(new CDOMergingConflictResolver(new PolicyManyValuedMerger(policy)));

    commitAndSync(remoteTransaction, localTransaction);

    if (expectAdded)
    {
      assertEquals(Arrays.asList(localConcurrentElement), localOwner.getClass2());
      assertTrue(localOwner.isSetClass2());
    }
    else
    {
      assertTrue(localOwner.getClass2().isEmpty());
      assertFalse(localOwner.isSetClass2());
    }
  }

  private void testMergeSetAndRemove(boolean setLocally) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction remoteTransaction = session.openTransaction();

    Category remoteCategory = getModel1Factory().createCategory();
    Product1 originalProduct = getModel1Factory().createProduct1();
    originalProduct.setName("original");
    Product1 remoteReplacementProduct = getModel1Factory().createProduct1();
    remoteReplacementProduct.setName("replacement");
    remoteCategory.getProducts().add(originalProduct);
    remoteCategory.getProducts().add(remoteReplacementProduct);
    remoteCategory.getTopProducts().add(originalProduct);
    remoteTransaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(remoteCategory);
    remoteTransaction.commit();

    CDOTransaction localTransaction = session.openTransaction();
    Category localCategory = (Category)localTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    Product1 localReplacementProduct = localCategory.getProducts().get(1);

    Type targetType;
    Type sourceType;

    if (setLocally)
    {
      localCategory.getTopProducts().set(0, localReplacementProduct);
      remoteCategory.getTopProducts().remove(0);
      targetType = Type.SET;
      sourceType = Type.REMOVE;
    }
    else
    {
      localCategory.getTopProducts().remove(0);
      remoteCategory.getTopProducts().set(0, remoteReplacementProduct);
      targetType = Type.REMOVE;
      sourceType = Type.SET;
    }

    DeltaCheckingManyValuedMerger merger = new DeltaCheckingManyValuedMerger(targetType, sourceType);
    localTransaction.options().addConflictResolver(new CDOMergingConflictResolver(merger));

    // The conflict resolver passes local changes as target and remote changes as source.
    commitAndSync(remoteTransaction, localTransaction);
    assertTrue(merger.isChecked());

    // Both changes remove the original product, while SET also inserts its replacement.
    assertEquals(1, localCategory.getTopProducts().size());
    assertEquals("replacement", localCategory.getTopProducts().get(0).getName());
  }

  private void assertMerge(ListOperation localOperation, ListOperation remoteOperation, Type[] expectedTargetTypes, Type[] expectedSourceTypes,
      String... expectedNames) throws Exception
  {
    assertMerge(localOperation, remoteOperation, expectedTargetTypes, expectedSourceTypes, null, expectedNames);
  }

  private void assertMerge(ListOperation localOperation, ListOperation remoteOperation, Type[] expectedTargetTypes, Type[] expectedSourceTypes,
      int[][] expectedTargetMovePositions, String... expectedNames) throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction remoteTransaction = session.openTransaction();

    Category remoteCategory = getModel1Factory().createCategory();

    for (String name : new String[] { "A", "B", "C", "D", "E", "X" })
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName(name);
      remoteCategory.getProducts().add(product);
    }

    remoteCategory.getTopProducts().addAll(remoteCategory.getProducts().subList(0, 5));
    remoteTransaction.getOrCreateResource(getResourcePath("/res1")).getContents().add(remoteCategory);
    remoteTransaction.commit();

    CDOTransaction localTransaction = session.openTransaction();
    Category localCategory = (Category)localTransaction.getResource(getResourcePath("/res1")).getContents().get(0);
    localOperation.apply(localCategory);
    remoteOperation.apply(remoteCategory);

    DeltaCheckingManyValuedMerger merger = new DeltaCheckingManyValuedMerger(expectedTargetTypes, expectedSourceTypes, expectedTargetMovePositions);
    localTransaction.options().addConflictResolver(new CDOMergingConflictResolver(merger));

    // The conflict resolver passes local changes as target and remote changes as source.
    commitAndSync(remoteTransaction, localTransaction);
    assertTrue(merger.isChecked());

    List<String> actualNames = new ArrayList<>();
    for (Product1 product : localCategory.getTopProducts())
    {
      actualNames.add(product.getName());
    }

    assertEquals(Arrays.asList(expectedNames), actualNames);
  }

  protected CDOConflictResolver createConflictResolver()
  {
    return new CDOMergingConflictResolver();
  }

  /**
   * @author Eike Stepper
   */
  private interface ListOperation
  {
    public void apply(Category category);
  }

  /**
   * @author Eike Stepper
   */
  private static final class PolicyManyValuedMerger extends DefaultCDOMerger.PerFeature.ManyValued
  {
    public PolicyManyValuedMerger(UnsetSemanticPolicy unsetSemanticPolicy)
    {
      super(ResolutionPreference.NONE, OccurrenceConflictPolicy.DEFAULT, OrderingPolicy.STABLE, DuplicateResolutionPolicy.COALESCE,
          ClearSemanticPolicy.OBSERVED_REMOVE, unsetSemanticPolicy);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DeltaCheckingManyValuedMerger extends DefaultCDOMerger.PerFeature.ManyValued
  {
    private final Type[] expectedTargetTypes;

    private final Type[] expectedSourceTypes;

    private final int[][] expectedTargetMovePositions;

    private boolean checked;

    public DeltaCheckingManyValuedMerger(Type expectedTargetType, Type expectedSourceType)
    {
      this(new Type[] { expectedTargetType }, new Type[] { expectedSourceType });
    }

    public DeltaCheckingManyValuedMerger(Type[] expectedTargetTypes, Type[] expectedSourceTypes)
    {
      this(expectedTargetTypes, expectedSourceTypes, null);
    }

    public DeltaCheckingManyValuedMerger(Type[] expectedTargetTypes, Type[] expectedSourceTypes, int[][] expectedTargetMovePositions)
    {
      this.expectedTargetTypes = expectedTargetTypes;
      this.expectedSourceTypes = expectedSourceTypes;
      this.expectedTargetMovePositions = expectedTargetMovePositions;
    }

    @Override
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta, CDORevision ancestorRevision)
    {
      List<CDOFeatureDelta> targetDeltas = ((CDOListFeatureDelta)targetFeatureDelta).getListChanges();
      List<CDOFeatureDelta> sourceDeltas = ((CDOListFeatureDelta)sourceFeatureDelta).getListChanges();
      assertEquals(expectedTargetTypes.length, targetDeltas.size());
      assertEquals(expectedSourceTypes.length, sourceDeltas.size());

      for (int i = 0; i < expectedTargetTypes.length; i++)
      {
        assertEquals(expectedTargetTypes[i], targetDeltas.get(i).getType());
      }

      for (int i = 0; i < expectedSourceTypes.length; i++)
      {
        assertEquals(expectedSourceTypes[i], sourceDeltas.get(i).getType());
      }

      if (expectedTargetMovePositions != null)
      {
        assertEquals(expectedTargetMovePositions.length, targetDeltas.size());

        for (int i = 0; i < expectedTargetMovePositions.length; i++)
        {
          CDOMoveFeatureDelta moveDelta = (CDOMoveFeatureDelta)targetDeltas.get(i);
          assertEquals(expectedTargetMovePositions[i][0], moveDelta.getOldPosition());
          assertEquals(expectedTargetMovePositions[i][1], moveDelta.getNewPosition());
        }
      }

      checked = true;
      return super.changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta, ancestorRevision);
    }

    private boolean isChecked()
    {
      return checked;
    }
  }
}
