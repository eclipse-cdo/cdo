/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.transport.ConnectorLocation;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ServerTCPConnectorImpl extends AbstractTCPConnector
{
  public ServerTCPConnectorImpl()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.SERVER;
  }

  public String getHost()
  {
    return getSocketChannel().socket().getInetAddress().getHostAddress();
  }

  public int getPort()
  {
    return getSocketChannel().socket().getPort();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("ServerTCPConnector[{0}]", getDescription()); //$NON-NLS-1$ 
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    super.onDeactivate();
  }
}
