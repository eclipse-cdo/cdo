/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.admin.CDOAdminUtil;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.spi.admin.CDOAdminHandler;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig.CallAddRepository;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.util.TestListener;
import org.eclipse.emf.cdo.tests.util.TestListener.EventAssertion;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Design a repository administration API
 * <p>
 * See bug 381472
 *
 * @author Eike Stepper
 */
@Requires(ISessionConfig.CAPABILITY_NET4J)
@CleanRepositoriesBefore
@CleanRepositoriesAfter
@CallAddRepository
public class Bugzilla_381472_Test extends AbstractCDOTest
{
  private static final String ADMIN_HANDLER_TYPE = "test";

  private CDOAdmin openAdmin(final Map<String, Object> expectedProperties)
  {
    IManagedContainer serverContainer = getContainerConfig().getServerContainer();
    serverContainer.registerFactory(new CDOAdminHandler.Factory(ADMIN_HANDLER_TYPE)
    {
      @Override
      public CDOAdminHandler create(String description) throws ProductCreationException
      {
        return new CDOAdminHandler()
        {

          public String getType()
          {
            return ADMIN_HANDLER_TYPE;
          }

          public IRepository createRepository(String name, Map<String, Object> properties)
          {
            assertEquals(expectedProperties, properties);
            return getRepository(name);
          }

          public void deleteRepository(IRepository delegate)
          {
            // Do nothing
          }
        };
      }
    });

    SessionConfig.Net4j sessionConfig = (SessionConfig.Net4j)getSessionConfig();
    IConnector connector = sessionConfig.getConnector();
    return CDOAdminUtil.openAdmin(connector);
  }

  private void cleanup(CDOAdmin admin)
  {
    IOUtil.closeSilent(admin);
  }

  // @Override
  // protected void doSetUp() throws Exception
  // {
  // getRepositoryConfig().setAddRepository(true);
  // super.doSetUp();
  // }
  //
  // @Override
  // protected void doTearDown() throws Exception
  // {
  // getRepositoryConfig().setAddRepository(false);
  // super.doTearDown();
  // }

