/*
 * Copyright (c) 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.Map;

/**
 * Bug 485487.
 *
 * @author Eike Stepper
 */
public class Bugzilla_485487_Test extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private Repository repository;

  private boolean forceTimeout;

  @Override
  protected void doSetUp() throws Exception
  {
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
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

            if (forceTimeout)
            {
              ConcurrencyUtil.sleep(2000);
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
  public void testTimeoutDuringCommit() throws Exception
  {
    disableConsole();

    CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
    session.options().setCommitTimeout(1);

    CDOTransaction transaction = session.openTransaction();
    transaction.options().setCommitInfoTimeout(1000);
    transaction.createResource(getResourcePath("/test1"));

    msg("--> Commit-1");

    try
    {
      forceTimeout = true;
      transaction.setCommitComment("test1");
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      assertEquals(true, expected.getMessage().contains("Timeout after"));
    }

    msg("--> Rollback");

    forceTimeout = false;
    transaction.rollback();

    transaction.createResource(getResourcePath("/test2"));

    msg("--> Commit-2");
    transaction.setCommitComment("test2");
    transaction.commit();
  }
}
