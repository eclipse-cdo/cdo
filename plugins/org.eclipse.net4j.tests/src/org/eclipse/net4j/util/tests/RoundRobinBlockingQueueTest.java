/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.collection.RoundRobinBlockingQueue;

import org.eclipse.spi.net4j.Channel;

import java.util.concurrent.BlockingQueue;

/**
 * @author Eike Stepper
 */
public class RoundRobinBlockingQueueTest extends AbstractOMTest
{
  public void testRoundRobinBlockingQueue() throws Exception
  {
    BlockingQueue<IChannel> queue = new RoundRobinBlockingQueue<IChannel>();

    Channel[] channels = new Channel[3];

    for (int i = 0; i < channels.length; i++)
    {
      Channel c = new Channel();
      c.setID((short)i);
      channels[i] = c;
    }

    assertEquals(true, queue.isEmpty());
    assertNull(queue.peek());
    assertNull(queue.poll());

    // Order will be 0000...1111...2222...
    for (int i = 0; i < channels.length; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        queue.put(channels[i]);
      }
    }

    for (int i = 0; i < 30; i++)
    {
      IChannel peek1 = queue.peek();
      IChannel peek2 = queue.peek();
      assertSame(peek1, peek2);

      IChannel poll = queue.poll();
      // The order should be 012012012012...
      assertEquals(i % 3, poll.getID());
      assertSame(peek1, poll);
    }

    assertEquals(true, queue.isEmpty());
    assertNull(queue.peek());
    assertNull(queue.poll());
  }
}
