/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.internal.util.concurrent.SynchronizingCorrelator;
import org.eclipse.net4j.util.concurrent.ISynchronizer;

/**
 * @author Eike Stepper
 */
public class SynchronizingCorrelatorTest extends AbstractOMTest
{
  public void testPutConsumerFirst() throws Exception
  {
    final Boolean[] result = { false };
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<String, Boolean>();
    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike");
        result[0] = eike.get(5000);
        System.out.println("RESULT: " + result[0]);
      }
    };

    consumer.start();
    sleep(50);

    correlator.put("eike", true);
    consumer.join(100);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testPutConsumerFirst100() throws Exception
  {
    for (int i = 0; i < 100; i++)
    {
      testPutConsumerFirst();
    }
  }

  public void testBlockingPutConsumerFirst() throws Exception
  {
    final Boolean[] result = { false };
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<String, Boolean>();
    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike");
        result[0] = eike.get(5000);
        System.out.println("RESULT: " + result[0]);
      }
    };

    consumer.start();
    Thread.sleep(10);

    boolean consumed = correlator.put("eike", true, 1000);
    System.out.println("Consumed: " + consumed);
    assertTrue(consumed);

    consumer.join(100);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testBlockingPutConsumerFirst100() throws Exception
  {
    for (int i = 0; i < 100; i++)
    {
      testBlockingPutConsumerFirst();
    }
  }

  public void testPutProducerFirst() throws Exception
  {
    final Boolean[] result = { false };
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<String, Boolean>();
    correlator.put("eike", true);

    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike");
        result[0] = eike.get(5000);
        System.out.println("RESULT: " + result[0]);
      }
    };

    consumer.start();
    Thread.sleep(10);

    consumer.join(100);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testPutProducerFirst100() throws Exception
  {
    for (int i = 0; i < 100; i++)
    {
      testPutProducerFirst();
    }
  }

  public void testBlockingPutProducerFirst() throws Exception
  {
    final Boolean[] result = { false };
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<String, Boolean>();
    boolean consumed = correlator.put("eike", true, 50);
    System.out.println("Consumed: " + consumed);
    assertFalse(consumed);

    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike");
        result[0] = eike.get(5000);
        System.out.println("RESULT: " + result[0]);
      }
    };

    consumer.start();
    Thread.sleep(10);

    consumer.join(50);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testBlockingPutProducerFirst50() throws Exception
  {
    for (int i = 0; i < 50; i++)
    {
      testBlockingPutProducerFirst();
    }
  }
}
