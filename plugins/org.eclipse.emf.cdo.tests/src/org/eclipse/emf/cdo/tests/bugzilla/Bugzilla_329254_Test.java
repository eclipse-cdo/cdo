/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.io.IOUtil;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * LastCommitTimeStamp updated even when a serverSide Error occurred.
 * <p>
 * See bug 329254.
 * 
 * @author Pascal Lehmann
 * @since 4.0
 */
public class Bugzilla_329254_Test extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private boolean modelInitialized;

  private CountDownLatch enterLatch = new CountDownLatch(1);

  private CountDownLatch leaveLatch = new CountDownLatch(1);

  private int sessionId2;

  @Override
  protected void doSetUp() throws Exception
  {
    modelInitialized = false;
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
    Repository repository = new Repository.Default()
    {
      @Override
      public InternalCommitContext createCommitContext(InternalTransaction transaction)
      {
        return new TransactionCommitContext(transaction)
        {
          @Override
          protected void adjustForCommit()
          {
            // ignore all calls before model has been initialized.
            if (modelInitialized)
            {
              IOUtil.OUT().println("AdjustForCommit entered: " + this);
              if (getTransaction().getSession().getSessionID() == sessionId2)
              {
                // grant the other session access to enter and
                // block until it has left again.
                enterLatch.countDown();
                try
                {
                  leaveLatch.await();
                }
                catch (InterruptedException ex)
                {
                  ex.printStackTrace();
                }
              }

              super.adjustForCommit();
              IOUtil.OUT().println("AdjustForCommit left: " + this);
            }
            else
            {
              super.adjustForCommit();
            }
          }
        };
      }
    };

    Map<String, String> props = getRepositoryProperties();
    ((InternalRepository)repository).setProperties(props);

    repository.setName(REPOSITORY_NAME);

    Map<String, Object> map = getTestProperties();
    map.put(RepositoryConfig.PROP_TEST_REPOSITORY, repository);
  }

  public void testCommitTimeStampUpdateOnError() throws Exception
  {
    disableConsole();

    CDOSession session1 = openSession(REPOSITORY_NAME);
    CDOSession session2 = openSession(REPOSITORY_NAME);

    session1.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    session2.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    sessionId2 = session2.getSessionID();

    CDOTransaction transaction1 = session1.openTransaction();
    final CDOTransaction transaction2 = session2.openTransaction();
    final CDOTransaction transaction3 = session1.openTransaction();
    CDOTransaction transaction4 = session2.openTransaction();
    CDOTransaction transaction5 = session1.openTransaction();

    // create initial model.
    CDOResource resource = transaction1.createResource(getResourcePath("/test"));
    Company company1 = getModel1Factory().createCompany();
    company1.setName("company1");
    resource.getContents().add(company1);
    commitAndSync(transaction1, transaction2, transaction3);

    // do concurrent changes on company to produce an error.
    Company company2 = transaction2.getObject(company1);
    company2.setStreet("street1");

    Company company3 = transaction3.getObject(company1);
    company3.setCity("city1");

    Thread commitThread1 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          transaction3.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    Thread commitThread2 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          transaction2.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    commitThread1.start();
    commitThread2.start();

    commitThread1.join();
    commitThread2.join();

    transaction4.waitForUpdate(transaction3.getLastCommitTime(), DEFAULT_TIMEOUT);
    transaction4.waitForUpdate(transaction2.getLastCommitTime(), DEFAULT_TIMEOUT);

    // do another commit.
    Company company4 = transaction4.getObject(company1);
    company4.setName("company2");
    commitAndSync(transaction4, transaction1, transaction5);

    // check if update arrived.
    assertEquals(company4.getName(), company1.getName());

    // check committing on the other session too.
    Company company5 = transaction5.getObject(company1);
    company5.setName("company3");
    commitAndSync(transaction5, transaction4);

    // check if update arrived.
    assertEquals(company5.getName(), company4.getName());

  }

  public void testCommitTimeStampUpdateLongRunningCommitSameType() throws Exception
  {
    disableConsole();

    CDOSession session1 = openSession(REPOSITORY_NAME);
    CDOSession session2 = openSession(REPOSITORY_NAME);
    session1.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    sessionId2 = session2.getSessionID();

    CDOTransaction transaction1 = session1.openTransaction();
    final CDOTransaction transaction2 = session2.openTransaction();
    final CDOTransaction transaction3 = session1.openTransaction();

    // create initial model.
    CDOResource resource = transaction1.createResource(getResourcePath("/test"));
    Company company1a = getModel1Factory().createCompany();
    company1a.setName("companyA");
    resource.getContents().add(company1a);
    Company company1b = getModel1Factory().createCompany();
    company1b.setName("companyB");
    resource.getContents().add(company1b);
    commitAndSync(transaction1, transaction2, transaction3);

    modelInitialized = true;

    // do concurrent changes on different objects, same type.
    Company company2a = transaction2.getObject(company1a);
    company2a.setName("companyA2");

    Company company3b = transaction3.getObject(company1b);
    company3b.setName("companyB2");

    Thread commitThread1 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          try
          {
            // wait until session 2 has entered write.
            enterLatch.await();
          }
          catch (InterruptedException ex)
          {
            ex.printStackTrace();
          }

          transaction3.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
        finally
        {
          // let session 2 continue.
          leaveLatch.countDown();
        }
      }
    };

    Thread commitThread2 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          transaction2.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    commitThread1.start();
    commitThread2.start();

    commitThread1.join();
    commitThread2.join();

    transaction1.waitForUpdate(transaction3.getLastCommitTime(), DEFAULT_TIMEOUT);
    transaction1.waitForUpdate(transaction2.getLastCommitTime(), DEFAULT_TIMEOUT);

    // do another commit.
    CDOTransaction transaction4 = session2.openTransaction();
    Company company4a = transaction4.getObject(company1a);
    company4a.setName("companyA3");
    commitAndSync(transaction4, transaction1);

    // check if update arrived.
    assertEquals(company4a.getName(), company1a.getName());
  }

  public void testCommitTimeStampUpdateLongRunningCommitDifferentType() throws Exception
  {
    disableConsole();

    CDOSession session1 = openSession(REPOSITORY_NAME);
    CDOSession session2 = openSession(REPOSITORY_NAME);
    session1.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    sessionId2 = session2.getSessionID();

    CDOTransaction transaction1 = session1.openTransaction();
    final CDOTransaction transaction2 = session2.openTransaction();
    final CDOTransaction transaction3 = session1.openTransaction();

    // create initial model.
    CDOResource resource = transaction1.createResource(getResourcePath("/test"));
    Company company1 = getModel1Factory().createCompany();
    company1.setName("company1");
    resource.getContents().add(company1);
    Category cat1 = getModel1Factory().createCategory();
    cat1.setName("category1");
    company1.getCategories().add(cat1);
    commitAndSync(transaction1, transaction2, transaction3);

    modelInitialized = true;

    // do concurrent changes on different objects, different type.
    Company company2 = transaction2.getObject(company1);
    company2.setName("company2");

    Category cat3 = transaction3.getObject(cat1);
    cat3.setName("category3");

    Thread commitThread1 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          try
          {
            // wait until session 2 has entered write.
            enterLatch.await();
          }
          catch (InterruptedException ex)
          {
            ex.printStackTrace();
          }

          transaction3.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
        finally
        {
          // let session 2 continue.
          leaveLatch.countDown();
        }
      }
    };

    Thread commitThread2 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          transaction2.commit();
        }
        catch (CommitException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    commitThread1.start();
    commitThread2.start();

    commitThread1.join();
    commitThread2.join();

    transaction1.waitForUpdate(transaction3.getLastCommitTime(), DEFAULT_TIMEOUT);
    transaction1.waitForUpdate(transaction2.getLastCommitTime(), DEFAULT_TIMEOUT);

    // do another commit.
    CDOTransaction transaction4 = session2.openTransaction();
    Company company4 = transaction4.getObject(company1);
    company4.setName("company3");
    commitAndSync(transaction4, transaction1);

    // check if update arrived.
    assertEquals(company4.getName(), company1.getName());
  }
}
