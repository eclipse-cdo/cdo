/*
 * Copyright (c) 2007, 2008, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Encapsulates a Java {@link Selector socket selector} and orchestrates a number of {@link ITCPActiveSelectorListener
 * active} and {@link ITCPPassiveSelectorListener passive} consumers, usually {@link IConnector connectors} and
 * {@link IAcceptor acceptors} that compete for the socket selector's I/O time.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITCPSelector
{
  public Selector getSocketSelector();

  public void orderRegistration(ServerSocketChannel channel, ITCPPassiveSelectorListener listener);

  public void orderRegistration(SocketChannel channel, boolean client, ITCPActiveSelectorListener listener);

  public void orderConnectInterest(SelectionKey selectionKey, boolean client, boolean on);

  public void orderReadInterest(SelectionKey selectionKey, boolean client, boolean on);

  public void orderWriteInterest(SelectionKey selectionKey, boolean client, boolean on);
}
