/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ImportXML
{
  private static final String REPOSITORY_NAME = "repo1";

  public static void main(String[] args) throws CommitException
  {
    // Turn on tracing
    OMPlatform.INSTANCE.setDebugging(true);

    // Prepare the standalone infra structure (not needed when running inside Eclipse)
    IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
    Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
    JVMUtil.prepareContainer(container); // Prepare the JVM transport
    CDONet4jServerUtil.prepareContainer(container); // Prepare the CDO server
    CDONet4jUtil.prepareContainer(container); // Prepare the CDO client
    container.activate();

    // Start the transport and create a repository
    JVMUtil.getAcceptor(container, "default"); // Start the JVM transport
    CDOServerUtil.addRepository(container, createRepository()); // Start a CDO repository

    // Establish a communications connection and open a session with the repository
    IConnector connector = JVMUtil.getConnector(container, "default"); // Open a JVM connection
    CDOSession session = openSession(connector);// Open a CDO session
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);// Not needed after first commit!!!

    CDOTransaction transaction = session.openTransaction();// Open a CDO transaction
    Resource resource = transaction.createResource("/my/big/resource");// Create a new EMF resource

    // Work normally with the EMF resource
    EObject inputModel = getInputModel();
    resource.getContents().add(inputModel);
    transaction.commit();
    session.close();
    connector.close();
  }

  private static IRepository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    return CDOServerUtil.createRepository(REPOSITORY_NAME, createStore(), props);
  }

  private static IStore createStore()
  {
    // You might want to create an IDBStore here instead if memory is an issue!
    return MEMStoreUtil.createMEMStore();
  }

  private static EObject getInputModel()
  {
    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    cat1.setName("CAT1");
    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    cat2.setName("CAT2");
    cat1.getCategories().add(cat2);
    Product1 p1 = Model1Factory.eINSTANCE.createProduct1();
    p1.setName("P1");
    cat1.getProducts().add(p1);
    Product1 p2 = Model1Factory.eINSTANCE.createProduct1();
    p2.setName("P2");
    cat1.getProducts().add(p2);
    Product1 p3 = Model1Factory.eINSTANCE.createProduct1();
    p3.setName("P3");
    cat2.getProducts().add(p3);
    return cat1;
  }

  protected static CDOSession openSession(IConnector connector)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(REPOSITORY_NAME);
    return configuration.openSession();
  }
}
