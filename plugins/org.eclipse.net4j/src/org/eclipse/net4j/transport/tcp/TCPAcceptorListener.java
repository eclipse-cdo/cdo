package org.eclipse.net4j.transport.tcp;

/**
 * @author Eike Stepper
 */
public interface TCPAcceptorListener
{
  public void notifyConnectorAccepted(TCPAcceptor acceptor, TCPConnector connector);
}