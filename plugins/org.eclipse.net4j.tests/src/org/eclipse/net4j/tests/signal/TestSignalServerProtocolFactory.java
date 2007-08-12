package org.eclipse.net4j.tests.signal;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.internal.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class TestSignalServerProtocolFactory extends ServerProtocolFactory<TestSignalProtocol>
{
  public static final String TYPE = TestSignalProtocol.PROTOCOL_NAME;

  public TestSignalServerProtocolFactory()
  {
    super(TYPE);
  }

  public TestSignalProtocol create(String description) throws ProductCreationException
  {
    return new TestSignalProtocol();
  }
}