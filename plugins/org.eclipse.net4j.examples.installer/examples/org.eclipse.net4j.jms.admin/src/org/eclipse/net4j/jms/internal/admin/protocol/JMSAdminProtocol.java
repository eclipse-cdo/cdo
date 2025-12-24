/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
