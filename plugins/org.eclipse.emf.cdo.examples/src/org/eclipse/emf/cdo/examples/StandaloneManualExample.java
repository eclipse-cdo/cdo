/*
 * Copyright (c) 2008-2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.FactoriesProtocolProvider;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.ecore.EObject;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class StandaloneManualExample
{
  public static void main(String[] args) throws CommitException
  {
    // Enable logging and tracing
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);

    // Prepare receiveExecutor
    ExecutorService receiveExecutor = ThreadPool.create();

    // Prepare bufferProvider
    IBufferProvider bufferProvider = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferProvider);

    IProtocolProvider protocolProvider = new FactoriesProtocolProvider(new org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocolFactory());

    // Prepare selector
    org.eclipse.net4j.internal.tcp.TCPSelector selector = new org.eclipse.net4j.internal.tcp.TCPSelector();
    selector.activate();

    // Prepare connector
    org.eclipse.net4j.internal.tcp.TCPClientConnector connector = new org.eclipse.net4j.internal.tcp.TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferProvider);
    connector.getConfig().setReceiveExecutor(receiveExecutor);
    connector.getConfig().setProtocolProvider(protocolProvider);
    connector.getConfig().setNegotiator(null);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(2036);
    connector.activate();

    // Create configuration
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("repo1"); //$NON-NLS-1$

    // Open session
    CDOSession session = configuration.openNet4jSession();
    session.getPackageRegistry().putEPackage(CompanyPackage.eINSTANCE);

    // Open transaction
    CDOTransaction transaction = session.openTransaction();

    // Get or create resource
    CDOResource resource = transaction.getOrCreateResource("/path/to/my/resource"); //$NON-NLS-1$

    // Work with the resource and commit the transaction
    EObject object = CompanyFactory.eINSTANCE.createCompany();
    resource.getContents().add(object);
    transaction.commit();

    // Cleanup
    session.close();
    connector.deactivate();
  }
}
