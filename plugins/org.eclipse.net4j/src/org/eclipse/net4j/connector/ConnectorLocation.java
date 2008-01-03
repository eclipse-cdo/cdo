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
package org.eclipse.net4j.connector;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.protocol.IProtocol;

/**
 * Enumerates the roles that an {@link IConnector} can play during the establishment of a physical connection.
 * <p>
 * An active connector that is created by a client application plays a {@link #CLIENT} role and a passive connector that
 * is created by an {@link org.eclipse.net4j.acceptor.IAcceptor IAcceptor} in response to a call to one of the active
 * connector's connect methods plays a {@link #SERVER} role.
 * <p>
 * Note however that the role of a connector is only meaningful to determine how a physical connection has been
 * <em>established</em>. Once it has been established actual communication (opening {@link IChannel}s, sending and
 * receiving {@link IBuffer}s) can occur in both directions without limitation. Values of this enumeration do not
 * necessarily permit general assumption about whether a connector is located in a typical client or server environment.
 * 
 * @see IConnector#connect(long)
 * @see IConnector#connectAsync()
 * @see IConnector#openChannel()
 * @see IConnector#openChannel(IProtocol)
 * @see IConnector#openChannel(String, Object)
 * @see IChannel#sendBuffer(IBuffer)
 * @see IChannel#setReceiveHandler(IBufferHandler)
 * @author Eike Stepper
 */
public enum ConnectorLocation
{
  CLIENT, SERVER
}
