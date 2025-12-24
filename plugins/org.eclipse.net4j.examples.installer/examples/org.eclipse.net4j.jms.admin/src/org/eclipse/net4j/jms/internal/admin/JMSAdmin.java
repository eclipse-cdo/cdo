/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.admin;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.jms.admin.IJMSAdmin;
import org.eclipse.net4j.jms.internal.admin.bundle.OM;
import org.eclipse.net4j.jms.internal.admin.protocol.JMSAdminProtocol;
import org.eclipse.net4j.jms.internal.admin.protocol.JMSCreateDestinationRequest;

/**
 * @author Eike Stepper
 */
public class JMSAdmin implements IJMSAdmin
{
  private JMSAdminProtocol protocol;

  public JMSAdmin(IConnector connector)
  {
    protocol = new JMSAdminProtocol(connector);
  }

  @Override
  public void close()
  {
    protocol.close();
    protocol = null;
  }

  @Override
  public boolean createQueue(String name)
  {
    return createDestination(name, JMSAdminProtocolConstants.DESTINATION_TYPE_QUEUE);
  }

  @Override
  public boolean createTopic(String name)
  {
    return createDestination(name, JMSAdminProtocolConstants.DESTINATION_TYPE_TOPIC);
  }

  private boolean createDestination(String name, byte type)
  {
    try
    {
      return new JMSCreateDestinationRequest(protocol, type, name).send();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return false;
    }
  }
}
