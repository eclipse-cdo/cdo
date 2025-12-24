/*
 * Copyright (c) 2007-2012, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.concurrent.ISynchronizer;
import org.eclipse.net4j.util.concurrent.SynchronizingCorrelator;

import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class SynchronizingCorrelatorTest extends AbstractOMTest
{
  public void testPutConsumerFirst() throws Exception
  {
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<String, Boolean>();
    final CountDownLatch correlationEstablished = new CountDownLatch(1);
    final Boolean[] result = { false };

    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike"); //$NON-NLS-1$
        correlationEstablished.countDown();

        result[0] = eike.get(5000);
        msg("RESULT: " + result[0]); //$NON-NLS-1$
      }
    };

    consumer.start();
    await(correlationEstablished);

    correlator.put("eike", true, DEFAULT_TIMEOUT); //$NON-NLS-1$
    consumer.join(DEFAULT_TIMEOUT);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testPutConsumerFirst1000() throws Exception
  {
    for (int i = 0; i < 1000; i++)
    {
      testPutConsumerFirst();
    }
  }

  public void testBlockingPutConsumerFirst() throws Exception
  {
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<String, Boolean>();
    final CountDownLatch correlationEstablished = new CountDownLatch(1);
    final Boolean[] result = { false };

    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike"); //$NON-NLS-1$
        correlationEstablished.countDown();

        result[0] = eike.get(5000);
        msg("RESULT: " + result[0]); //$NON-NLS-1$
      }
    };

    consumer.start();
    await(correlationEstablished);

    boolean consumed = correlator.put("eike", true, 1000); //$NON-NLS-1$
    msg("Consumed: " + consumed); //$NON-NLS-1$
    assertEquals(true, consumed);

    consumer.join(1000);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testBlockingPutConsumerFirst1000() throws Exception
  {
    for (int i = 0; i < 1000; i++)
    {
      testBlockingPutConsumerFirst();
    }
  }

  public void _testPutProducerFirst() throws Exception
  {
    final Boolean[] result = { false };
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<>();
    correlator.put("eike", true, DEFAULT_TIMEOUT); //$NON-NLS-1$

    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike"); //$NON-NLS-1$
        result[0] = eike.get(5000);
        msg("RESULT: " + result[0]); //$NON-NLS-1$
      }
    };

    consumer.start();
    Thread.sleep(10);

    consumer.join(100);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void _testPutProducerFirst10() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      _testPutProducerFirst();
    }
  }

  public void testBlockingPutProducerFirst() throws Exception
  {
    final Boolean[] result = { false };
    final SynchronizingCorrelator<String, Boolean> correlator = new SynchronizingCorrelator<>();
    boolean consumed = correlator.put("eike", true, 50); //$NON-NLS-1$
    msg("Consumed: " + consumed); //$NON-NLS-1$
    assertEquals(false, consumed);

    final Thread consumer = new Thread()
    {
      @Override
      public void run()
      {
        ISynchronizer<Boolean> eike = correlator.correlate("eike"); //$NON-NLS-1$
        result[0] = eike.get(5000);
        msg("RESULT: " + result[0]); //$NON-NLS-1$
      }
    };

    consumer.start();
    Thread.sleep(10);

    consumer.join(1000);
    assertEquals(Boolean.TRUE, result[0]);
  }

  public void testBlockingPutProducerFirst10() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      testBlockingPutProducerFirst();
    }
  }
}
