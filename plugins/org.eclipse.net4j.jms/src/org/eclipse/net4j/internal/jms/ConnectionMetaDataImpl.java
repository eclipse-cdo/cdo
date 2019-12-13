/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.ConnectionMetaData;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class ConnectionMetaDataImpl implements ConnectionMetaData
{
  private ConnectionImpl connection;

  public ConnectionMetaDataImpl(ConnectionImpl connection)
  {
    this.connection = connection;
  }

  public ConnectionImpl getConnection()
  {
    return connection;
  }

  @Override
  public int getJMSMajorVersion()
  {
    return 1;
  }

  @Override
  public int getJMSMinorVersion()
  {
    return 1;
  }

  @Override
  public String getJMSVersion()
  {
    return "1.1"; //$NON-NLS-1$
  }

  @Override
  public String getJMSProviderName()
  {
    return "Net4j JMS"; //$NON-NLS-1$
  }

  @Override
  public int getProviderMajorVersion()
  {
    return 0;
  }

  @Override
  public int getProviderMinorVersion()
  {
    return 8;
  }

  @Override
  public String getProviderVersion()
  {
    return "0.8.0"; //$NON-NLS-1$
  }

  @Override
  public Enumeration<?> getJMSXPropertyNames()
  {
    return new StringTokenizer(""); //$NON-NLS-1$
  }
}
