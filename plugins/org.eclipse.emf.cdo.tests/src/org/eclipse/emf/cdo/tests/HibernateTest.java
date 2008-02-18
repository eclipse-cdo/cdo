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
package org.eclipse.emf.cdo.tests;

/**
 * @author Eike Stepper
 */
public class HibernateTest
{
  // private static final String REPOSITORY_NAME = "repo1";
  //
  // public static void main(String[] args) throws Exception
  // {
  // IManagedContainer container = initContainer();
  //
  // // Start the transport and create a repository
  // JVMUtil.getAcceptor(container, "default"); // Start the JVM transport
  // CDOServerUtil.addRepository(container, createRepository()); // Start a CDO respository
  //
  // // Establish a communications connection and open a session with the repository
  // IConnector connector = JVMUtil.getConnector(container, "default"); // Open a JVM connection
  // CDOSession session = CDOUtil.openSession(connector, REPOSITORY_NAME, true);// Open a CDO session
  // session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);// Not needed after first commit!!!
  //
  // // Open a transaction, and create a new resource
  // CDOTransaction transaction = session.openTransaction();
  // Resource resource = transaction.createResource("/my/big/resource");
  // resource.getContents().add(getInputModel());
  // transaction.commit();
  //
  // // Cleanup
  // session.close();
  // connector.disconnect();
  // }
  //
  // private static IManagedContainer initContainer()
  // {
  // // Turn on tracing
  // OMPlatform.INSTANCE.setDebugging(true);
  // OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
  // OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
  //
  // // Prepare the standalone infra structure (not needed when running inside Eclipse)
  // IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
  // Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
  // JVMUtil.prepareContainer(container); // Prepare the JVM transport
  // CDOServerUtil.prepareContainer(container); // Prepare the CDO server
  // CDOUtil.prepareContainer(container, false); // Prepare the CDO client
  // return container;
  // }
  //
  // private static IRepository createRepository() throws Exception
  // {
  // Map<String, String> props = new HashMap<String, String>();
  // props.put(Props.PROP_SUPPORTING_AUDITS, "false");
  // props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "true");
  // props.put(Props.PROP_VERIFYING_REVISIONS, "false");
  // props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
  // props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
  // return CDOServerUtil.createRepository(REPOSITORY_NAME, createStore(), props);
  // }
  //
  // private static IStore createStore() throws Exception
  // {
  // DriverManager.setLogWriter(new PrintWriter(System.out));
  // Driver driver = new com.mysql.jdbc.Driver();
  // DriverManager.registerDriver(driver);
  // String driverName = driver.getClass().getName();
  // String dialectName = MySQLDialect.class.getName();
  //
  // Configuration configuration = new Configuration();
  // configuration.setProperty(Environment.DRIVER, driverName);
  // configuration.setProperty(Environment.URL, "jdbc:mysql://localhost/cdohibernate");
  // configuration.setProperty(Environment.USER, "root");
  // configuration.setProperty(Environment.DIALECT, dialectName);
  // configuration.setProperty(Environment.SHOW_SQL, "true");
  // return new HibernateStore(configuration);
  // }
  //
  // private static EObject getInputModel()
  // {
  // Category cat1 = Model1Factory.eINSTANCE.createCategory();
  // cat1.setName("CAT1");
  // Category cat2 = Model1Factory.eINSTANCE.createCategory();
  // cat2.setName("CAT2");
  // cat1.getCategories().add(cat2);
  // Product p1 = Model1Factory.eINSTANCE.createProduct();
  // p1.setName("P1");
  // cat1.getProducts().add(p1);
  // Product p2 = Model1Factory.eINSTANCE.createProduct();
  // p2.setName("P2");
  // cat1.getProducts().add(p2);
  // Product p3 = Model1Factory.eINSTANCE.createProduct();
  // p3.setName("P3");
  // cat2.getProducts().add(p3);
  // return cat1;
  // }
}
