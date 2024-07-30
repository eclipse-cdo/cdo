/*
 * Copyright (c) 2013, 2014, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import java.util.Map;

/**
 * Bug 411927: CDOSessionImpl can "freeze" during invalidation reordering.
 *
 * @author Eike Stepper
 */
public class Bugzilla_411927_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "res1";

  private static final int CLIENTS = 10;

  private static final int COMMITS_PER_CLIENT = 3;

  private static final String BAD_COMMIT = "Bad Commit";

  public void testWithoutFailure() throws Exception
  {
    executeTestWith(FailureTime.NEVER);
  }

  public void testWithFailureBeforeTimeStamp() throws Exception
  {
    executeTestWith(FailureTime.BEFORE_TIMESTAMP);
  }

  public void testWithFailureAfterTimeStamp() throws Exception
  {
    executeTestWith(FailureTime.AFTER_TIMESTAMP);
  }

  private void initRepository(final FailureTime failureTime)
  {
    Repository repository = new Repository.Default()
    {
      @Override
      public InternalCommitContext createCommitContext(InternalTransaction transaction)
      {
        return new TransactionCommitContext(transaction)
        {
          @Override
          protected void lockObjects() throws InterruptedException
          {
            if (failureTime == FailureTime.BEFORE_TIMESTAMP)
            {
              simulateProblem();
            }

            super.lockObjects();
          }

          @Override
          protected void adjustForCommit()
          {
            if (failureTime == FailureTime.AFTER_TIMESTAMP)
            {
              simulateProblem();
            }

            super.adjustForCommit();
          }

          private void simulateProblem()
          {
            if (BAD_COMMIT.equals(getCommitComment()))
            {
              RuntimeException cause = new RuntimeException("SIMULATED PROBLEM");
              throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_OPTIMISTIC_LOCKING, cause);
            }
          }
        };
      }
    };

    repository.setName(IRepositoryConfig.REPOSITORY_NAME);

    Map<String, String> props = getRepositoryProperties();
    repository.setProperties(props);

    getTestProperties().put(RepositoryConfig.PROP_TEST_REPOSITORY, repository);
  }

  private void executeTestWith(FailureTime failureTime) throws ConcurrentAccessException, CommitException, InterruptedException
  {
    disableConsole();
    initRepository(failureTime);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_NAME));
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    Thread[] clients = new Thread[CLIENTS];
    for (int id = 0; id < CLIENTS; id++)
    {
      if (failureTime == FailureTime.NEVER || id < CLIENTS - 1)
      {
        clients[id] = new GoodClient(id);
      }
      else
      {
        clients[id] = new BadClient(id);
      }
    }

    for (int id = 0; id < CLIENTS; id++)
    {
      clients[id].start();
    }

    for (int id = 0; id < CLIENTS; id++)
    {
      clients[id].join();
    }

    assertEquals("Invalidation queue is blocked", true, session.waitForUpdate(getRepository().getLastCommitTimeStamp(), DEFAULT_TIMEOUT));
  }

  /**
   * @author Eike Stepper
   */
  private enum FailureTime
  {
    NEVER, BEFORE_TIMESTAMP, AFTER_TIMESTAMP
  }

  /**
   * @author Eike Stepper
   */
  private final class GoodClient extends Thread
  {
    private final int id;

    private Category category;

    public GoodClient(int id) throws ConcurrentAccessException, CommitException
    {
      super("GoodClient-" + id);
      this.id = id;
      setDaemon(true);

      category = getModel1Factory().createCategory();
      category.setName(GoodClient.this.getName());

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath(RESOURCE_NAME));
      Company company = (Company)resource.getContents().get(0);

      company.getCategories().add(category);
      transaction.commit();
    }

    @Override
    public void run()
    {
      CDOTransaction transaction = (CDOTransaction)CDOUtil.getCDOObject(category).cdoView();
      int objectsPerCommit = 10 * (CLIENTS - id);

      for (int commit = 0; commit < COMMITS_PER_CLIENT; commit++)
      {
        for (int object = 0; object < objectsPerCommit; object++)
        {
          category.getProducts().add(getModel1Factory().createProduct1());
        }

        try
        {
          transaction.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
          break;
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class BadClient extends Thread
  {
    private Company company;

    public BadClient(int id) throws ConcurrentAccessException, CommitException
    {
      super("BadClient-" + id);
      setDaemon(true);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath(RESOURCE_NAME));
      company = (Company)resource.getContents().get(0);
    }

    @Override
    public void run()
    {
      CDOTransaction transaction = (CDOTransaction)CDOUtil.getCDOObject(company).cdoView();
      for (int commit = 0; commit < 100 * COMMITS_PER_CLIENT; commit++)
      {
        company.setName("Company-" + commit); // Make the transaction dirty

        try
        {
          transaction.setCommitComment(BAD_COMMIT);
          transaction.commit();
        }
        catch (CommitException ex)
        {
          transaction.rollback();
        }
      }
    }
  }
}
