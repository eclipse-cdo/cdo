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

import org.eclipse.net4j.tcp.ITCPConstants;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.internal.net4j.transport.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class TCPAcceptorFactory extends AcceptorFactory<TCPAcceptor>
{
  public static final String SEPARATOR = ":"; //$NON-NLS-1$

  public TCPAcceptorFactory()
  {
    super(ITCPConstants.TYPE);
  }

  public TCPAcceptor create(String description)
  {
    String address = ITCPConstants.DEFAULT_ADDRESS;
    int port = ITCPConstants.DEFAULT_PORT;

    if (!StringUtil.isEmpty(description))
    {
      String[] tokens = description.split(SEPARATOR);
      if (!StringUtil.isEmpty(tokens[0]))
      {
        address = tokens[0];
      }

      if (tokens.length > 1 && !StringUtil.isEmpty(tokens[1]))
      {
        port = Integer.parseInt(tokens[1]);
      }
    }

    TCPAcceptor acceptor = new TCPAcceptor();
    acceptor.setAddress(address);
    acceptor.setPort(port);
    return acceptor;
  }

  @Override
  public String getDescriptionFor(TCPAcceptor acceptor)
  {
    return acceptor.getAddress() + SEPARATOR + acceptor.getPort();
  }
}
