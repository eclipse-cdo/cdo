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
package org.eclipse.net4j.jms.internal.admin.protocol;

import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.protocol.ClientProtocolFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class JMSAdminProtocolFactory extends ClientProtocolFactory
{
  public static final String TYPE = JMSAdminProtocolConstants.PROTOCOL_NAME;

  public JMSAdminProtocolFactory()
  {
    super(TYPE);
  }

  public JMSAdminProtocol create(String description)
  {
    return new JMSAdminProtocol();
  }

  public static JMSAdminProtocol get(IManagedContainer container, String description)
  {
    return (JMSAdminProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
