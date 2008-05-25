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

import org.eclipse.net4j.http.IHTTPConnector;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.connector.Connector;

/**
 * @author Eike Stepper
 */
public abstract class HTTPConnector extends Connector implements IHTTPConnector
{
  private String connectorID;

  public HTTPConnector()
  {
  }

  public String getConnectorID()
  {
    return connectorID;
  }

  public void setConnectorID(String connectorID)
  {
    this.connectorID = connectorID;
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }
}
