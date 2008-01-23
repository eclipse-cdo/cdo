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

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

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

  public static void main(String[] args)
  {
    // Prepare the standalone infra structure (not needed when running inside Eclipse)
    IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
    Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
    JVMUtil.prepareContainer(container); // Prepare the JVM transport
    CDOServerUtil.prepareContainer(container); // Prepare the CDO server
    CDOUtil.prepareContainer(container, false); // Prepare the CDO client

    // Start the transport and create a repository
    JVMUtil.getAcceptor(container, "default"); // Start the JVM transport
    CDOServerUtil.addRepository(container, createRepository()); // Start a CDO respository

    // Establish a communications connection and open a session with the repository
    IConnector connector = JVMUtil.getConnector(container, "default"); // Open a JVM connection
    CDOSession session = CDOUtil.openSession(connector, REPOSITORY_NAME, true);// Open a CDO session
    CDOTransaction transaction = session.openTransaction();// Open a CDO transaction
    Resource resource = transaction.createResource("/my/big/resource");// Create a new EMF resource

    // Work normally with the EMF resource
    for (int i = 0; i < 100; i++)
    {
      EObject inputModel = getInputModel(i);
      resource.getContents().add(inputModel);
      transaction.commit();
    }

    session.close();
  }

  private static IRepository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.PROP_SUPPORTING_REVISION_DELTAS, "true");
    props.put(IRepository.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(IRepository.PROP_REVISED_LRU_CAPACITY, "10000");
    return CDOServerUtil.createRepository(REPOSITORY_NAME, createStore(), props);
  }

  private static IStore createStore()
  {
    // You might want to create an IDBStore here instead if memory is an issue!
    return StoreUtil.createMEMStore();
  }

  private static EObject getInputModel(int i)
  {
    throw new UnsupportedOperationException("Load you model here");
  }
}
