package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.buffer.IBuffer;

/**
 * @author Eike Stepper
 */
public final class QueuedBuffer
{
  private IBuffer buffer;

  private long channelCount;

  public QueuedBuffer(IBuffer buffer, long channelCount)
  {
    this.buffer = buffer;
    this.channelCount = channelCount;
  }

  public IBuffer getBuffer()
  {
    return buffer;
  }

  public long getChannelCount()
  {
    return channelCount;
  }
}