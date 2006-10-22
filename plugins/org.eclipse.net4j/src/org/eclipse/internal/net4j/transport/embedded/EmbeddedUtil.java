/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.embedded;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.tcp.TCPSelector;

/**
 * @author Eike Stepper
 */
public final class EmbeddedUtil
{
  private EmbeddedUtil()
  {
  }

  public static Connector createConnector(BufferProvider bufferProvider, TCPSelector selector,
      String host, int port)
  {
    ClientEmbeddedConnectorImpl connector = new ClientEmbeddedConnectorImpl();
    connector.setBufferProvider(bufferProvider);
    return connector;
  }
}
