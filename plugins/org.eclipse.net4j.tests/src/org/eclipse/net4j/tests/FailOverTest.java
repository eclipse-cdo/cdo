/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.signal.failover.NOOPFailOverStrategy;
import org.eclipse.net4j.signal.failover.RetryFailOverStrategy;
import org.eclipse.net4j.tests.signal.IntFailRequest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;

/**
 * @author Eike Stepper
 */
public class FailOverTest extends AbstractProtocolTest
{
  public FailOverTest()
  {
  }

  public void testFailingBefore() throws Exception
  {
    int data = 0x0a;
    IConnector connector = startTransport();
    IFailOverStrategy failOverStrategy = new NOOPFailOverStrategy(connector);
    TestSignalProtocol protocol = new TestSignalProtocol(failOverStrategy);

    // Simulate a disconnect from the server.
    IAcceptor acceptor = getAcceptor();
    acceptor.close();

    // Exception HERE
    IntRequest request = new IntRequest(protocol, data);
    int result = request.send();
    assertEquals(data, result);
  }

  public void testFailingBeforeAndRetry() throws Exception
  {
    int data = 0x0a;
    IConnector connector = startTransport();
    IFailOverStrategy failOverStrategy = new RetryFailOverStrategy(connector);
    TestSignalProtocol protocol = new TestSignalProtocol(failOverStrategy);

    // Simulate a disconnect from the server.
    IAcceptor acceptor = getAcceptor();
    acceptor.close();

    // new Thread()
    // {
    // @Override
    // public void run()
    // {
    // try
    // {
    // sleep(1000L);
    // restartContainer();
    // getAcceptor();
    // }
    // catch (Exception ex)
    // {
    // throw WrappedException.wrap(ex);
    // }
    // };
    // }.start();

    // Exception HERE
    IntRequest request = new IntRequest(protocol, data);
    int result = request.send();
    assertEquals(data, result);
  }

  public void testFailingDuring() throws Exception
  {
    int data = 0x0a;
    IConnector connector = startTransport();
    IFailOverStrategy failOverStrategy = new RetryFailOverStrategy(connector);
    TestSignalProtocol protocol = new TestSignalProtocol(failOverStrategy);

    // Exception HERE
    IntRequest request = new IntRequest(protocol, data);

    // Simulate a disconnect from the server.
    IAcceptor acceptor = getAcceptor();
    acceptor.close();

    int result = request.send();
    assertEquals(data, result);
  }

  public void testFailingDuring2() throws Exception
  {
    int data = 0x0a;
    IConnector connector = startTransport();
    IFailOverStrategy failOverStrategy = new RetryFailOverStrategy(connector);
    TestSignalProtocol protocol = new TestSignalProtocol(failOverStrategy);

    // Exception HERE
    int result = new IntFailRequest(protocol, data).send(1000);
    assertEquals(data, result);
  }
}
