package org.eclipse.internal.net4j;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class Net4jTransportInjector implements IElementProcessor
{
  public Object process(IManagedContainer container, String productGroup, String factoryType, final String description,
      final Object element)
  {
    if (element instanceof Acceptor)
    {
      Acceptor acceptor = (Acceptor)element;
      if (acceptor.getBufferProvider() == null)
      {
        acceptor.setBufferProvider(getBufferProvider(container));
      }

      if (acceptor.getReceiveExecutor() == null)
      {
        acceptor.setReceiveExecutor(getExecutorService(container));
      }

      if (acceptor.getFactoryRegistry() == null)
      {
        acceptor.setFactoryRegistry(container.getFactoryRegistry());
      }
    }
    else if (element instanceof Connector)
    {
      Connector connector = (Connector)element;
      if (connector.getBufferProvider() == null)
      {
        connector.setBufferProvider(getBufferProvider(container));
      }

      if (connector.getReceiveExecutor() == null)
      {
        connector.setReceiveExecutor(getExecutorService(container));
      }

      if (connector.getFactoryRegistry() == null)
      {
        connector.setFactoryRegistry(container.getFactoryRegistry());
      }
    }

    return element;
  }

  public BufferProvider getBufferProvider(IManagedContainer container)
  {
    return (BufferProvider)container.getElement(BufferProviderFactory.PRODUCT_GROUP, BufferProviderFactory.TYPE, null);
  }

  public ExecutorService getExecutorService(IManagedContainer container)
  {
    return (ExecutorService)container.getElement(ExecutorServiceFactory.PRODUCT_GROUP, ExecutorServiceFactory.TYPE,
        null);
  }
}