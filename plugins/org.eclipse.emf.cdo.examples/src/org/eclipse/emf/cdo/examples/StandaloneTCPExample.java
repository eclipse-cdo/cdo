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
package org.eclipse.emf.cdo.examples;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class StandaloneTCPExample
{
  public static void main(String[] args)
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container); // Register Net4j factories
    TCPUtil.prepareContainer(container); // Register TCP factories
    CDOUtil.prepareContainer(container, false); // Register CDO factories
    IConnector connector = TCPUtil.getConnector(container, "localhost:2036");

    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("my-repo");

    CDOSession session = configuration.openSession();
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource("/path/to/my/resource");

    EObject object = Model1Factory.eINSTANCE.createCompany();
    resource.getContents().add(object);

    transaction.commit();
    session.close();
  }
}
