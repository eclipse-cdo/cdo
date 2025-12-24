/*
 * Copyright (c) 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.WSUtil;

import org.eclipse.spi.net4j.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class WSAcceptorFactory extends AcceptorFactory
{

  /**
   * Default acceptor name when acceptor tag in cdo-server.xml does not declare a listen address.
   */
  public static final String DEFAULT_ACCEPTOR_NAME = "default"; //$NON-NLS-1$

  public WSAcceptorFactory()
  {
    super(WSUtil.FACTORY_TYPE);
  }

  /**
   * Allows derived classes to override the TYPE identifier
   */
  protected WSAcceptorFactory(String type)
  {
    super(type);
  }

  @Override
  public WSAcceptor create(String description)
  {
    WSAcceptor acceptor = createAcceptor();
    if (StringUtil.isEmpty(description))
    {
      acceptor.setName(DEFAULT_ACCEPTOR_NAME);
    }
    else
    {
      acceptor.setName(description);
    }
    return acceptor;
  }

  protected WSAcceptor createAcceptor()
  {
    return new WSAcceptor();
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof IWSAcceptor)
    {
      IWSAcceptor acceptor = (IWSAcceptor)object;
      return acceptor.getName();
    }

    return null;
  }
}
