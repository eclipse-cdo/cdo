/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.plain.server;

import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.postgresql.PostgreSQLAdapter;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.postgresql.ds.PGSimpleDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Runs a standalone CDO repository backed by PostgreSQL and exposed over Net4j TCP. The optional
 * address argument can be a bind address, a port, or a {@code bind-address:port} pair. The default
 * is {@code 0.0.0.0:2036}; for example, use {@code 2036}, {@code 127.0.0.1}, or
 * {@code 192.168.1.50:2036}. The bind address {@code 0.0.0.0} listens on all local IPv4
 * interfaces; clients must connect to an actual reachable hostname or IP address.
 *
 * @author Eike Stepper
 */
public final class RemoteServer
{
  private static final String REPOSITORY_NAME = "repo1"; //$NON-NLS-1$

  private RemoteServer()
  {
  }

  /**
   * Starts the PostgreSQL-backed repository and waits until the process is stopped.
   * Database settings come from {@code cdo.db.url}, {@code cdo.db.user}, and {@code cdo.db.password}
   * system properties or the corresponding {@code CDO_DB_URL}, {@code CDO_DB_USER}, and
   * {@code CDO_DB_PASSWORD} environment variables. The optional argument may be a port (for
   * example, {@code 2036}), a bind address (for example, {@code 127.0.0.1}), or a
   * {@code bind-address:port} pair (for example, {@code 192.168.1.50:2036}). With no argument,
   * the server binds to {@code 0.0.0.0:2036}, listening on all local IPv4 interfaces. This is a
   * local bind address; clients should connect using a reachable hostname or IP address.
   *
   * @param args zero or one bind address, port, or {@code bind-address:port} argument
   * @throws Exception if repository startup fails
   */
  public static void main(String[] args) throws Exception
  {
    String url = setting("cdo.db.url"); //$NON-NLS-1$
    String user = setting("cdo.db.user"); //$NON-NLS-1$
    String password = setting("cdo.db.password"); //$NON-NLS-1$

    if (url == null || user == null)
    {
      throw new IllegalArgumentException("Set cdo.db.url and cdo.db.user system properties or CDO_DB_URL and CDO_DB_USER environment variables."); //$NON-NLS-1$
    }

    String address = parseAddress(args);
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      PGSimpleDataSource dataSource = new PGSimpleDataSource();
      dataSource.setURL(url);
      dataSource.setUser(user);

      if (password != null)
      {
        dataSource.setPassword(password);
      }

      IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy();
      IDBAdapter dbAdapter = new PostgreSQLAdapter();
      IDBConnectionProvider connectionProvider = dbAdapter.createConnectionProvider(dataSource);
      IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

      Map<String, String> properties = new HashMap<>();
      properties.put(IRepository.Props.SUPPORTING_AUDITS, "false"); //$NON-NLS-1$
      properties.put(IRepository.Props.SUPPORTING_BRANCHES, "false"); //$NON-NLS-1$

      IRepository repository = CDOServerUtil.createRepository(REPOSITORY_NAME, store, properties);
      repository.setInitialPackages(CompanyPackage.eINSTANCE);
      CDOServerUtil.addRepository(container, repository);

      ITCPAcceptor acceptor = TCPUtil.getAcceptor(container, address);
      System.out.println("CDO repository " + REPOSITORY_NAME + " listening on TCP port " + acceptor.getPort()); //$NON-NLS-1$

      Runtime.getRuntime().addShutdownHook(new Thread(container::deactivate, "cdo-server-shutdown")); //$NON-NLS-1$

      System.out.println("Press Enter to stop the server."); //$NON-NLS-1$
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)))
      {
        reader.readLine();
      }
    }
    catch (Exception | Error ex)
    {
      container.deactivate();
      throw ex;
    }
  }

  /**
   * Parses the optional server bind argument.
   *
   * @param args zero or one bind address, port, or {@code bind-address:port} argument
   * @return the Net4j acceptor address, defaulting to {@code 0.0.0.0:2036}
   */
  private static String parseAddress(String[] args)
  {
    if (args.length > 1)
    {
      throw new IllegalArgumentException("Expected at most one bind address or port."); //$NON-NLS-1$
    }

    if (args.length == 0)
    {
      return "0.0.0.0:" + ITCPAcceptor.DEFAULT_PORT; //$NON-NLS-1$
    }

    String argument = args[0];
    if (argument.indexOf(':') >= 0)
    {
      return argument;
    }

    if (argument.chars().allMatch(Character::isDigit))
    {
      return "0.0.0.0:" + argument; //$NON-NLS-1$
    }

    return argument + ":" + ITCPAcceptor.DEFAULT_PORT; //$NON-NLS-1$
  }

  private static String setting(String property)
  {
    String value = System.getProperty(property);
    return value != null ? value : System.getenv().get(property.replace('.', '_').toUpperCase());
  }
}
