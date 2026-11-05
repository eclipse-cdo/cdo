/*
 * Copyright (c) 2018, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.NonAuditListTableMapping;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.db.DBConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.db.dml.SQLStatement;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bug 396743: [DB] List size column mismatching the row entries.
 * <p>
 * The problem only occurs in non-auditing.
 *
 * @author Eike Stepper
 */
@Skips(IRepositoryConfig.CAPABILITY_AUDITING)
@org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore(reason = "TEST_MAPPING_STRATEGY")
@org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter(reason = "TEST_MAPPING_STRATEGY")
public class Bugzilla_396743_Test extends AbstractCDOTest
{
  private CountDownLatch readValueLatch;

  private CountDownLatch commitLatch;

  private void awaitReadValues()
  {
    if (readValueLatch == null || StoreThreadLocal.getCommitContext() != null)
    {
      // Don't block reads that are triggered from commit processing
      return;
    }

    await(readValueLatch);
  }

  public void testWrongListSizeAdditions() throws Exception
  {
    getTestProperties().put(DBConfig.PROP_TEST_MAPPING_STRATEGY, new HorizontalNonAuditMappingStrategy()
    {
      @Override
      public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
      {
        if (feature == getModel1Package().getCompany_Categories())
        {
          msg("Instrumenting " + feature);
          return new NonAuditListTableMapping(this, containingClass, feature)
          {
            @Override
            protected void bindKeyFields(SQLStatement sql, PreparedStatement stmt, CDORevision revision) throws SQLException
            {
              if (sql.getSQL().toUpperCase().startsWith("SELECT") && revision.getEClass() == getModel1Package().getCompany())
              {
                commitLatch.countDown(); // Let main thread execute a commit
                awaitReadValues(); // Wait for commit to finish
              }

              super.bindKeyFields(sql, stmt, revision);
            }
          };
        }

        return super.doCreateListMapping(containingClass, feature);
      }
    });

    Company company1 = getModel1Factory().createCompany();
    company1.getCategories().add(getModel1Factory().createCategory());

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("res"));
    resource1.getContents().add(company1);
    transaction1.commit();

    // Remove the company revision from the server cache to trigger DBAccessor.readRevision()
    CDORevision revision = CDOUtil.getCDOObject(company1).cdoRevision();
    getRepository().getRevisionManager().getCache().removeRevision(revision.getID(), revision);

    readValueLatch = new CountDownLatch(1);
    commitLatch = new CountDownLatch(1);

    final boolean[] exceptionOccured = { false };
    Thread readerClient = new Thread("Test Reader Client")
    {
      @Override
      public void run()
      {
        CDOSession session2 = openSession();
        CDOTransaction transaction2 = session2.openTransaction();
        CDOResource resource2 = transaction2.getResource(getResourcePath("res"));

        try
        {
          resource2.getContents().get(0); // Will trigger readValues() in the DBStore
        }
        catch (Exception ex)
        {
          exceptionOccured[0] = ex.getMessage().contains("IndexOutOfBoundsException");
        }

        session2.close();
      }
    };

    readerClient.start();
    await(commitLatch);

    company1.getCategories().add(getModel1Factory().createCategory());

    try
    {
      transaction1.commit();
    }
    catch (Exception ex)
    {
      if (ex.getMessage().contains("TimeoutRuntimeException"))
      {
        // The test logic to produce the timing problem causes a deadlock in TransactionCommitContext.lockObjects() if
        // the problem is fixed. This is the success case, i.e., no IndexOutOfBoundsException in
        // AbstractListTableMapping.readValues().
        return;
      }

      throw ex;
    }
    finally
    {
      readValueLatch.countDown();
      readerClient.join(DEFAULT_TIMEOUT);
    }

