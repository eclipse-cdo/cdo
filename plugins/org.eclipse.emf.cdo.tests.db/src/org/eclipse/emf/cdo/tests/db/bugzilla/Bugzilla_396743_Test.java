/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalNonAuditMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.NonAuditListTableMapping;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.db.DBConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.db.IDBPreparedStatement;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

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
    if (readValueLatch == null)
    {
      return;
    }

    if (StoreThreadLocal.getCommitContext() != null)
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
            protected void setKeyFields(PreparedStatement stmt, CDORevision revision) throws SQLException
            {
              if (((IDBPreparedStatement)stmt).getSQL().toUpperCase().startsWith("SELECT") && revision.getEClass() == getModel1Package().getCompany())
              {
                commitLatch.countDown(); // Let main thread execute a commit
                awaitReadValues(); // Wait for commit to finish
              }

              super.setKeyFields(stmt, revision);
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
}
