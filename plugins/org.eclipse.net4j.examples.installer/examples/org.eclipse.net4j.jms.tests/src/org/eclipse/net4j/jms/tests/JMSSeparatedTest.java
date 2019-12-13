/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.jms.JMSInitialContext;
import org.eclipse.net4j.jms.admin.IJMSAdmin;
import org.eclipse.net4j.jms.admin.JMSAdminUtil;
import org.eclipse.net4j.jms.internal.server.Server;
import org.eclipse.net4j.jms.server.IStore;
import org.eclipse.net4j.jms.server.JMSServerUtil;
import org.eclipse.net4j.jms.server.jdbc.JDBCUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

public class JMSSeparatedTest
{
  public static void main(String[] args) throws Exception
  {
    try
    {
      Context context = init();
      ConnectionFactory connectionFactory = (ConnectionFactory)context.lookup("net4j.jms.ConnectionFactory"); //$NON-NLS-1$
      Destination destination = (Destination)context.lookup("StockTopic"); //$NON-NLS-1$

      Connection connection = connectionFactory.createConnection();
      Session session = connection.createSession(true, 0);

      MessageProducer publisher = session.createProducer(destination);
      MessageConsumer subscriber1 = session.createConsumer(destination);
      MessageConsumer subscriber2 = session.createConsumer(destination);
      subscriber1.setMessageListener(new MessageLogger("subscriber1")); //$NON-NLS-1$
      subscriber2.setMessageListener(new MessageLogger("subscriber2")); //$NON-NLS-1$

      connection.start();

      publisher.send(session.createObjectMessage("Message 1")); //$NON-NLS-1$
      publisher.send(session.createObjectMessage("Message 2")); //$NON-NLS-1$
      publisher.send(session.createObjectMessage("Message 3")); //$NON-NLS-1$
      publisher.send(session.createObjectMessage("Message 4")); //$NON-NLS-1$

      session.commit();
    }
    finally
    {
      ConcurrencyUtil.sleep(500);
      Server.INSTANCE.deactivate();
    }
  }

  private static Context init() throws Exception
  {
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);

    initServer();
    return initClient();
  }

  private static void initServer() throws Exception
  {
    IDBAdapter.REGISTRY.put(EmbeddedDerbyAdapter.NAME, new EmbeddedDerbyAdapter());
    IStore store = JDBCUtil.getStore();
    Server.INSTANCE.setStore(store);
    Server.INSTANCE.activate();

    IManagedContainer serverContainer = new ManagedContainer();
    Net4jUtil.prepareContainer(serverContainer);
    TCPUtil.prepareContainer(serverContainer);
    JMSServerUtil.prepareContainer(serverContainer);

    TCPUtil.getAcceptor(serverContainer, null);
  }

  private static Context initClient() throws NamingException
  {
    IManagedContainer clientContainer = new ManagedContainer();
    Net4jUtil.prepareContainer(clientContainer);
    TCPUtil.prepareContainer(clientContainer);

    IConnector connector = TCPUtil.getConnector(clientContainer, "localhost"); //$NON-NLS-1$

    IJMSAdmin admin = JMSAdminUtil.createAdmin(connector);
    admin.createQueue("StockQueue"); //$NON-NLS-1$
    admin.createTopic("StockTopic"); //$NON-NLS-1$

    return new JMSInitialContext(clientContainer);
  }

  /**
   * @author Eike Stepper
   */
  private static final class MessageLogger implements MessageListener
  {
    private String name;

    public MessageLogger(String name)
    {
      this.name = name;
    }

    @Override
    public void onMessage(Message message)
    {
      try
      {
        Object object = ((ObjectMessage)message).getObject();
        IOUtil.OUT().println("\n------> MESSAGE for " + name + ": " + object + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        message.acknowledge();
      }
      catch (JMSException ex)
      {
        IOUtil.print(ex);
      }
    }
  }
}
