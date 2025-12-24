/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms;

import org.eclipse.net4j.internal.jms.ConnectionFactoryImpl;
import org.eclipse.net4j.util.container.IManagedContainer;

import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;

import java.util.Hashtable;

/**
 * @author Eike Stepper
 */
public class JMSInitialContext extends InitialContext
{
  private IManagedContainer transportContainer;

  public JMSInitialContext(IManagedContainer transportContainer) throws NamingException
  {
    this.transportContainer = transportContainer;
  }

  public JMSInitialContext(boolean lazy, IManagedContainer transportContainer) throws NamingException
  {
    super(lazy);
    this.transportContainer = transportContainer;
  }

  public JMSInitialContext(Hashtable<?, ?> environment, IManagedContainer transportContainer) throws NamingException
  {
    super(environment);
    this.transportContainer = transportContainer;
  }

  public IManagedContainer getTransportContainer()
  {
    return transportContainer;
  }

  @Override
  public Object lookup(Name name) throws NamingException
  {
    return postProcess(super.lookup(name));
  }

  @Override
  public Object lookup(String name) throws NamingException
  {
    return postProcess(super.lookup(name));
  }

  @Override
  public Object lookupLink(Name name) throws NamingException
  {
    return postProcess(super.lookupLink(name));
  }

  @Override
  public Object lookupLink(String name) throws NamingException
  {
    return postProcess(super.lookupLink(name));
  }

  protected Object postProcess(Object object)
  {
    if (object instanceof ConnectionFactoryImpl)
    {
      ConnectionFactoryImpl factory = (ConnectionFactoryImpl)object;
      factory.setTransportContainer(transportContainer);
    }

    return object;
  }
}
