/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.internal.server;

import org.eclipse.spi.net4j.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptorFactory extends AcceptorFactory
{
  public static final String TYPE = "http"; //$NON-NLS-1$

  public HTTPAcceptorFactory()
  {
    super(TYPE);
  }

  @Override
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
