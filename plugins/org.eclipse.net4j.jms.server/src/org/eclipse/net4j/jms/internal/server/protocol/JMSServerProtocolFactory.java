/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;

import org.eclipse.internal.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class JMSServerProtocolFactory extends ServerProtocolFactory
{
  public JMSServerProtocolFactory()
  {
    super(JMSProtocolConstants.PROTOCOL_NAME);
  }

  public Object create(String description)
  {
    return new JMSServerProtocol();
  }
}