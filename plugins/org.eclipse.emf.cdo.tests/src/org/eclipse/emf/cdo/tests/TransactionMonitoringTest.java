/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Eike Stepper
 */
public class TransactionMonitoringTest
{
  public static void main(String[] args)
  {
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    CDOUtil.prepareContainer(container);
    container.activate();

    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(TCPUtil.getConnector(container, "localhost"));
    configuration.setRepositoryName("repo1");

    CDOSession session = configuration.openSession();
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource("/my/big/resource");
    resource.getContents().add(Model1Factory.eINSTANCE.createCompany());
    transaction.commit();

    System.out.println();
    System.out.println("FINISHED");
    System.out.println();

    session.close();
    container.deactivate();
  }
}
