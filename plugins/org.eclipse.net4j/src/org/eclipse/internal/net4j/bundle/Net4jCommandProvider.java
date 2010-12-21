package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;

import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public class Net4jCommandProvider implements CommandProvider
{
  public Net4jCommandProvider(BundleContext bundleContext)
  {
    bundleContext.registerService(CommandProvider.class.getName(), this, null);
  }

  public String getHelp()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("---Net4j commands---\n");
    buffer.append("\tacceptors list - list all active acceptors, their connectors and channels\n");
    buffer.append("\tconnectors list - list all active connectors and their channels\n");
    return buffer.toString();
  }

  public Object _acceptors(CommandInterpreter interpreter)
  {
    try
    {
      String cmd = interpreter.nextArgument();
      if ("list".equals(cmd))
      {
        acceptorsList(interpreter);
        return null;
      }

      interpreter.println(getHelp());
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  public Object _connectors(CommandInterpreter interpreter)
  {
    try
    {
      String cmd = interpreter.nextArgument();
      if ("list".equals(cmd))
      {
        connectorsList(interpreter);
        return null;
      }

      interpreter.println(getHelp());
    }
    catch (Exception ex)
    {
      interpreter.printStackTrace(ex);
    }

    return null;
  }

  protected void acceptorsList(CommandInterpreter interpreter) throws Exception
  {
    for (Object element : IPluginContainer.INSTANCE.getElements(AcceptorFactory.PRODUCT_GROUP))
    {
      if (element instanceof IAcceptor)
      {
        IAcceptor acceptor = (IAcceptor)element;
        interpreter.println(acceptor);
        for (IConnector connector : acceptor.getAcceptedConnectors())
        {
          interpreter.println("  " + connector);
          for (IChannel channel : connector.getChannels())
          {
            interpreter.println("    " + channel);
          }
        }
      }
    }
  }

  protected void connectorsList(CommandInterpreter interpreter) throws Exception
  {
    for (Object element : IPluginContainer.INSTANCE.getElements(ConnectorFactory.PRODUCT_GROUP))
    {
      if (element instanceof IConnector)
      {
        IConnector connector = (IConnector)element;
        interpreter.println(connector);
        for (IChannel channel : connector.getChannels())
        {
          interpreter.println("    " + channel);
        }
      }
    }
  }
}
