/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.signal.failover.RetryFailOverStrategy;
import org.eclipse.net4j.tests.signal.IntFailRequest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.TestSignalClientProtocolFactory;
import org.eclipse.net4j.tests.signal.TestSignalServerProtocolFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class FailOverTest extends AbstractTransportTest
{
  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new TestSignalServerProtocolFactory());
    container.registerFactory(new TestSignalClientProtocolFactory());
    return container;
  }

  public void testFailingBefore() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalClientProtocolFactory.TYPE, null);

    // Simulate a disconnect from the server.
    LifecycleUtil.deactivate(getAcceptor());

    int data = 0x0a;
    IFailOverStrategy failOverStrategy = new RetryFailOverStrategy(getConnector());

    // Exception HERE
    IntRequest request = new IntRequest(channel, data);

    int result = failOverStrategy.send(request);
    assertEquals(data, result);
  }

  public void testFailingDuring() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalClientProtocolFactory.TYPE, null);

    int data = 0x0a;
    IFailOverStrategy failOverStrategy = new RetryFailOverStrategy(getConnector());

    // Exception HERE
    IntRequest request = new IntRequest(channel, data);

    // Simulate a disconnect from the server.
    LifecycleUtil.deactivate(getAcceptor());

    int result = failOverStrategy.send(request);
    assertEquals(data, result);
  }

  public void testFailingDuring2() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalClientProtocolFactory.TYPE, null);

    int data = 0x0a;
    IFailOverStrategy failOverStrategy = new RetryFailOverStrategy(getConnector());

    // Exception HERE
    IntFailRequest request = new IntFailRequest(channel, data);

    int result = failOverStrategy.send(request, 1000);
    assertEquals(data, result);
  }
}
