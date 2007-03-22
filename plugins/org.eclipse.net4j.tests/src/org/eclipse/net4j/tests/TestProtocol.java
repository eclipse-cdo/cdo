package org.eclipse.net4j.tests;

import org.eclipse.net4j.transport.IBuffer;

import org.eclipse.internal.net4j.transport.Protocol;

import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public final class TestProtocol extends Protocol
{
  private CountDownLatch counter;

  public TestProtocol(CountDownLatch counter)
  {
    this.counter = counter;
  }

  public String getType()
  {
    return TestProtocolFactory.TYPE;
  }

  public void handleBuffer(IBuffer buffer)
  {
    System.out.println("BUFFER ARRIVED");
    buffer.release();
    counter.countDown();
  }
}