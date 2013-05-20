/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.ecore.EObject;

/**
 * Understanding the Architecture of a Client Application
 * <p>
 * The architecture of a CDO application is characterized by its mandatory dependency on EMF, the Eclipse Modeling
 * Framework. Most of the time an application interacts with the object graph of the model through standard EMF APIs
 * because CDO model graph objects are {@link EObject EObjects}. While CDO's basic functionality integrates nicely and
 * transparently with EMF's extension mechansims some of the more advanced functions may require to add direct
 * dependendcies on CDO to your application code.
 * <p>
 * The following diagram illustrates the major building blocks of a CDO application: {@img application-architecture.png}
 * 
 * @author Eike Stepper
 */
public class Architecture
{
  /**
   * OSGi
   * <p>
   * The <i>Open Services Gateway Initiative</i> (OSGi)...
   */
  public class OSGi
  {
  }

  /**
   * EMF
   */
  public class EMF
  {
  }

  /**
   * CDO Client
   */
  public class Client
  {
  }

  /**
   * Net4j Core
   */
  public class Net4j
  {
  }

  /**
   * Models
   */
  public class Models
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