  public void testInitial() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(1, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoAdded() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      InternalRepository repo2 = getRepository("repo2");
      admin.waitForRepository("repo2");

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoAddedRemoved() throws Exception
  {
    final CDOAdmin admin = openAdmin(null);

    try
    {
      InternalRepository repo2 = getRepository("repo2");
      admin.waitForRepository("repo2");

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      LifecycleUtil.deactivate(repo2);

      new PollingTimeOuter()
      {
        @Override
        protected boolean successful()
        {
          CDOAdminRepository[] repositories = admin.getRepositories();
          if (repositories.length != 1)
          {
            return false;
          }

          assertEquals(getRepository().getName(), repositories[0].getName());
          return true;
        }
      }.assertNoTimeOut();
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoAddedRemovedAdded() throws Exception
  {
    final CDOAdmin admin = openAdmin(null);

    try
    {
      InternalRepository repo2 = getRepository("repo2");
      admin.waitForRepository("repo2");

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      LifecycleUtil.deactivate(repo2);

      new PollingTimeOuter()
      {
        @Override
        protected boolean successful()
        {
          CDOAdminRepository[] repositories = admin.getRepositories();
          if (repositories.length != 1)
          {
            return false;
          }

          assertEquals(getRepository().getName(), repositories[0].getName());
          return true;
        }
      }.assertNoTimeOut();

      repo2 = getRepository("repo2");
      admin.waitForRepository("repo2");

      repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoAddedRemovedAddedOther() throws Exception
  {
    final CDOAdmin admin = openAdmin(null);

    try
    {
      InternalRepository repo2 = getRepository("repo2");
      admin.waitForRepository("repo2");

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      LifecycleUtil.deactivate(repo2);

      new PollingTimeOuter()
      {
        @Override
        protected boolean successful()
        {
          CDOAdminRepository[] repositories = admin.getRepositories();
          if (repositories.length != 1)
          {
            return false;
          }

          assertEquals(getRepository().getName(), repositories[0].getName());
          return true;
        }
      }.assertNoTimeOut();

      InternalRepository repo3 = getRepository("repo3");
      admin.waitForRepository("repo3");

      repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo3.getName(), repositories[1].getName());
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoAddedAdded() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      InternalRepository repo2 = getRepository("repo2");
      admin.waitForRepository("repo2");

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      InternalRepository repo3 = getRepository("repo3");
      admin.waitForRepository("repo3");

      repositories = admin.getRepositories();
      assertEquals(3, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());
      assertEquals(repo3.getName(), repositories[2].getName());
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoAddedEvent() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      getRepository("repo2");
      admin.waitForRepository("repo2");

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
          assertEquals("repo2", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoRemovedEvent() throws Exception
  {
    final CDOAdmin admin = openAdmin(null);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      InternalRepository repo1 = getRepository();
      LifecycleUtil.deactivate(repo1);

      new PollingTimeOuter()
      {
        @Override
        protected boolean successful()
        {
          CDOAdminRepository[] repositories = admin.getRepositories();
          return repositories.length == 0;
        }
      }.assertNoTimeOut();

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.REMOVED, event.getDeltaKind());
          assertEquals("repo1", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoTypeChangedEvent() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      CDOAdminRepository repo1 = admin.getRepository("repo1");

      TestListener listener = new TestListener();
      repo1.addListener(listener);

      getRepository().setType(Type.BACKUP);

      listener.assertEvent(new EventAssertion<RepositoryTypeChangedEvent>()
      {
        public void execute(RepositoryTypeChangedEvent event) throws Exception
        {
          assertEquals(Type.MASTER, event.getOldType());
          assertEquals(Type.BACKUP, event.getNewType());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testRepoStateChangedEvent() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      CDOAdminRepository repo1 = admin.getRepository("repo1");

      final TestListener listener = new TestListener();
      repo1.addListener(listener);

      getRepository().setState(State.OFFLINE);

      listener.assertEvent(new EventAssertion<RepositoryStateChangedEvent>()
      {
        public void execute(RepositoryStateChangedEvent event) throws Exception
        {
          assertEquals(State.ONLINE, event.getOldState());
          assertEquals(State.OFFLINE, event.getNewState());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testCreateRepo() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      CDOAdminRepository repo2 = admin.createRepository("repo2", ADMIN_HANDLER_TYPE, null);
      assertEquals("repo2", repo2.getName());

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
          assertEquals("repo2", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testCreateRepoWithPropertiesEmpty() throws Exception
  {
    Map<String, Object> properties = Collections.emptyMap();
    CDOAdmin admin = openAdmin(properties);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      CDOAdminRepository repo2 = admin.createRepository("repo2", ADMIN_HANDLER_TYPE, properties);
      assertEquals("repo2", repo2.getName());

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
          assertEquals("repo2", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testCreateRepoWithPropertiesPrimitive() throws Exception
  {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("key", 4711);

    CDOAdmin admin = openAdmin(properties);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      CDOAdminRepository repo2 = admin.createRepository("repo2", ADMIN_HANDLER_TYPE, properties);
      assertEquals("repo2", repo2.getName());

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
          assertEquals("repo2", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testCreateRepoWithPropertiesCDOID() throws Exception
  {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put("key", CDOIDUtil.createLong(4711));

    CDOAdmin admin = openAdmin(properties);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      CDOAdminRepository repo2 = admin.createRepository("repo2", ADMIN_HANDLER_TYPE, properties);
      assertEquals("repo2", repo2.getName());

      CDOAdminRepository[] repositories = admin.getRepositories();
      assertEquals(2, repositories.length);
      assertEquals(getRepository().getName(), repositories[0].getName());
      assertEquals(repo2.getName(), repositories[1].getName());

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.ADDED, event.getDeltaKind());
          assertEquals("repo2", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testCreateRepoWrongHandlerType() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      admin.createRepository("repo2", "WRONG", null);
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // Success
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testDeleteRepo() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    TestListener listener = new TestListener();
    admin.addListener(listener);

    try
    {
      CDOAdminRepository repo1 = admin.getRepository("repo1");
      repo1.delete(ADMIN_HANDLER_TYPE);
      assertEquals(null, admin.getRepository("repo1"));

      listener.assertEvent(new EventAssertion<IContainerEvent<CDOAdminRepository>>()
      {
        public void execute(IContainerEvent<CDOAdminRepository> event) throws Exception
        {
          assertEquals(IContainerDelta.Kind.REMOVED, event.getDeltaKind());
          assertEquals("repo1", event.getDeltaElement().getName());
        }
      });
    }
    finally
    {
      cleanup(admin);
    }
  }

  public void testDeleteRepoWrongHandlerType() throws Exception
  {
    CDOAdmin admin = openAdmin(null);

    try
    {
      CDOAdminRepository repo1 = admin.getRepository("repo1");
      repo1.delete("WRONG");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // Success
    }
    finally
    {
      cleanup(admin);
    }
  }
}
