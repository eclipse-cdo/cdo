/*
 * Copyright (c) 2011-2013, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.h2.jdbcx.JdbcDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class Server
{
  public static void main(String[] args) throws Exception
  {
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    Net4jUtil.prepareContainer(IPluginContainer.INSTANCE); // Prepare the Net4j kernel
    TCPUtil.prepareContainer(IPluginContainer.INSTANCE); // Prepare the TCP support
    CDONet4jServerUtil.prepareContainer(IPluginContainer.INSTANCE); // Prepare the CDO server

    String name = "demo";
    IStore store = createStore(name);
    Map<String, String> properties = createProperties(name);

    IRepository repository = CDOServerUtil.createRepository(name, store, properties);
    CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository);

    Net4jUtil.getAcceptor(IPluginContainer.INSTANCE, "tcp", "0.0.0.0:2036");

    while (System.in.available() == 0)
    {
      Thread.sleep(100);
    }

    LifecycleUtil.deactivate(repository);
    LifecycleUtil.deactivate(IPluginContainer.INSTANCE);
  }

  private static IStore createStore(String name)
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:database/" + name);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true);
    IDBAdapter dbAdapter = new H2Adapter();
    IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(dataSource);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
  }

  private static Map<String, String> createProperties(String name)
  {
    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, name);
    props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    return props;
  }
}
