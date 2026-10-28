/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.IErrorHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An incoming Net4j message backed by externally owned storage.
 * <p>
 * This buffer is a one-shot incoming view over storage owned by a transport or another external source. Its byte buffer
 * is positioned at the first payload byte while retaining the complete framed message in its limit. It is not pooled,
 * cannot be reset or written, and its {@link #release()} operation invokes the supplied release action at most once.
 * <p>
 * The state check protects calls made through this object after release; it cannot revoke a {@link ByteBuffer} reference
 * already returned by {@link #getByteBuffer()}. The caller must ensure that no such reference is still in use when it
 * releases the buffer. The release callback runs synchronously on the thread that wins the release race; if it throws,
 * the buffer remains released and the callback is not retried.
 *
 * @author Eike Stepper
 */
public final class ExternalBuffer implements IBuffer
{
  /**
   * External frame storage whose lease ends only when {@link #release()} runs the callback.
   */
  private final ByteBuffer byteBuffer;

  private final short channelID;

  private final boolean eos;

  /**
   * Returns the external storage lease; guarded by {@link #released} for at-most-once invocation.
   */
  private final Runnable releaseAction;

  /**
   * Makes release terminal and idempotent when multiple owners race to relinquish the view.
   */
  private final AtomicBoolean released = new AtomicBoolean();

  /**
   * Volatile so access checks observe release performed by another thread.
   */
  private volatile BufferState state = BufferState.GETTING;

  private IErrorHandler errorHandler;

  /**
   * Creates a getting buffer over one validated complete Net4j frame. The constructor adopts the
   * storage lease represented by {@code releaseAction}; callers must eventually release this buffer
   * to return that storage to its owner.
   *
   * @param message
   *          A view over the complete message, positioned at the first payload byte with the frame's
   *          end as its limit.
   * @param channelID
   *          The signed channel ID decoded from the message header.
   * @param eos
   *          Whether the message header encodes end of stream.
   * @param releaseAction
   *          The action that releases the backing storage, or {@code null} when no action is required.
   */
  public ExternalBuffer(ByteBuffer message, short channelID, boolean eos, Runnable releaseAction)
  {
    byteBuffer = message;
    this.channelID = channelID;
    this.eos = eos;
    this.releaseAction = releaseAction;
  }

  @Override
  @Deprecated
  public IBufferProvider getBufferProvider()
  {
    return null;
  }

  @Override
  public short getChannelID()
  {
    return channelID;
  }

  @Override
  public short getCapacity()
  {
    return (short)byteBuffer.capacity();
  }

  @Override
  public BufferState getState()
  {
    return state;
  }

  @Override
  public ByteBuffer startGetting(SocketChannel socketChannel) throws IllegalStateException, IOException
  {
    throw unsupported("startGetting"); //$NON-NLS-1$
  }

  @Override
  public ByteBuffer startPutting(short channelID)
  {
    throw unsupported("startPutting"); //$NON-NLS-1$
  }

  @Override
  public boolean write(SocketChannel socketChannel) throws IllegalStateException, IOException
  {
    throw unsupported("write"); //$NON-NLS-1$
  }

  @Override
  public void flip()
  {
    throw unsupported("flip"); //$NON-NLS-1$
  }

  @Override
  public ByteBuffer getByteBuffer()
  {
    checkGetting();
    return byteBuffer;
  }

  @Override
  public boolean isEOS()
  {
    return eos;
  }

  @Override
  public void setEOS(boolean eos)
  {
    throw unsupported("setEOS"); //$NON-NLS-1$
  }

  @Override
  public boolean isCCAM()
  {
    return false;
  }

  @Override
  public void setCCAM(boolean ccam)
  {
    throw unsupported("setCCAM"); //$NON-NLS-1$
  }

  @Override
  public void release()
  {
    if (released.compareAndSet(false, true))
    {
      state = BufferState.RELEASED;
      errorHandler = null;

      if (releaseAction != null)
      {
        releaseAction.run();
      }
    }
  }

  @Override
  public void clear()
  {
    throw unsupported("clear"); //$NON-NLS-1$
  }

  @Override
  @SuppressWarnings("deprecation")
  public String formatContent(boolean showHeader)
  {
    checkGetting();
    ByteBuffer view = byteBuffer.duplicate();

    if (showHeader)
    {
      view.position(0);
    }
    else
    {
      view.position(HEADER_SIZE);
    }

    StringBuilder builder = new StringBuilder();

    while (view.hasRemaining())
    {
      HexUtil.appendHex(builder, view.get() & 0xFF);
      builder.append(' ');
    }

    return builder.toString();
  }

  @Override
  public IErrorHandler getErrorHandler()
  {
    return errorHandler;
  }

  @Override
  public void setErrorHandler(IErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  @Override
  public void compact()
  {
    throw unsupported("compact"); //$NON-NLS-1$
  }

  @Override
  public int getPosition()
  {
    checkGetting();
    return byteBuffer.position();
  }

  @Override
  public void setPosition(int position)
  {
    checkGetting();
    byteBuffer.position(position);
  }

  @Override
  public int getLimit()
  {
    checkGetting();
    return byteBuffer.limit();
  }

  @Override
  public void setLimit(int limit)
  {
    checkGetting();
    byteBuffer.limit(limit);
  }

  @Override
  public boolean hasRemaining()
  {
    checkGetting();
    return byteBuffer.hasRemaining();
  }

  @Override
  public byte get()
  {
    checkGetting();
    return byteBuffer.get();
  }

  @Override
  public void get(byte[] dst)
  {
    checkGetting();
    byteBuffer.get(dst);
  }

  @Override
  public short getShort()
  {
    checkGetting();
    return byteBuffer.getShort();
  }

  @Override
  public int getInt()
  {
    checkGetting();
    return byteBuffer.getInt();
  }

  @Override
  public String getString()
  {
    checkGetting();
    return BufferUtil.getString(byteBuffer);
  }

  @Override
  public void put(byte b)
  {
    throw unsupported("put"); //$NON-NLS-1$
  }

  @Override
  public void put(byte[] src, int offset, int length)
  {
    throw unsupported("put"); //$NON-NLS-1$
  }

  @Override
  public void putShort(short value)
  {
    throw unsupported("putShort"); //$NON-NLS-1$
  }

  private void checkGetting()
  {
    if (state != BufferState.GETTING)
    {
      throw new IllegalStateException("Buffer is " + state); //$NON-NLS-1$
    }
  }

  private UnsupportedOperationException unsupported(String operation)
  {
    return new UnsupportedOperationException(operation + " is not supported by an incoming buffer"); //$NON-NLS-1$
  }
}
