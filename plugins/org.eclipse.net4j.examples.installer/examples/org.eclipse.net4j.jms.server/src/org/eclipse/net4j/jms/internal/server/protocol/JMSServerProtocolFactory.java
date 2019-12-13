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
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class JMSServerProtocolFactory extends ServerProtocolFactory
{
  public static final String TYPE = JMSProtocolConstants.PROTOCOL_NAME;

  public JMSServerProtocolFactory()
  {
    super(TYPE);
  }

  @Override
  public JMSServerProtocol create(String description)
  {
    return new JMSServerProtocol();
  }

  public static JMSServerProtocol get(IManagedContainer container, String description)
  {
    return (JMSServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
