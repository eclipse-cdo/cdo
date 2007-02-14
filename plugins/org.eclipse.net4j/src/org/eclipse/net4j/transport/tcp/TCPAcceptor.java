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
package org.eclipse.net4j.transport.tcp;

import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface TCPAcceptor
{
  public static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$

  public static final int DEFAULT_PORT = 2036;

  public String getAddress();

  public int getPort();

  public Connector[] getAcceptedConnectors();

  public IRegistry<String, ProtocolFactory> getProtocolFactoryRegistry();

  public void setProtocolFactoryRegistry(IRegistry<String, ProtocolFactory> protocolFactoryRegistry);

  public void addAcceptorListener(TCPAcceptorListener listener);

  public void removeAcceptorListener(TCPAcceptorListener listener);

  public ExecutorService getReceiveExecutor();

  public void setReceiveExecutor(ExecutorService receiveExecutor);
}
