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
package org.eclipse.net4j.examples.echo.client;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoClientProtocol extends SignalProtocol implements EchoProtocol
{
  public EchoClientProtocol(IConnector connector)
  {
    open(connector);
  }

  public String getType()
  {
    return PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    // On client side only needed for server initiated requests
    return null;
  }
}
