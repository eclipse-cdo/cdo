/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.ecore.EObject;

/**
 * Understanding the Architecture of a Repository
 * <p>
 * The main building block of a CDO repository is split into two layers, the generic repository layer that client
 * applications interact with and the database integration layer that providers can hook into to integrate their data
 * storage solutions with CDO. A number of such integrations already ship with CDO, making it possible to connect a
 * repository to all sorts of JDBC databases, Hibernate, Objectivity/DB, MongoDB or DB4O.
 * <p>
 * While technically a CDO repository depends on EMF this dependency is not of equal importance as it is in a CDO
 * application. In particular the generated application models are not required to be deployed to the server because a
 * CDO repository accesses models reflectively and the model objects are not implemented as {@link EObject EObjects} on
 * the server.
 * <p>
 * The following diagram illustrates the major building blocks of a CDO repository: {@img repository-architecture.png}
 */
public class Architecture
{
  /**
   * OSGi
   */
  public class OSGi
  {
  }

  /**
   * CDO Server Core
   */
  public class Core
  {
  }

  /**
   * CDO Store
   */
  public class Store
  {
  }

  /**
   * OCL
   */
  public class OCL
  {
  }

  /**
   * Net4j
   */
  public class Net4j
  {
  }

  /**
   * Protocol
   */
  public class Protocol
  {
  }

  /**
   * Transport
   */
  public class Transport
  {
  }
}
