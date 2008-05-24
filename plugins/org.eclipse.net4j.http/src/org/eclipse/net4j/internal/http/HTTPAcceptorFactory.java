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

import org.eclipse.internal.net4j.acceptor.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptorFactory extends AcceptorFactory
{
  public static final String TYPE = "tcp";

  private static final String SEPARATOR = ":"; //$NON-NLS-1$

  public HTTPAcceptorFactory()
  {
    super(TYPE);
  }

  public HTTPAcceptor create(String description)
  {
    // String address = IHTTPAcceptor.DEFAULT_ADDRESS;
    // int port = IHTTPAcceptor.DEFAULT_PORT;
    //
    // if (!StringUtil.isEmpty(description))
    // {
    // String[] tokens = description.split(SEPARATOR);
    // if (!StringUtil.isEmpty(tokens[0]))
    // {
    // address = tokens[0];
    // }
    //
    // if (tokens.length > 1 && !StringUtil.isEmpty(tokens[1]))
    // {
    // port = Integer.parseInt(tokens[1]);
    // }
    // }

    HTTPAcceptor acceptor = new HTTPAcceptor();
    // acceptor.setAddress(address);
    // acceptor.setPort(port);
    return acceptor;
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    // if (object instanceof HTTPAcceptor)
    // {
    // HTTPAcceptor acceptor = (HTTPAcceptor)object;
    // return acceptor.getAddress() + SEPARATOR + acceptor.getPort();
    // }

    return null;

  }
}
