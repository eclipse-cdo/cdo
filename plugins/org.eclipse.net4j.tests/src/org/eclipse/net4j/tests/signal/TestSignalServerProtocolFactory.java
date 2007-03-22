package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.internal.net4j.transport.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class TestSignalServerProtocolFactory extends ServerProtocolFactory<TestSignalProtocol>
{
  public TestSignalServerProtocolFactory()
  {
    super(TestSignalProtocol.TYPE);
  }

  public TestSignalProtocol create(String description) throws ProductCreationException
  {
    return new TestSignalProtocol();
  }
}