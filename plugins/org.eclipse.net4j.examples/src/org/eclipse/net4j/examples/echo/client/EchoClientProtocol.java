/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.examples.echo.client;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.signal.SignalProtocol;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoClientProtocol extends SignalProtocol<Object> implements EchoProtocol
{
  public EchoClientProtocol(IConnector connector)
  {
    super(PROTOCOL_NAME);
    open(connector);
  }
}
