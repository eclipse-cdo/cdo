/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.admin.protocol;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.signal.SignalProtocol;

/**
 * @author Eike Stepper
 */
public class JMSAdminProtocol extends SignalProtocol<Object>
{
  public JMSAdminProtocol(IConnector connector)
  {
    super(JMSAdminProtocolConstants.PROTOCOL_NAME);
    open(connector);
  }
}
