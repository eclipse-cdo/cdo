/*
 * Copyright (c) 2016, 2021, 2024-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Bug 485487.
 *
 * @author Eike Stepper
 */
public class Bugzilla_485487_Test extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private Repository repository;

  private CountDownLatch writeAccessorEntered;

  private volatile boolean cancelCommit;

  @Override
  protected void doSetUp() throws Exception
  {
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
    writeAccessorEntered = new CountDownLatch(1);

    repository = new Repository.Default()
    {
      @Override
      public InternalCommitContext createCommitContext(InternalTransaction transaction)
      {
        return new TransactionCommitContext(transaction)
        {
          @Override
          protected void writeAccessor(OMMonitor monitor)
          {
            super.writeAccessor(monitor);
            if (cancelCommit)
            {
              writeAccessorEntered.countDown();

              while (!monitor.isCanceled())
              {
                ConcurrencyUtil.sleep(10);
              }

              monitor.checkCanceled();
            }
          }
        };
      }
    };

    Map<String, String> props = getRepositoryProperties();
    repository.setProperties(props);

    repository.setName(REPOSITORY_NAME);

    Map<String, Object> map = getTestProperties();
    map.put(RepositoryConfig.PROP_TEST_REPOSITORY, repository);
    map.put(RepositoryConfig.PROP_TEST_ENABLE_SERVER_BROWSER, true);
  }

  @Skips(IConfig.CAPABILITY_SANITIZE_TIMEOUT)
  @CleanRepositoriesBefore(reason = "Isolated repository needed")
  @CleanRepositoriesAfter(reason = "Isolated repository needed")
  public void testCancellationDuringCommit() throws Exception
  {
    disableConsole();

    CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
    session.options().setCommitTimeout(60);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCommitInfoTimeout(1000);
    transaction.createResource(getResourcePath("/test1"));

    msg("--> Commit-1");

    try
    {
      NullProgressMonitor monitor = new NullProgressMonitor();
      cancelCommit = true;
      Thread canceller = new Thread(() -> {
        try
        {
          writeAccessorEntered.await(10, TimeUnit.SECONDS);
          monitor.setCanceled(true);
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
        }
      }, "Bugzilla_485487_Test-Canceller");
      canceller.start();

      transaction.setCommitComment("test1");
      transaction.commit(monitor);
      fail("OperationCanceledException expected");
    }
    catch (OperationCanceledException expected)
    {
      // Expected.
    }
    finally
    {
      cancelCommit = false;
    }

    msg("--> Rollback");

    transaction.rollback();

    transaction.createResource(getResourcePath("/test2"));

    msg("--> Commit-2");
    transaction.setCommitComment("test2");
    transaction.commit();

    CDOView view = session.openView();

    try
    {
      assertNotNull(view.getResource(getResourcePath("/test2")));

      try
      {
        view.getResource(getResourcePath("/test1"));
        fail("Cancelled resource must not exist");
      }
      catch (CDOResourceNodeNotFoundException | InvalidURIException expected)
      {
        // Expected.
      }
    }
    finally
    {
      view.close();
    }
  }
}
