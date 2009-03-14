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
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public interface ISessionConfig extends IConfig
{
  public void startTransport() throws Exception;

  public void stopTransport() throws Exception;

  public IAcceptor getAcceptor();

  public IConnector getConnector();

  public CDOSession openMangoSession();

  public CDOSession openModel1Session();

  public CDOSession openModel2Session();

  public CDOSession openModel3Session();

  public CDOSession openSession(EPackage ePackage);

  public CDOSession openSession(String repositoryName);

  public CDOSession openSession();
}
