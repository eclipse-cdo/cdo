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
package org.eclipse.net4j.jms.internal.admin;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.jms.admin.IJMSAdmin;
import org.eclipse.net4j.jms.internal.admin.bundle.OM;
import org.eclipse.net4j.jms.internal.admin.protocol.JMSCreateDestinationRequest;

/**
 * @author Eike Stepper
 */
public class JMSAdmin implements IJMSAdmin
{
  private IChannel channel;

  public JMSAdmin(IConnector connector)
  {
    channel = connector.openChannel(JMSAdminProtocolConstants.PROTOCOL_NAME, this);
  }

  public void close()
  {
    channel.close();
    channel = null;
  }

  public boolean createQueue(String name)
  {
    return createDestination(name, JMSAdminProtocolConstants.DESTINATION_TYPE_QUEUE);
  }

  public boolean createTopic(String name)
  {
    return createDestination(name, JMSAdminProtocolConstants.DESTINATION_TYPE_TOPIC);
  }

  private boolean createDestination(String name, byte type)
  {
    try
    {
      return new JMSCreateDestinationRequest(channel, type, name).send();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return false;
    }
  }
}
