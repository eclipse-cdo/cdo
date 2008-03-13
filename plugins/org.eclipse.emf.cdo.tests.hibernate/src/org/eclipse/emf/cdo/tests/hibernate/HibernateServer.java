/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - extended testcase
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateFileMappingProvider;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.internal.util.om.log.PrintLogHandler;
import org.eclipse.net4j.internal.util.om.trace.PrintTraceHandler;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLInnoDBDialect;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HibernateServer
{
  private static final String REPOSITORY_NAME = "repo1";

  private static final boolean TRACE_TO_CONSOLE = true;

  private static final boolean TRACE_TO_FILE = false;

  private static PrintStream traceStream;

  public static void main(String[] args) throws Exception
  {
    try
    {
      IManagedContainer container = initContainer();
      TCPUtil.getAcceptor(container, "0.0.0.0:2036"); // Start the JVM transport
      CDOServerUtil.addRepository(container, createRepository()); // Start a CDO respository

      IOUtil.OUT().println();
      IOUtil.OUT().println("Hit any key to shut down...");
      while (System.in.read() == -1)
      {
        Thread.sleep(500);
      }

      LifecycleUtil.deactivate(container);
    }
    finally
    {
      IOUtil.closeSilent(traceStream);
    }
  }

  private static IManagedContainer initContainer() throws FileNotFoundException
  {
    // Turn on tracing
    OMPlatform.INSTANCE.setDebugging(false);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    if (TRACE_TO_FILE)
    {
      traceStream = new PrintStream("trace.txt");
      OMPlatform.INSTANCE.addTraceHandler(new PrintTraceHandler(traceStream));
    }

    if (TRACE_TO_CONSOLE)
    {
      OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    }

    // Prepare the standalone infra structure (not needed when running inside Eclipse)
    IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
    Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
    TCPUtil.prepareContainer(container); // Prepare the TCP transport
    CDOServerUtil.prepareContainer(container); // Prepare the CDO server
    return container;
  }

  private static IRepository createRepository() throws Exception
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.PROP_OVERRIDE_UUID, "f8188187-65de-4c8a-8e75-e0ce5949837a");
    props.put(Props.PROP_SUPPORTING_AUDITS, "false");
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
    props.put(Props.PROP_VERIFYING_REVISIONS, "false");
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
    addHibernateTeneoProperties(props);

    return CDOServerUtil.createRepository(REPOSITORY_NAME, createStore(), props);
  }

  private static void addHibernateTeneoProperties(Map<String, String> props) throws Exception
  {
    DriverManager.setLogWriter(new PrintWriter(System.out));
    Driver driver = new com.mysql.jdbc.Driver();
    DriverManager.registerDriver(driver);
    String driverName = driver.getClass().getName();
    String dialectName = MySQLInnoDBDialect.class.getName();

    props.put(Environment.DRIVER, driverName);
    props.put(Environment.URL, "jdbc:mysql://localhost/cdohibernate");
    props.put(Environment.USER, "cdo");
    // props.setProperty(Environment.PASS, "root");
    props.put(Environment.DIALECT, dialectName);
    props.put(Environment.SHOW_SQL, "true");
    props.put("hibernate.hbm2ddl.auto", "create-drop");
  }

  private static IStore createStore() throws Exception
  {
    // IHibernateMappingProvider mappingProvider = new TeneoHibernateMappingProvider();
    // return new HibernateStore(props, mappingProvider);
    IHibernateMappingProvider mappingProvider = new HibernateFileMappingProvider("/mappings/product.hbm.xml");
    return new HibernateStore(mappingProvider);
  }
}
