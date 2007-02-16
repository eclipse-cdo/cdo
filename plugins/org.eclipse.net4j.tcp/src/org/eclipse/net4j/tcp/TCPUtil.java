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
    return createConnectorDescription(null, host);
  }

  public static String createConnectorDescription(String host, int port)
  {
    return createConnectorDescription(null, host, port);
  }

  public static String createConnectorDescription(String userName, String host)
  {
    return createConnectorDescription(userName, host, TCPConstants.DEFAULT_PORT);
  }

  public static String createConnectorDescription(String userName, String host, int port)
  {
    Object[] elements = { userName, host, port };
    return DescriptionUtil.getDescription(TCPConstants.TYPE, elements);
  }
}
