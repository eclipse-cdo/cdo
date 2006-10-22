package org.eclipse.net4j.transport;

/**
 * @author Eike Stepper
 */
public interface BufferPool extends BufferProvider
{
  public boolean evictOne();

  public int evict(int survivors);

  /**
   * @author Eike Stepper
   */
  public interface Introspection extends BufferProvider.Introspection
  {
    public int getPooledBuffers();
  }
}