/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.tcp;

import org.eclipse.net4j.transport.tcp.TCPConnectorDescription;
import org.eclipse.net4j.util.ObjectUtil;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class TCPConnectorDescriptionImpl implements TCPConnectorDescription, Cloneable, Serializable
{
  private static final long serialVersionUID = 1L;

  private String host;

  private int port = DEFAULT_PORT;

  public TCPConnectorDescriptionImpl()
  {
  }

  public String getHost()
  {
    return host;
  }

  public void setHost(String host)
  {
    this.host = host;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public String getDisplayName()
  {
    return MessageFormat.format("{0}:{1}", host, port);
  }

  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    TCPConnectorDescriptionImpl description = new TCPConnectorDescriptionImpl();
    description.setHost(host);
    description.setPort(port);
    return description;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof TCPConnectorDescription)
    {
      TCPConnectorDescription that = (TCPConnectorDescription)obj;
      return this.port == that.getPort() && ObjectUtil.equals(this.host, that.getHost());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return port ^ host.hashCode();
  }

  @Override
  public String toString()
  {
    return getDisplayName();
  }
}
