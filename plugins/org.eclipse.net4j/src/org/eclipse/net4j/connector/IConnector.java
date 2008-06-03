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
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.List;

/**
 * One endpoint of a physical connection of arbitrary nature between two communicating parties. A {@link IConnector}
 * encapsulates the process of establishing and closing such connections and has a {@link ConnectorLocation} of
 * {@link ConnectorLocation#CLIENT CLIENT} or {@link ConnectorLocation#SERVER SERVER} with respect to this process. Once
 * a connection is established either party can use its connector to open multiple {@link IChannel}s to asynchronously
 * exchange {@link IBuffer}s.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients. Providers of connectors for new physical
 * connection types have to implement org.eclipse.internal.net4j.connector.InternalConnector.
 * <p>
 * <dt><b>Class Diagram:</b></dt>
 * <dd><img src="doc-files/Connectors.png" title="Diagram Connectors" border="0" usemap="Connectors.png"/></dd>
 * <p>
 * <MAP NAME="Connectors.png"> <AREA SHAPE="RECT" COORDS="259,15,400,75" HREF="IConnectorCredentials.html"> <AREA
 * SHAPE="RECT" COORDS="12,174,138,245" HREF="ConnectorLocation.html"> <AREA SHAPE="RECT" COORDS="258,139,401,281"
 * HREF="IConnector.html"> <AREA SHAPE="RECT" COORDS="518,156,642,263" HREF="ConnectorState.html"> <AREA SHAPE="RECT"
 * COORDS="280,360,380,410" HREF="IChannel.html"> </MAP>
 * <p>
 * <dt><b>Sequence Diagram: Communication Process</b></dt>
 * <dd><img src="doc-files/CommunicationProcess.jpg" title="Communication Process" border="0"
 * usemap="#CommunicationProcess.jpg"/></dd>
 * <p>
 * <MAP NAME="CommunicationProcess.jpg"> <AREA SHAPE="RECT" COORDS="128,94,247,123" HREF="IConnector.html"> <AREA
 * SHAPE="RECT" COORDS="648,95,767,123" HREF="IConnector.html"> <AREA SHAPE="RECT" COORDS="509,254,608,283"
 * HREF="IChannel.html"> <AREA SHAPE="RECT" COORDS="287,355,387,383" HREF="IChannel.html"> <AREA SHAPE="RECT"
 * COORDS="818,195,897,222" HREF="IProtocol.html"> </MAP>
 * 
 * @author Eike Stepper
 */
public interface IConnector extends IContainer<IChannel>
{
  public String getURL();

  /**
   * Indicates which role this connector has played during the establishment of the physical connection.
   */
  public ConnectorLocation getLocation();

  /**
   * Same as <code>{@link #getLocation()} == {@link ConnectorLocation#CLIENT}</code>.
   */
  public boolean isClient();

  /**
   * Same as <code>{@link #getLocation()} == {@link ConnectorLocation#SERVER}</code>.
   */
  public boolean isServer();

  /**
   * Returns the userID of this connector.
   */
  public String getUserID();

  /**
   * Returns the factory registry used by this connector to lookup factories that can create {@link IProtocol}s for
   * newly opened {@link IChannel}s.
   * <p>
   * Automatic protocol creation only happens if {@link #isServer()} returns <code>true</code>.
   */
  public IRegistry<IFactoryKey, IFactory> getProtocolFactoryRegistry();

  /**
   * Returns the post processors used by this connector to modify protocols created for new channels.
   */
  public List<IElementProcessor> getProtocolPostProcessors();

  /**
   * Returns the current state of this onnector.
   */
  public ConnectorState getState();

  /**
   * Same as <code>{@link #getState()} == {@link ConnectorState#CONNECTED}</code>.
   */
  public boolean isConnected();

  /**
   * Asynchronous connect. May leave this {@link IConnector} in a state where <code>{@link #isConnected()} == false
   * </code>.
   * <p>
   */
  public void connectAsync() throws ConnectorException;

  /**
   * Blocks until <code>{@link #isConnected()} == true</code> or the given timeout expired.
   * <p>
   * 
   * @throws ConnectorException
   */
  public boolean waitForConnection(long timeout) throws ConnectorException;

  /**
   * Synchronous connect. Blocks until <code>{@link #isConnected()} == true</code> or the given timeout expired.
   * <p>
   */
  public boolean connect(long timeout) throws ConnectorException;

  public ConnectorException disconnect();

  /**
   * Returns an array of currently open channels. Note that the resulting array does not contain <code>null</code>
   * values. Generally the <code>channelIndex</code> of a channel can not be used as an index into this array.
   * <p>
   */
  public IChannel[] getChannels();

  /**
   * Synchronous request to open a new {@link IChannel} with an undefined channel protocol. Since the peer connector
   * can't lookup a protocol {@link IFactory factory} without a protocol identifier the {@link IBufferHandler} of the
   * peer {@link IChannel} can only be provided by externally provided channel {@link ILifecycle lifecycle}
   * {@link IListener listeners}.
   * <p>
   * 
   * @see #openChannel(String, Object)
   * @see #openChannel(IProtocol)
   */
  public IChannel openChannel() throws ConnectorException;

  /**
   * Synchronous request to open a new {@link IChannel} with a channel protocol defined by a given protocol identifier.
   * The peer connector will lookup a protocol {@link IFactory factory} with the protocol identifier, create a
   * {@link IBufferHandler} and inject it into the peer {@link IChannel}.
   * <p>
   * 
   * @see #openChannel()
   * @see #openChannel(IProtocol)
   */
  public IChannel openChannel(String protocolID, Object infraStructure) throws ConnectorException;

  /**
   * Synchronous request to open a new {@link IChannel} with the given channel protocol . The peer connector will lookup
   * a protocol {@link IFactory factory} with the protocol identifier, create a {@link IBufferHandler} and inject it
   * into the peer channel.
   * <p>
   * 
   * @see #openChannel()
   * @see #openChannel(String, Object)
   */
  public IChannel openChannel(IProtocol protocol) throws ConnectorException;
}
