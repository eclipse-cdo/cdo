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
package org.eclipse.net4j.http.internal.server;

import org.eclipse.spi.net4j.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptorFactory extends AcceptorFactory
{
  public static final String TYPE = "http";

  public HTTPAcceptorFactory()
  {
    super(TYPE);
  }

  public HTTPAcceptor create(String description)
  {
    HTTPAcceptor acceptor = new HTTPAcceptor();
    return acceptor;
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    return null;
  }
}
