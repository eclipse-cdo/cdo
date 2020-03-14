/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.connector.IServerConnector;

import java.net.URI;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class WSServerConnector extends WSConnector implements IServerConnector
{
  private WSAcceptor acceptor;

  public WSServerConnector(WSAcceptor acceptor)
  {
    this.acceptor = acceptor;
  }

  @Override
  public Location getLocation()
  {
    return Location.SERVER;
  }

  @Override
  public URI getServiceURI()
  {
    try
    {
      return getWebSocket().getSession().getUpgradeRequest().getRequestURI();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  @Override
  public String getAcceptorName()
  {
    try
    {
      return acceptor.getName();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  @Override
  public WSAcceptor getAcceptor()
  {
    return acceptor;
  }

  @Override
  public String getURL()
  {
    try
    {
      String serviceURI = getServiceURI().toString();
      if (!serviceURI.endsWith("/")) //$NON-NLS-1$
      {
        serviceURI += "/"; //$NON-NLS-1$
      }

      return serviceURI + ACCEPTOR_NAME_PREFIX + getAcceptorName();
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("WSServerConnector[{0}]", getURL()); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    leaveConnecting();
  }
}
