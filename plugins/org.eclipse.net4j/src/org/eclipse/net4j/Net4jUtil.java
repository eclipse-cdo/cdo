/*
 * Copyright (c) 2007-2013, 2016, 2018, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocol2;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.buffer.BufferFactory;
import org.eclipse.internal.net4j.buffer.BufferPool;
import org.eclipse.internal.net4j.buffer.BufferPoolFactory;
import org.eclipse.internal.net4j.buffer.ExternalBuffer;
import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;

/**
 * A utility class with various static factory and convenience methods.
 *
 * @author Eike Stepper
 */
public final class Net4jUtil
{
  public static final String SCHEME_SEPARATOR = "://"; //$NON-NLS-1$

  public static final short DEFAULT_BUFFER_CAPACITY = 4096;

  /**
   * @since 4.18
   */
  public static final String LOCAL_ACCEPTOR_TYPE = "jvm";

  /**
   * @since 4.18
   */
  public static final String LOCAL_ACCEPTOR_DESCRIPTION = "local";

  private Net4jUtil()
  {
  }

  public static ExecutorService getExecutorService(IManagedContainer container)
  {
    return ExecutorServiceFactory.get(container);
  }

  public static IBufferProvider getBufferProvider(IManagedContainer container)
  {
    return BufferPoolFactory.get(container);
  }

  public static IAcceptor getAcceptor(IManagedContainer container, String type, String description)
  {
    return (IAcceptor)container.getElement(AcceptorFactory.PRODUCT_GROUP, type, description);
  }

  /**
   * @since 4.0
   */
  public static IConnector getConnector(IManagedContainer container, String type, String description, long timeout)
  {
    IConnector connector = (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, type, description);

    try
    {
      connector.waitForConnection(timeout);
    }
    catch (ConnectorException ex)
    {
      container.removeElement(ConnectorFactory.PRODUCT_GROUP, type, description);
      throw ex;
    }

    return connector;
  }

  public static IConnector getConnector(IManagedContainer container, String type, String description)
  {
    return getConnector(container, type, description, 10000L);
  }

  public static IConnector getConnector(IManagedContainer container, String description)
  {
    int pos = description.indexOf(SCHEME_SEPARATOR);
    if (pos <= 0)
    {
      throw new IllegalArgumentException("Connector type (scheme) missing: " + description); //$NON-NLS-1$
    }

    String factoryType = description.substring(0, pos);

    String connectorDescription = description.substring(pos + SCHEME_SEPARATOR.length());
    if (StringUtil.isEmpty(connectorDescription))
    {
      throw new IllegalArgumentException("Illegal connector description: " + description); //$NON-NLS-1$
    }

    return getConnector(container, factoryType, connectorDescription);
  }

  /**
   * @since 4.18
   */
  public static IAcceptor getLocalAcceptor(IManagedContainer container)
  {
    return getAcceptor(container, LOCAL_ACCEPTOR_TYPE, LOCAL_ACCEPTOR_DESCRIPTION);
  }

  /**
   * @since 4.18
   */
  public static IConnector getLocalConnector(IManagedContainer container)
  {
    return getConnector(container, LOCAL_ACCEPTOR_TYPE, LOCAL_ACCEPTOR_DESCRIPTION);
  }

  public static IBufferProvider createBufferFactory(short bufferCapacity)
  {
    return new BufferFactory(bufferCapacity);
  }

  public static IBufferProvider createBufferFactory()
  {
    return createBufferFactory(DEFAULT_BUFFER_CAPACITY);
  }

  public static IBufferPool createBufferPool(IBufferProvider factory)
  {
    return new BufferPool(factory);
  }

  public static IBufferPool createBufferPool(short bufferCapacity)
  {
    return createBufferPool(createBufferFactory(bufferCapacity));
  }

  public static IBufferPool createBufferPool()
  {
    return createBufferPool(createBufferFactory());
  }

