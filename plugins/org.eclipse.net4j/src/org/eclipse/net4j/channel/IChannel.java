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
package org.eclipse.net4j.channel;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.event.INotifier;

/**
 * A bidirectional communications channel for the asynchronous exchange of {@link IBuffer}s. A channel is lightweight
 * and virtual in the sense that it does not necessarily represent a single physical connection like a TCP socket
 * connection. The underlying physical connection is represented by a {@link IConnector}.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients. Providers of channels (for example for new
 * physical connection types) have to extend/subclass {@link org.eclipse.spi.net4j.InternalChannel InternalChannel}.
 * <p>
 * <dt><b>Class Diagram:</b></dt>
 * <dd><img src="doc-files/Channels.png" title="Diagram Buffers" border="0" usemap="#Channels.png"/></dd>
 * <p>
 * <MAP NAME="Channels.png"> <AREA SHAPE="RECT" COORDS="301,8,451,68" HREF="IChannelID.html"> <AREA SHAPE="RECT"
 * COORDS="301,141,451,211" HREF="IChannel.html"> <AREA SHAPE="RECT" COORDS="599,151,696,201"
 * HREF="IBufferHandler.html"> <AREA SHAPE="RECT" COORDS="7,151,96,201" HREF="IConnector.html"> </MAP>
 * <p>
 * <dt><b>Sequence Diagram: Communication Process</b></dt>
 * <dd><img src="doc-files/CommunicationProcess.jpg" title="Communication Process" border="0"
 * usemap="#CommunicationProcess.jpg"/></dd>
 * <p>
 * <MAP NAME="CommunicationProcess.jpg"> <AREA SHAPE="RECT" COORDS="128,94,247,123" HREF="IConnector.html"> <AREA
 * SHAPE="RECT" COORDS="648,95,767,123" HREF="IConnector.html"> <AREA SHAPE="RECT" COORDS="509,254,608,283"
 * HREF="IChannel.html"> <AREA SHAPE="RECT" COORDS="287,355,387,383" HREF="IChannel.html"> <AREA SHAPE="RECT"
 * COORDS="818,195,897,222" HREF="IProtocol.html"> </MAP>
 * <p>
 * An example for opening a channel on an {@link IConnector} and sending an {@link IBuffer}:
 * <p>
 * <pre style="background-color:#ffffc8; border-width:1px; border-style:solid; padding:.5em;"> // Open a channel
 * IChannel channel = connector.openChannel(); short channelIndex = channel.getChannelIndex(); // Fill a buffer Buffer
 * buffer = bufferProvider.getBuffer(); ByteBuffer byteBuffer = buffer.startPutting(channelIndex);
 * byteBuffer.putDouble(15.47); // Let the channel send the buffer without blocking channel.sendBuffer(buffer); </pre>
 * <p>
 * An example for receiving {@link IBuffer}s from channels on an {@link IConnector}:
 * <p>
 * <pre style="background-color:#ffffc8; border-width:1px; border-style:solid; padding:.5em;"> // Create a receive
 * handler final IBufferHandler receiveHandler = new IBufferHandler() { public void handleBuffer(IBuffer buffer) {
 * ByteBuffer byteBuffer = buffer.getByteBuffer(); IOUtil.OUT().println(&quot;Received &quot; + byteBuffer.getDouble());
 * buffer.release(); } }; // Set the receive handler to all new channels connector.addListener(new
 * ContainerEventAdapter() { protected void onAdded(IContainer container, Object element) { IChannel channel =
 * (IChannel)element; channel.setReceiveHandler(receiveHandler); } }); </pre>
 * 
 * @author Eike Stepper
 */
public interface IChannel extends IBufferHandler, INotifier
{
  /**
   * Returns the ID of this channel that is unique among all channels that the connector of this channel has ever
   * created or will ever create.
   */
  public int getChannelID();

  /**
   * Returns the index of this channel within the array of channels returned from the {@link IConnector#getChannels()
   * getChannels()} method of the connector of this channel.
   */
  public short getChannelIndex();

  /**
   * Asynchronously sends the given buffer to the receive handler of the peer channel.
   */
  public void sendBuffer(IBuffer buffer);

  /**
   * Returns the <code>IBufferHandler</code> that handles buffers received from the peer channel.
   */
  public IBufferHandler getReceiveHandler();

  /**
   * Sets the <code>IBufferHandler</code> to handle buffers received from the peer channel.
   */
  public void setReceiveHandler(IBufferHandler receiveHandler);

  /**
   * Closes this channel and removes it from its {@link IConnector}.
   * <p>
   * Once a channel has been closed it is <b>not</b> allowed anymore to call its {@link #sendBuffer(IBuffer)} method.
   */
  public void close();
}
