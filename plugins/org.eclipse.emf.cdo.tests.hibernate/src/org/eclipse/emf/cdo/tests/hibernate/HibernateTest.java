/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.util.om.log.PrintLogHandler;
import org.eclipse.net4j.internal.util.om.trace.PrintTraceHandler;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQLInnoDBDialect;

import java.io.PrintWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class HibernateTest extends AbstractOMTest
{
  private static final String REPOSITORY_NAME = "repo1";

  public void testHibernate() throws Exception
  {
    IManagedContainer container = initContainer();
    JVMUtil.getAcceptor(container, "default"); // Start the JVM transport
    CDOServerUtil.addRepository(container, createRepository()); // Start a CDO respository
    IConnector connector = JVMUtil.getConnector(container, "default"); // Open a JVM connection

    CDOSession session = CDOUtil.openSession(connector, REPOSITORY_NAME, true);// Open a CDO session
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);// Not needed after first commit!!!
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource("/my/big/resource");
    resource.getContents().add(getInputModel());
    transaction.commit();
    session.close();

    CDOSession session2 = CDOUtil.openSession(connector, REPOSITORY_NAME, true);// Open a CDO session
    CDOTransaction transaction2 = session2.openTransaction();
    Resource resource2 = transaction2.getResource("/my/big/resource");
    Category category = (Category)resource2.getContents().get(0);
    assertEquals("CAT1", category.getName());
    assertEquals("CAT2", category.getCategories().get(0).getName());
    assertEquals("P1", category.getProducts().get(0).getName());
    assertEquals("P2", category.getProducts().get(1).getName());
    assertEquals("P3", category.getCategories().get(0).getProducts().get(0).getName());
    transaction.close();

    // Cleanup
    session2.close();
    connector.disconnect();
  }

  private static IManagedContainer initContainer()
  {
    // Turn on tracing
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    // Prepare the standalone infra structure (not needed when running inside Eclipse)
    IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
    Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
    JVMUtil.prepareContainer(container); // Prepare the JVM transport
    CDOServerUtil.prepareContainer(container); // Prepare the CDO server
    CDOUtil.prepareContainer(container, false); // Prepare the CDO client
    return container;
  }

  private static IRepository createRepository() throws Exception
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.PROP_SUPPORTING_AUDITS, "false");
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
    props.put(Props.PROP_VERIFYING_REVISIONS, "false");
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
    return CDOServerUtil.createRepository(REPOSITORY_NAME, createStore(), props);
  }

  private static IStore createStore() throws Exception
  {
    DriverManager.setLogWriter(new PrintWriter(System.out));
    Driver driver = new com.mysql.jdbc.Driver();
    DriverManager.registerDriver(driver);
    String driverName = driver.getClass().getName();
    String dialectName = MySQLInnoDBDialect.class.getName();

    Properties props = new Properties();
    props.setProperty(Environment.DRIVER, driverName);
    props.setProperty(Environment.URL, "jdbc:mysql://localhost/cdohibernate");
    props.setProperty(Environment.USER, "root");
    // props.setProperty(Environment.PASS, "root");
    props.setProperty(Environment.DIALECT, dialectName);
    props.setProperty(Environment.SHOW_SQL, "true");
    props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
    // IHibernateMappingProvider mappingProvider = new TeneoHibernateMappingProvider();
    // return new HibernateStore(props, mappingProvider);
    return new HibernateStore(props, null);
  }

  private static EObject getInputModel()
  {
    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    cat1.setName("CAT1");
    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product p1 = Model1Factory.eINSTANCE.createProduct();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product p2 = Model1Factory.eINSTANCE.createProduct();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product p3 = Model1Factory.eINSTANCE.createProduct();
    p3.setName("P3");
    cat2.getProducts().add(p3);
    return cat1;
  }
}
