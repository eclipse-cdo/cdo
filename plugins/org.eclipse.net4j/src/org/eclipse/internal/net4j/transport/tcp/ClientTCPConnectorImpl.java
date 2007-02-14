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

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author Eike Stepper
 */
public class ClientTCPConnectorImpl extends AbstractTCPConnector
{
  public ClientTCPConnectorImpl()
  {
  }

  public Type getType()
  {
    return Type.CLIENT;
  }

  @Override
  public String toString()
  {
    return "ClientTCPConnector[" + getDescription() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (getDescription() == null)
    {
      throw new IllegalStateException("getDescription() == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    TCPConnectorDescription description = (TCPConnectorDescription)getDescription();
    InetAddress addr = InetAddress.getByName(description.getHost());
    InetSocketAddress sAddr = new InetSocketAddress(addr, description.getPort());
    getSocketChannel().connect(sAddr);
  }
}
