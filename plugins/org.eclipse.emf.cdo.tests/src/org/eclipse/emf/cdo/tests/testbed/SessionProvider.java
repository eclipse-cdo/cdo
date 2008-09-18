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
package org.eclipse.emf.cdo.tests.testbed;

import org.eclipse.emf.cdo.CDOSession;

import org.eclipse.net4j.connector.IConnector;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public interface SessionProvider
{
  public static final String REPOSITORY_NAME = "repo1";

  public IConnector getConnector();

  public CDOSession openMangoSession();

  public CDOSession openModel1Session();

  public CDOSession openModel2Session();

  public CDOSession openModel3Session();

  public CDOSession openSession(EPackage ePackage);
}
