/*
 * Copyright (c) 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.TestListener2;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Bug 387564 - Ensure lock notification sending after invalidation for "lock/unlock objects on commit"
 *
 * @author Eike Stepper
 */
public class Bugzilla_387564_Test extends AbstractCDOTest
{
  private static final boolean DEBUG = true;

  public void testLockEventAfterInvalidationEventSameSession() throws Exception
  {
    CDOSession session = openSession();
    runTest(session, session);
  }

  public void testLockEventAfterInvalidationEventDifferentSession() throws Exception
  {
    CDOSession session = openSession();
    CDOSession controlSession = openSession();
    runTest(session, controlSession);
  }

  private void runTest(CDOSession session, CDOSession controlSession) throws ConcurrentAccessException, CommitException
  {
    Company company = getModel1Factory().createCompany();
    company.setName("Initial");

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(company);
    transaction.commit();

    CDOUtil.getCDOObject(company).cdoWriteLock().lock();
    company.setName("Changed");

    CDONet4jSessionConfiguration template = (CDONet4jSessionConfiguration)((SessionConfig)getSessionConfig())
        .createSessionConfiguration(IRepositoryConfig.REPOSITORY_NAME);

    CDONet4jSessionConfiguration configuration = new CDONet4jSessionConfigurationImpl()
    {
      @Override
      public InternalCDOSession createSession()
      {
        return new CDONet4jSessionImpl()
        {
          @Override
          public void invalidate(InvalidationData invalidationData)
          {
            sleep(1000); // Delay the invalidation handling to give lock notifications a chance to overtake.
            super.invalidate(invalidationData);
          }
        };
      }
    };

    configuration.setConnector(template.getConnector());
    configuration.setRepositoryName(template.getRepositoryName());
    configuration.setRevisionManager(template.getRevisionManager());
    getTestProperties().put(SessionConfig.PROP_TEST_SESSION_CONFIGURATION, configuration);

    TestListener2 controlSessionListener = createControlListener("SESSION");
    controlSession.addListener(controlSessionListener);

    TestListener2 controlViewListener = createControlListener("VIEW");
    CDOView controlView = controlSession.openView();
    controlView.options().setLockNotificationEnabled(true);
    controlView.addListener(controlViewListener);

    // Load the company into the control view, so that the view emits invalidation events for it.
    Company controlObject = controlView.getObject(company);
    IOUtil.OUT().println(controlObject.getName());
    ((CDOTransaction)CDOUtil.getView(company)).commit();

    controlSessionListener.waitFor(2);
    assertInstanceOf(CDOSessionInvalidationEvent.class, controlSessionListener.getEvents().get(0));
    assertInstanceOf(CDOSessionLocksChangedEvent.class, controlSessionListener.getEvents().get(1));
    if (DEBUG)
    {
      IOUtil.OUT().println(controlSessionListener);
      IOUtil.OUT().println(controlSessionListener.formatEvents("   ", "\n"));
    }

    controlViewListener.waitFor(2);
    assertInstanceOf(CDOViewInvalidationEvent.class, controlViewListener.getEvents().get(0));
    assertInstanceOf(CDOViewLocksChangedEvent.class, controlViewListener.getEvents().get(1));
    if (DEBUG)
    {
      IOUtil.OUT().println(controlViewListener);
      IOUtil.OUT().println(controlViewListener.formatEvents("   ", "\n"));
    }

    IOUtil.OUT().println(controlObject.getName());
  }

  private static TestListener2 createControlListener(String name)
  {
    List<Class<? extends IEvent>> eventClasses = new ArrayList<>();
    eventClasses.add(CDOSessionInvalidationEvent.class);
    eventClasses.add(CDOSessionLocksChangedEvent.class);
    eventClasses.add(CDOViewInvalidationEvent.class);
    eventClasses.add(CDOViewLocksChangedEvent.class);

    return new TestListener2(eventClasses, name);
  }
}
