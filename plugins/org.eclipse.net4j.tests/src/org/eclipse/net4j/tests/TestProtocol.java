package org.eclipse.net4j.tests;

import org.eclipse.net4j.IBuffer;

import org.eclipse.internal.net4j.Protocol;

import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public final class TestProtocol extends Protocol<CountDownLatch>
{
  public TestProtocol(CountDownLatch counter)
  {
    setInfraStructure(counter);
  }

  public String getType()
  {
    return ServerTestProtocolFactory.TYPE;
  }

  public void handleBuffer(IBuffer buffer)
  {
    System.out.println("BUFFER ARRIVED");
    buffer.release();
    if (getInfraStructure() != null)
    {
      getInfraStructure().countDown();
    }
  }
}