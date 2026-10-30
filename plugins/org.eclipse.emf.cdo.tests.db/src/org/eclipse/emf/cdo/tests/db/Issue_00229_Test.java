/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exercises concurrent CDO commits while schema preflight creates a mapping table.
 * <p>
 * One commit writes many instances of already initialized classes so the store uses its normal commit-wide JDBC
 * batching. At the same time, another commit creates an instance of a class that has never been mapped and therefore
 * needs a new table through the store's ordinary schema-preflight path.
 * <p>
 * Both commits are released from a start barrier together, and the large batched write increases the opportunity for
 * overlap. The test has no production hook to pause either commit at a specific schema-lock transition, so it exercises
 * concurrent contention without claiming to force a particular queue interleaving.
 *
 * @author Eike Stepper
 */
@Requires(DBConfig.CAPABILITY)
@CleanRepositoriesBefore(reason = "Issue 229")
@CleanRepositoriesAfter(reason = "Issue 229")
public class Issue_00229_Test extends AbstractCDOTest
{
  private static final String NS_URI = "http://www.eclipse.org/emf/CDO/tests/issues/229";

  private static final Object PREVIOUS_REGISTRY_ENTRY = EPackage.Registry.INSTANCE.get(NS_URI);

  private static final EPackage MODEL = createModel();

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    EPackage.Registry.INSTANCE.put(NS_URI, MODEL);
    properties.put(RepositoryConfig.PROP_TEST_INITIAL_PACKAGES, new EPackage[] { MODEL });
  }

  @Override
  protected void doTearDown() throws Exception
  {
    try
    {
      super.doTearDown();
    }
    finally
    {
      Object currentEntry = EPackage.Registry.INSTANCE.get(NS_URI);
      if (currentEntry == MODEL || currentEntry == null)
      {
        if (PREVIOUS_REGISTRY_ENTRY == null)
        {
          EPackage.Registry.INSTANCE.remove(NS_URI);
        }
        else
        {
          EPackage.Registry.INSTANCE.put(NS_URI, PREVIOUS_REGISTRY_ENTRY);
        }
      }
    }
  }

  public void testConcurrentSchemaPreparationAndBatchedCommit() throws Exception
  {
    String batchedPath = getResourcePath("/batched"); //$NON-NLS-1$
    String schemaPath = getResourcePath("/schema"); //$NON-NLS-1$
    CDOSession initialSession = openSession();

    try
    {
      CDOTransaction transaction = initialSession.openTransaction();
      CDOResource resource = transaction.createResource(batchedPath);
      EObject initialItem = create("Item"); //$NON-NLS-1$
      resource.getContents().add(initialItem);
      transaction.commit();
    }
    finally
    {
      initialSession.close();
    }

    CDOSession batchingSession = openSession();
    CDOSession schemaSession = openSession();
    CDOTransaction batchingTransaction = batchingSession.openTransaction();
    CDOTransaction schemaTransaction = schemaSession.openTransaction();
    CDOResource batchingResource = batchingTransaction.getResource(batchedPath);

    for (int i = 0; i < 40; i++)
    {
      EObject item = create("Item"); //$NON-NLS-1$
      EList<EObject> children = children(item);

      for (int j = 0; j < 20; j++)
      {
        EObject child = create("Child"); //$NON-NLS-1$
        child.eSet(child.eClass().getEStructuralFeature("value"), i * 20 + j); //$NON-NLS-1$
        children.add(child);
      }

      batchingResource.getContents().add(item);
    }

    EObject unseen = create("Unseen"); //$NON-NLS-1$
    unseen.eSet(unseen.eClass().getEStructuralFeature("name"), "created concurrently"); //$NON-NLS-1$
    schemaTransaction.createResource(schemaPath).getContents().add(unseen);

    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread batchingCommit = commitThread("batched-commit", batchingTransaction, ready, start, failure); //$NON-NLS-1$
    Thread schemaCommit = commitThread("schema-commit", schemaTransaction, ready, start, failure); //$NON-NLS-1$

    try
    {
      batchingCommit.start();
      schemaCommit.start();
      assertTrue("Commit workers did not become ready", ready.await(10, TimeUnit.SECONDS)); //$NON-NLS-1$
      start.countDown();
      batchingCommit.join(60000L);
      schemaCommit.join(60000L);

      assertFalse("Batched commit did not finish", batchingCommit.isAlive()); //$NON-NLS-1$
      assertFalse("Schema commit did not finish", schemaCommit.isAlive()); //$NON-NLS-1$

      if (failure.get() != null)
      {
        throw new AssertionError("Concurrent commit failed", failure.get()); //$NON-NLS-1$
      }
    }
    finally
    {
      start.countDown();
      cleanupConcurrentSessions(batchingCommit, schemaCommit, batchingSession, schemaSession);
    }

    CDOSession verifySession = openSession();

    try
    {
      CDOTransaction verifyTransaction = verifySession.openTransaction();
      CDOResource verifyBatchedResource = verifyTransaction.getResource(batchedPath);
      assertEquals(41, verifyBatchedResource.getContents().size());

      CDOResource verifySchemaResource = verifyTransaction.getResource(schemaPath);
      assertEquals("created concurrently", //$NON-NLS-1$
          verifySchemaResource.getContents().get(0).eGet(((EClass)MODEL.getEClassifier("Unseen")).getEStructuralFeature("name")));

      EObject followUp = create("Item"); //$NON-NLS-1$
      followUp.eSet(followUp.eClass().getEStructuralFeature("name"), "follow-up"); //$NON-NLS-1$
      verifyBatchedResource.getContents().add(followUp);
      verifyTransaction.commit();
    }
    finally
    {
      verifySession.close();
    }
  }

  private static Thread commitThread(String name, CDOTransaction transaction, CountDownLatch ready, CountDownLatch start, AtomicReference<Throwable> failure)
  {
    Thread thread = new Thread(() -> {
      ready.countDown();

      try
      {
        if (!start.await(10, TimeUnit.SECONDS))
        {
          throw new IllegalStateException("Commit start was not signaled"); //$NON-NLS-1$
        }

        transaction.commit();
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, name);
    thread.setDaemon(true);
    return thread;
  }

  private static void stopCommitThread(Thread thread) throws InterruptedException
  {
    if (thread.isAlive())
    {
      thread.interrupt();
      thread.join(5000L);
    }
  }

  private static void cleanupConcurrentSessions(Thread firstCommit, Thread secondCommit, CDOSession firstSession, CDOSession secondSession)
  {
    Throwable cleanupFailure = null;
    boolean interrupted = false;

    try
    {
      stopCommitThread(firstCommit);
    }
    catch (InterruptedException ex)
    {
      interrupted = true;
      cleanupFailure = ex;
    }

    try
    {
      stopCommitThread(secondCommit);
    }
    catch (InterruptedException ex)
    {
      interrupted = true;
      if (cleanupFailure == null)
      {
        cleanupFailure = ex;
      }
      else
      {
        cleanupFailure.addSuppressed(ex);
      }
    }

    try
    {
      closeSessionBounded(firstSession);
    }
    catch (Throwable ex)
    {
      if (cleanupFailure == null)
      {
        cleanupFailure = ex;
      }
      else
      {
        cleanupFailure.addSuppressed(ex);
      }
    }

    try
    {
      closeSessionBounded(secondSession);
    }
    catch (Throwable ex)
    {
      if (cleanupFailure == null)
      {
        cleanupFailure = ex;
      }
      else
      {
        cleanupFailure.addSuppressed(ex);
      }
    }

    if (interrupted)
    {
      Thread.currentThread().interrupt();
    }

    if (cleanupFailure != null)
    {
      throw new AssertionError("Concurrent test cleanup failed", cleanupFailure); //$NON-NLS-1$
    }
  }

  private static void closeSessionBounded(CDOSession session) throws InterruptedException
  {
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread closer = new Thread(() -> {
      try
      {
        session.close();
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "close-session"); //$NON-NLS-1$
    closer.setDaemon(true);
    closer.start();
    closer.join(5000L);

    if (closer.isAlive())
    {
      throw new AssertionError("Session close did not finish within 5000 ms"); //$NON-NLS-1$
    }

    if (failure.get() != null)
    {
      throw new AssertionError("Session close failed", failure.get()); //$NON-NLS-1$
    }
  }

  @SuppressWarnings("unchecked")
  private static EList<EObject> children(EObject item)
  {
    return (EList<EObject>)item.eGet(item.eClass().getEStructuralFeature("children")); //$NON-NLS-1$
  }

  private static EObject create(String className)
  {
    return MODEL.getEFactoryInstance().create((EClass)MODEL.getEClassifier(className));
  }

  private static EPackage createModel()
  {
    EcoreFactory factory = EcoreFactory.eINSTANCE;
    EPackage model = factory.createEPackage();
    model.setName("issue229"); //$NON-NLS-1$
    model.setNsPrefix("issue229"); //$NON-NLS-1$
    model.setNsURI(NS_URI);

    EClass item = factory.createEClass();
    item.setName("Item"); //$NON-NLS-1$
    model.getEClassifiers().add(item);

    EClass child = factory.createEClass();
    child.setName("Child"); //$NON-NLS-1$
    model.getEClassifiers().add(child);

    EClass unseen = factory.createEClass();
    unseen.setName("Unseen"); //$NON-NLS-1$
    model.getEClassifiers().add(unseen);

    EAttribute itemName = factory.createEAttribute();
    itemName.setName("name"); //$NON-NLS-1$
    itemName.setEType(EcorePackage.Literals.ESTRING);
    item.getEStructuralFeatures().add(itemName);

    EReference children = factory.createEReference();
    children.setName("children"); //$NON-NLS-1$
    children.setEType(child);
    children.setContainment(true);
    children.setUpperBound(-1);
    item.getEStructuralFeatures().add(children);

    EAttribute value = factory.createEAttribute();
    value.setName("value"); //$NON-NLS-1$
    value.setEType(EcorePackage.Literals.EINT);
    child.getEStructuralFeatures().add(value);

    EAttribute unseenName = factory.createEAttribute();
    unseenName.setName("name"); //$NON-NLS-1$
    unseenName.setEType(EcorePackage.Literals.ESTRING);
    unseen.getEStructuralFeatures().add(unseenName);

    return model;
  }
}
