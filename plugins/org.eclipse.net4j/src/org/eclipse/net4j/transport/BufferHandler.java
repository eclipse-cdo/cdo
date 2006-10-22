package org.eclipse.net4j.transport;

/**
 * @author Eike Stepper
 */
public interface BufferHandler
{
  /**
   * Handles a {@link Buffer} and optionally releases it. The implementor of
   * this method takes over the ownership of the buffer. Care must be taken to
   * properly {@link Buffer#release() release} the buffer if the ownership is
   * not explicitely passed to some further party.
   * <p>
   */
  public void handleBuffer(Buffer buffer);
}