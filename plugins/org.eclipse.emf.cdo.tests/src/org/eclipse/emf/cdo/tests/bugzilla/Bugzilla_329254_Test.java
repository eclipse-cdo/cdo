/*
 * Copyright (c) 2011-2013, 2016, 2018 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
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
@Requires(IConfig.CAPABILITY_UNAVAILABLE)
public class Bugzilla_329254_Test extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private final CountDownLatch enterLatch = new CountDownLatch(1);

  private final CountDownLatch leaveLatch = new CountDownLatch(1);

  private boolean modelInitialized;

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

                await(leaveLatch);
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
    repository.setProperties(props);

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

    CDOTransaction transaction10 = session1.openTransaction();
    final CDOTransaction transaction11async = session1.openTransaction();
    final CDOTransaction transaction21async = session2.openTransaction();
    CDOTransaction transaction12 = session1.openTransaction();
    CDOTransaction transaction22 = session2.openTransaction();

    // Create initial model.
    CDOResource resource = transaction10.createResource(getResourcePath("/test"));
    final Company company10 = getModel1Factory().createCompany();
    company10.setName("company");
    resource.getContents().add(company10);
    commitAndSync(transaction10, transaction11async, transaction21async);

    Thread thread11 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          // Do concurrent changes on company to produce an error.
          Company company11 = transaction11async.getObject(company10);
          company11.setCity("city");
          transaction11async.commit();
        }
        catch (Exception ex)
        {
          IOUtil.OUT().println("Simulated problem in thread 11: " + ex.getClass().getName());
        }
      }
    };

    Thread thread21 = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          // Do concurrent changes on company to produce an error.
          Company company21 = transaction21async.getObject(company10);
          company21.setStreet("street");
          transaction21async.commit();
        }
        catch (Exception ex)
        {
          IOUtil.OUT().println("Simulated problem in thread 21: " + ex.getClass().getName());
        }
      }
    };

    thread11.start();
    thread21.start();

    thread11.join(DEFAULT_TIMEOUT);
    thread21.join(DEFAULT_TIMEOUT);

    transaction22.waitForUpdate(transaction11async.getLastCommitTime(), DEFAULT_TIMEOUT);
    transaction22.waitForUpdate(transaction21async.getLastCommitTime(), DEFAULT_TIMEOUT);

    // Do another commit.
    Company company4 = transaction22.getObject(company10);
    company4.setName("company2");
    commitAndSync(transaction22, transaction10, transaction12);

    // Check if update arrived.
    assertEquals(company4.getName(), company10.getName());

    // Check committing on the other session too.
    Company company5 = transaction12.getObject(company10);
    company5.setName("company3");
    commitAndSync(transaction12, transaction22);

    // Check if update arrived.
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
          // wait until session 2 has entered write.
          await(enterLatch);

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

    commitThread1.join(DEFAULT_TIMEOUT);
    commitThread2.join(DEFAULT_TIMEOUT);

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
          // wait until session 2 has entered write.
          await(enterLatch);

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

    commitThread1.join(DEFAULT_TIMEOUT);
    commitThread2.join(DEFAULT_TIMEOUT);

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
