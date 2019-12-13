/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server.protocol.admin;

import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class JMSAdminServerProtocolFactory extends ServerProtocolFactory
{
  public static final String TYPE = JMSAdminProtocolConstants.PROTOCOL_NAME;

  public JMSAdminServerProtocolFactory()
  {
    super(TYPE);
  }

  @Override
  public JMSAdminServerProtocol create(String description)
  {
    return new JMSAdminServerProtocol();
  }

  public static JMSAdminServerProtocol get(IManagedContainer container, String description)
  {
    return (JMSAdminServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
