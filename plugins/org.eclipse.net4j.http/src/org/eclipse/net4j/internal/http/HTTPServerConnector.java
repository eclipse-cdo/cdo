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
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.connector.ConnectorLocation;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class HTTPServerConnector extends HTTPConnector
{
  private HTTPAcceptor acceptor;

  private String connectorID;

  private long lastTraffic = System.currentTimeMillis();

  public HTTPServerConnector(HTTPAcceptor acceptor, String connectorID)
  {
    this.acceptor = acceptor;
    this.connectorID = connectorID;
  }

  public HTTPAcceptor getAcceptor()
  {
    return acceptor;
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.SERVER;
  }

  public String getURL()
  {
    return "agent://connector:" + connectorID;
  }

  public String getConnectorID()
  {
    return connectorID;
  }

  public long getLastTraffic()
  {
    return lastTraffic;
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("HTTPServerConnector[{0}]", connectorID); //$NON-NLS-1$
    }

    return MessageFormat.format("HTTPServerConnector[{1}@{0}]", connectorID, getUserID()); //$NON-NLS-1$
  }
}
