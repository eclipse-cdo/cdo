/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bug 560280 - Possible deadlock during the session invalidation.
 *
 * @author Eike Stepper
 */
public class Bugzilla_560280_Test extends AbstractCDOTest
{
  public void testDeadlockBetweenInvalidationAndCommit() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("resource1"));
    transaction1.commit();

    AtomicReference<CDOTransaction> transactionUnderTest = new AtomicReference<>();
    AtomicBoolean controlUpdatePermissions = new AtomicBoolean();
    CountDownLatch reachedUpdatePermissions = new CountDownLatch(1);
    CountDownLatch allowUpdatePermissions = new CountDownLatch(1);

    CDONet4jSessionConfiguration configuration = new CDONet4jSessionConfigurationImpl()
    {
      @Override
      public InternalCDOSession createSession()
      {
        return new CDONet4jSessionImpl()
        {
          @Override
          public InternalCDOView[] getViews()
          {
            if (controlUpdatePermissions.get())
            {
              reachedUpdatePermissions.countDown();
              await(allowUpdatePermissions);

              transactionUnderTest.get().syncExec(() -> {
                // Just take the view lock.
              });
            }

            return super.getViews();
          }
        };
      }
    };

    CDONet4jSessionConfiguration template = (CDONet4jSessionConfiguration)((SessionConfig)getSessionConfig())
        .createSessionConfiguration(IRepositoryConfig.REPOSITORY_NAME);
    configuration.setConnector(template.getConnector());
    configuration.setRepositoryName(template.getRepositoryName());
    configuration.setRevisionManager(template.getRevisionManager());

    getTestProperties().put(SessionConfig.PROP_TEST_SESSION_CONFIGURATION, configuration);

    CDOSession sessionUnderTest = openSession();
    transactionUnderTest.set(sessionUnderTest.openTransaction());
    CDOResource resourceUnderTest = transactionUnderTest.get().createResource(getResourcePath("resourceUnderTest"));

    /*
     * Test Logic:
     */

    controlUpdatePermissions.set(true);

    resource1.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    // Execute transactionUndertTest.commit() on a separate thread so that the deadlock doesn't freeze the test suite.
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        await(reachedUpdatePermissions);
        allowUpdatePermissions.countDown();

        try
        {
          resourceUnderTest.getContents().add(getModel1Factory().createCompany());
          transactionUnderTest.get().commit();
        }
        catch (CommitException ex)
        {
          // This is really not expected now.
          ex.printStackTrace();
        }

        return true;
      }
    }.assertNoTimeOut();
  }
}
