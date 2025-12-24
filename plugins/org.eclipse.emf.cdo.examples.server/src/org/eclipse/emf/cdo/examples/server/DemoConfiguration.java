/*
 * Copyright (c) 2010-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.UserManager;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Eike Stepper
 */
public class DemoConfiguration extends Lifecycle
{
  public static final int NAME_LENGTH = 10;

  private static final String NAME_ALPHABET = "abcdefghijklmnopqrstuvwxyz";

  private Mode mode = Mode.NORMAL;

  private String[] userIDs;

  private transient String name;

  private transient InternalRepository repository;

  private DemoUserManager userManager;

  private transient long lastAccess = System.currentTimeMillis();

  DemoConfiguration(Mode mode, String[] userIDs)
  {
    this.mode = mode;
    if (userIDs != null)
    {
      if (userIDs.length != 0)
      {
        if (StringUtil.isEmpty(userIDs[0]))
        {
          userIDs = null;
        }
      }
    }

    this.userIDs = userIDs;
  }

  public Mode getMode()
  {
    return mode;
  }

  public String[] getUserIDs()
  {
    return userIDs;
  }

  public String getName()
  {
    return name;
  }

  public IRepository getRepository()
  {
    return repository;
  }

  public Map<String, char[]> getUsers()
  {
    if (userManager == null)
    {
      return Collections.emptyMap();
    }

    return userManager.getUsers();
  }

  public synchronized long getTimeoutMillis()
  {
    long idleTime = System.currentTimeMillis() - lastAccess;
    long time = DemoServer.MAX_IDLE_MILLIS - idleTime;
    if (time < 0)
    {
      time = 0;
    }

    return time;
  }

  public String formatTimeoutMinutes()
  {
    return MessageFormat.format("{0,time,mm:ss}", getTimeoutMillis());
  }

  @Override
  protected void doActivate() throws Exception
  {
    name = createRandomName();

    IDBStore store = createStore();

    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, ""); // Use repo name
    props.put(IRepository.Props.SUPPORTING_AUDITS, mode == Mode.NORMAL ? "false" : "true");
    props.put(IRepository.Props.SUPPORTING_BRANCHES, mode == Mode.BRANCHING ? "true" : "false");

    repository = (InternalRepository)CDOServerUtil.createRepository(name, store, props);

    InternalSessionManager sessionManager = createSessionManager();

    if (userIDs != null)
    {
      IAuthenticator authenticator = createAuthenticator();
      sessionManager.setAuthenticator(authenticator);
    }

    CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (repository != null)
    {
      File folder = new File(new File("databases"), repository.getName());

      LifecycleUtil.deactivate(repository);
      repository = null;

      IOUtil.delete(folder);
    }
  }

  protected String createRandomName()
  {
    return createRandomString(NAME_LENGTH, NAME_ALPHABET);
  }

  protected IDBStore createStore()
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(createDataSource());
    IDBStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
    return store;
  }

  protected IMappingStrategy createMappingStrategy()
  {
    switch (mode)
    {
    case NORMAL:
      return CDODBUtil.createHorizontalMappingStrategy(false, false);
    case AUDITING:
      return CDODBUtil.createHorizontalMappingStrategy(true, false);
    case BRANCHING:
      return CDODBUtil.createHorizontalMappingStrategy(true, true);
    default:
      throw new IllegalStateException("Invalid mode: " + mode);
    }
  }

  protected DataSource createDataSource()
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:databases/" + name + "/h2test");
    return dataSource;
  }

  protected InternalSessionManager createSessionManager()
  {
    InternalSessionManager sessionManager = (InternalSessionManager)CDOServerUtil.createSessionManager();
    repository.setSessionManager(sessionManager);
    sessionManager.addListener(new ContainerEventAdapter<ISession>()
    {
      @Override
      protected void onAdded(IContainer<ISession> container, ISession session)
      {
        ISignalProtocol<?> protocol = (ISignalProtocol<?>)session.getProtocol();
        if (protocol != null)
        {
          protocol.addListener(new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              synchronized (DemoConfiguration.this)
              {
                lastAccess = System.currentTimeMillis();
              }
            }
          });
        }
      }
    });

    return sessionManager;
  }

  protected IAuthenticator createAuthenticator()
  {
    userManager = new DemoUserManager();
    for (int i = 0; i < userIDs.length; i++)
    {
      String userID = userIDs[i];
      userManager.addUser(userID, ("pw" + (i + 1)).toCharArray());
    }

    userManager.activate();
    return userManager;
  }

  public static String createRandomString(int length, String alphabet)
  {
    Random random = new Random(System.currentTimeMillis());
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++)
    {
      int pos = random.nextInt(alphabet.length());
      builder.append(alphabet.charAt(pos));
    }

    return builder.toString();
  }

  public static String createRandomString(int length)
  {
    return createRandomString(length, NAME_ALPHABET);
  }

  /**
   * @author Eike Stepper
   */
  private static final class DemoUserManager extends UserManager
  {
    public DemoUserManager()
    {
    }

    public Map<String, char[]> getUsers()
    {
      return users;
    }
  }

  /**
   * @author Eike Stepper
   */
  public enum Mode
  {
    NORMAL, AUDITING, BRANCHING
  }
}
