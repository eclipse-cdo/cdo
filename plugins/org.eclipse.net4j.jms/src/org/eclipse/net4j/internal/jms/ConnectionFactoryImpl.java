/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.util.container.IManagedContainer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import java.io.Serializable;
import java.rmi.Remote;

public class ConnectionFactoryImpl implements ConnectionFactory, Remote, Serializable
{
  private static final long serialVersionUID = 1L;

  private String connectorType;

  private String connectorDescription;

  private Object transportContainer;

  public ConnectionFactoryImpl(String connectorType, String connectorDescription)
  {
    this.connectorType = connectorType;
    this.connectorDescription = connectorDescription;
  }

  public String getConnectorType()
  {
    return connectorType;
  }

  public String getConnectorDescription()
  {
    return connectorDescription;
  }

  public Object getTransportContainer()
  {
    return transportContainer;
  }

  public void setTransportContainer(Object transportContainer)
  {
    this.transportContainer = transportContainer;
  }

  @Override
  public Connection createConnection() throws JMSException
  {
    return createConnection(null, null);
  }

  @Override
  public Connection createConnection(String userName, String password) throws JMSException
  {
    return new ConnectionImpl((IManagedContainer)transportContainer, connectorType, connectorDescription, userName, password);
  }
}
