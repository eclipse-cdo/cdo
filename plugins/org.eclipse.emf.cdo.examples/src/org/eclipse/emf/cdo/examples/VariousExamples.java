/*
 * Copyright (c) 2009-2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class VariousExamples
{
  public static void openSessionAndUseTransaction() throws CommitException
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", "repos.foo.org:2036");

    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("MyRepo");

    CDONet4jSession session = configuration.openNet4jSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    // Work with the resource set....
    transaction.commit();
    session.close();
  }

  public static void registerSessionWithPluginContainer() throws CommitException
  {
    CDONet4jSession session = (CDONet4jSession)IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.sessions", "cdo", "tcp://repos.foo.org:2036/MyRepo");
    CDOTransaction transaction = session.openTransaction();
    // ...
    transaction.commit();
  }
}
