package org.eclipse.net4j.transport;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface ITransportContainer extends IManagedContainer
{
  public short getBufferCapacity();

  public IBufferProvider getBufferProvider();

  public ExecutorService getExecutorService();

  public IAcceptor getAcceptor(String type, String description);

  public IConnector getConnector(String type, String description);
}