  public static long getProvidedBuffers(IBufferProvider bufferProvider)
  {
    if (bufferProvider instanceof IBufferProvider.Introspection)
    {
      return ((IBufferProvider.Introspection)bufferProvider).getProvidedBuffers();
    }

    return -1L;
  }

  public static long getRetainedBuffers(IBufferProvider bufferProvider)
  {
    if (bufferProvider instanceof IBufferProvider.Introspection)
    {
      return ((IBufferProvider.Introspection)bufferProvider).getRetainedBuffers();
    }

    return -1L;
  }

  public static int getPooledBuffers(IBufferPool bufferPool)
  {
    if (bufferPool instanceof IBufferPool.Introspection)
    {
      return ((IBufferPool.Introspection)bufferPool).getPooledBuffers();
    }

    return -1;
  }

  /**
   * @since 2.0
   */
  public static ITransportConfig copyTransportConfig(ILifecycle lifecycle, ITransportConfig source)
  {
    return new TransportConfig(lifecycle, source.getReceiveExecutor(), source.getBufferProvider(), source.getProtocolProvider(), source.getNegotiator());
  }

  /**
   * @since 4.2
   */
  public static String getProtocolID(IProtocol<?> protocol)
  {
    if (protocol != null)
    {
      return protocol.getType();
    }

    return null;
  }

  /**
   * @since 4.2
   */
  public static int getProtocolVersion(IProtocol<?> protocol)
  {
    if (protocol instanceof IProtocol2)
    {
      return ((IProtocol2<?>)protocol).getVersion();
    }

    return IProtocol2.UNSPECIFIED_VERSION;
  }

  /**
   * Encodes the Net4j header of an outgoing buffer and returns a view of the complete message.
   * <p>
   * The payload is not copied. The returned view has position zero and a limit equal to the message length. The
   * original buffer's state, position, and limit are unchanged; the header is written through a duplicate view of its
   * backing storage.
   * <p>
   * The returned view shares storage with the original {@link IBuffer}. The sender must retain ownership of the buffer
   * until asynchronous transmission has completed or definitively failed. While the transport provider is using the
   * returned view, the buffer must not be released, cleared, reused, or otherwise mutated. This method neither transfers
   * ownership nor releases the buffer.
   *
   * @param buffer
   *          An outgoing buffer in {@link BufferState#PUTTING PUTTING} state.
   * @return A view of the encoded message, including its four-byte header.
   * @throws IllegalArgumentException
   *           If the buffer has an invalid channel ID, capacity, or payload length.
   * @throws IllegalStateException
   *           If the buffer is not in {@link BufferState#PUTTING PUTTING} state or violates its state invariants.
   * @since 4.23
   */
  public static ByteBuffer prepareForSending(IBuffer buffer)
  {
    if (buffer.getState() != BufferState.PUTTING)
    {
      throw new IllegalStateException("Buffer is not in PUTTING state: " + buffer.getState()); //$NON-NLS-1$
    }

    short channelID = buffer.getChannelID();
    if (channelID == IBuffer.NO_CHANNEL)
    {
      throw new IllegalArgumentException("NO_CHANNEL cannot be sent"); //$NON-NLS-1$
    }

    ByteBuffer byteBuffer = buffer.getByteBuffer();
    int capacity = buffer.getCapacity();
    int position = byteBuffer.position();
    int limit = byteBuffer.limit();
    int payloadSize = position - IBuffer.HEADER_SIZE;
    int messageSize = position;

    if (capacity < IBuffer.HEADER_SIZE || capacity > Short.MAX_VALUE || byteBuffer.capacity() != capacity)
    {
      throw new IllegalArgumentException("Invalid buffer capacity: " + capacity); //$NON-NLS-1$
    }

    if (limit > capacity //
        || position < IBuffer.HEADER_SIZE //
        || position > limit //
        || messageSize > Short.MAX_VALUE //
        || payloadSize > Short.MAX_VALUE - IBuffer.HEADER_SIZE)
    {
      throw new IllegalArgumentException("Invalid buffer position, limit, or payload length"); //$NON-NLS-1$
    }

    boolean eos = buffer.isEOS();
    if (payloadSize == 0 && !eos)
    {
      throw new IllegalArgumentException("A zero-length message must be EOS"); //$NON-NLS-1$
    }

    short encodedPayloadSize = eos ? (short)-payloadSize : (short)payloadSize;

    ByteBuffer header = byteBuffer.duplicate().order(ByteOrder.BIG_ENDIAN);
    header.putShort(IBuffer.CHANNEL_ID_POS, channelID);
    header.putShort(IBuffer.PAYLOAD_SIZE_POS, encodedPayloadSize);

    ByteBuffer message = byteBuffer.duplicate().order(ByteOrder.BIG_ENDIAN);
    message.position(0);
    message.limit(messageSize);
    return message;
  }

