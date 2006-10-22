package org.eclipse.net4j.transport;

/**
 * @author Eike Stepper
 */
public interface BufferProvider
{
  public short getBufferCapacity();

  public Buffer provideBuffer();

  public void retainBuffer(Buffer buffer);

  /**
   * @author Eike Stepper
   */
  public interface Introspection
  {
    public long getProvidedBuffers();

    public long getRetainedBuffers();
  }
}