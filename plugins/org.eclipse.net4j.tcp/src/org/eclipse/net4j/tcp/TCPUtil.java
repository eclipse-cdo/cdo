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
package org.eclipse.net4j.tcp;

import org.eclipse.internal.net4j.transport.DescriptionUtil;

/**
 * @author Eike Stepper
 */
public final class TCPUtil
{
  private TCPUtil()
  {
  }

  public static String createAcceptorDescription()
  {
    return createAcceptorDescription(TCPConstants.DEFAULT_ADDRESS);
  }

  public static String createAcceptorDescription(String address)
  {
    return createAcceptorDescription(address, TCPConstants.DEFAULT_PORT);
  }

  public static String createAcceptorDescription(String address, int port)
  {
    Object[] elements = { address, port };
    return DescriptionUtil.getDescription(TCPConstants.TYPE, elements);
  }

  public static String createConnectorDescription(String host)
  {
    return createConnectorDescription(host, null);
  }

  public static String createConnectorDescription(String host, int port)
  {
    return createConnectorDescription(host, port, null);
  }

  public static String createConnectorDescription(String host, String userName)
  {
    return createConnectorDescription(host, TCPConstants.DEFAULT_PORT, userName);
  }

  public static String createConnectorDescription(String host, int port, String userName)
  {
    Object[] elements = { host, port, userName };
    return DescriptionUtil.getDescription(TCPConstants.TYPE, elements);
  }
}
