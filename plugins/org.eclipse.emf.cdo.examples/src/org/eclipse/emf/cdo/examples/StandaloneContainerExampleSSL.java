/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class StandaloneContainerExampleSSL
{
  public static void main(String[] args) throws CommitException
  {
    // Enable logging and tracing
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);

    // Prepare container
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container); // Register Net4j factories
    SSLUtil.prepareContainer(container);
    CDONet4jUtil.prepareContainer(container); // Register CDO factories
    container.activate();

    // Create connector
    IConnector connector = SSLUtil.getConnector(container, "localhost:2036");

    // Create configuration
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("repo1"); //$NON-NLS-1$

    // Open session
    CDOSession session = configuration.openNet4jSession();
    session.options().setGeneratedPackageEmulationEnabled(true);

    session.getPackageRegistry().putEPackage(CompanyPackage.eINSTANCE);

    // Open transaction
    CDOTransaction transaction = session.openTransaction();

    // Read whatetever is there
    TreeIterator<EObject> iter = transaction.getRootResource().getAllContents();
    while (iter.hasNext())
    {
      System.out.println("---> It's a " + iter.next().eClass().getName());
    }

    // Get or create resource
    CDOResource resource = transaction.getOrCreateResource("/path/to/my/resource"); //$NON-NLS-1$

    // Work with the resource and commit the transaction
    EObject object = CompanyFactory.eINSTANCE.createCompany();
    resource.getContents().add(object);
    transaction.commit();

    // Cleanup
    session.close();
    connector.close();
    container.deactivate();
  }
}
