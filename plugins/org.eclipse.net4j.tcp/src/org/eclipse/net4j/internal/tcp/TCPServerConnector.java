/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
public class TCPServerConnector extends TCPConnector
{
  public TCPServerConnector()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.SERVER;
  }

  public String getHost()
  {
    try
    {
      return getSocketChannel().socket().getInetAddress().getHostAddress();
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  public int getPort()
  {
    try
    {
      return getSocketChannel().socket().getPort();
    }
    catch (RuntimeException ex)
    {
      return 0;
    }
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("ServerTCPConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }
    else
    {
      return MessageFormat.format("ServerTCPConnector[{3}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
  }
}