  /**
   * Wraps one complete encoded Net4j message without copying its payload.
   * <p>
   * The input's current position and limit delimit the message. Ownership of the release action transfers to this
   * method when it is invoked. If validation or wrapper construction fails, the action is invoked once before the
   * failure is rethrown.
   *
   * @param message
   *          Storage containing exactly one complete Net4j message between its current position and limit.
   * @param release
   *          The action that releases the supplied storage; may be <code>null</code> when no action is required.
   * @return A getting buffer over the original message storage.
   * @throws IllegalArgumentException
   *           If the message is incomplete, oversized, malformed, or has an invalid channel ID.
   * @since 4.23
   */
  public static IBuffer wrapBuffer(ByteBuffer message, Runnable release)
  {
    try
    {
      ByteBuffer view = message.slice().order(ByteOrder.BIG_ENDIAN);

      int messageSize = view.remaining();
      if (messageSize < IBuffer.HEADER_SIZE || messageSize > Short.MAX_VALUE)
      {
        throw new IllegalArgumentException("Invalid Net4j message size: " + messageSize); //$NON-NLS-1$
      }

      short channelID = view.getShort(IBuffer.CHANNEL_ID_POS);
      short encodedPayloadSize = view.getShort(IBuffer.PAYLOAD_SIZE_POS);
      if (channelID == IBuffer.NO_CHANNEL || encodedPayloadSize == Short.MIN_VALUE)
      {
        throw new IllegalArgumentException("Invalid Net4j message header"); //$NON-NLS-1$
      }

      boolean eos = encodedPayloadSize <= 0;

      int payloadSize = Math.abs(encodedPayloadSize);
      if (IBuffer.HEADER_SIZE + payloadSize != messageSize)
      {
        throw new IllegalArgumentException("Declared payload length does not match the message size"); //$NON-NLS-1$
      }

      view.position(IBuffer.HEADER_SIZE);
      return new ExternalBuffer(view, channelID, eos, release);
    }
    catch (RuntimeException | Error ex)
    {
      if (release != null)
      {
        try
        {
          release.run();
        }
        catch (RuntimeException | Error releaseException)
        {
          ex.addSuppressed(releaseException);
        }
      }

      throw ex;
    }
  }

  /**
   * @deprecated Containers created via {@link org.eclipse.net4j.util.container.ContainerUtil#createInitializedContainer()} or
   *             {@link org.eclipse.net4j.util.om.OMPlatform#createManagedContainer()} are initialized automatically. Advanced callers
   *             configuring a raw container can use {@link org.eclipse.net4j.util.om.OMBundle#prepareContainer(IManagedContainer)}.
   */
  @Deprecated
  public static void prepareContainer(IManagedContainer container)
  {
    OMPlatform.INSTANCE.bundle("org.eclipse.net4j.util", ContainerUtil.class).prepareContainer(container); //$NON-NLS-1$
    OM.BUNDLE.prepareContainer(container);
  }
}