    // Check that no IndexOutOfBoundsException has been thrown in the reader
    assertEquals("In the reader thread an IndexOutOfBoundsException occured", false, exceptionOccured[0]);
  }

  public void testConcurrentListShrinkDuringDirectRevisionRead() throws Exception
  {
    CountDownLatch listReadStarted = new CountDownLatch(1);
    CountDownLatch commitFinished = new CountDownLatch(1);
    AtomicReference<Thread> readerThread = new AtomicReference<>();

    getTestProperties().put(DBConfig.PROP_TEST_MAPPING_STRATEGY, new HorizontalNonAuditMappingStrategy()
    {
      @Override
      public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
      {
        if (feature == getModel1Package().getCompany_Categories())
        {
          return new NonAuditListTableMapping(this, containingClass, feature)
          {
            @Override
            protected void bindKeyFields(SQLStatement sql, PreparedStatement stmt, CDORevision revision) throws SQLException
            {
              if (Thread.currentThread() == readerThread.get() && listReadStarted.getCount() != 0 && sql.getSQL().toUpperCase().startsWith("SELECT"))
              {
                listReadStarted.countDown();

                try
                {
                  if (!commitFinished.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
                  {
                    throw new SQLException("Timed out waiting for the concurrent commit");
                  }
                }
                catch (InterruptedException ex)
                {
                  Thread.currentThread().interrupt();
                  throw new SQLException(ex);
                }
              }

              super.bindKeyFields(sql, stmt, revision);
            }
          };
        }

        return super.doCreateListMapping(containingClass, feature);
      }
    });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("shrink"));
    Company company = getModel1Factory().createCompany();
    company.setName("before");
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    resource.getContents().add(company);
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(company).cdoID();
    CDOID remainingID = CDOUtil.getCDOObject(company.getCategories().get(1)).cdoID();

    AtomicReference<InternalCDORevision> result = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread reader = new Thread(() -> {
      IDBStoreAccessor accessor = null;

      try
      {
        accessor = (IDBStoreAccessor)getRepository().getStore().getReader(null);
        result.set(accessor.readRevision(id, getRepository().getBranchManager().getMainBranch().getHead(), CDORevision.UNCHUNKED, null));
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        if (accessor != null)
        {
          try
          {
            accessor.release();
          }
          catch (Throwable ex)
          {
            Throwable readFailure = failure.get();
            if (readFailure == null)
            {
              failure.set(ex);
            }
            else
            {
              readFailure.addSuppressed(ex);
            }
          }
        }
      }
    }, "Direct revision reader");
    readerThread.set(reader);
    reader.start();

    try
    {
      assertTrue("Reader did not reach the list query", listReadStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
      company.setName("after");
      company.getCategories().remove(0);
      transaction.commit();
    }
    finally
    {
      commitFinished.countDown();
      reader.join(DEFAULT_TIMEOUT);
    }

    assertFalse("Reader did not finish", reader.isAlive());
    assertNull("Revision read failed: " + failure.get(), failure.get());
    assertNotNull(result.get());
    assertEquals("after", result.get().getValue(getModel1Package().getAddress_Name()));
    assertEquals(1, result.get().getListOrNull(getModel1Package().getCompany_Categories()).size());
    assertEquals(remainingID, result.get().getListOrNull(getModel1Package().getCompany_Categories()).get(0));
  }

  public void testIncompleteListWithoutVersionChangeIsRejected() throws Exception
  {
    AtomicReference<Thread> readerThread = new AtomicReference<>();
    getTestProperties().put(DBConfig.PROP_TEST_MAPPING_STRATEGY, new HorizontalNonAuditMappingStrategy()
    {
      @Override
      public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
      {
        if (feature == getModel1Package().getCompany_Categories())
        {
          return new NonAuditListTableMapping(this, containingClass, feature)
          {
            @Override
            public void readValues(IDBStoreAccessor accessor, InternalCDORevision revision, int listChunk)
            {
              if (Thread.currentThread() != readerThread.get())
              {
                super.readValues(accessor, revision, listChunk);
              }
            }
          };
        }

        return super.doCreateListMapping(containingClass, feature);
      }
    });

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("incomplete"));
    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    resource.getContents().add(company);
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(company).cdoID();

    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread reader = new Thread(() -> {
      IDBStoreAccessor accessor = null;

      try
      {
        accessor = (IDBStoreAccessor)getRepository().getStore().getReader(null);
        accessor.readRevision(id, getRepository().getBranchManager().getMainBranch().getHead(), CDORevision.UNCHUNKED, null);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
      finally
      {
        if (accessor != null)
        {
          accessor.release();
        }
      }
    }, "Incomplete list reader");
    readerThread.set(reader);
    reader.start();
    reader.join(DEFAULT_TIMEOUT);

    assertFalse("Reader did not finish", reader.isAlive());
    assertTrue("Incomplete list was not rejected: " + failure.get(), failure.get() instanceof IllegalStateException);
    assertEquals("A completed CDO list contains a construction slot", failure.get().getMessage());
  }
}
