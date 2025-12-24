/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.concurrent.NonFairReentrantLock;
import org.eclipse.net4j.util.concurrent.QueueRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class NonFairReentrantLockTest
{
  public static void main(String[] args) throws Exception
  {
    final QueueRunner eventQueue = new QueueRunner();
    eventQueue.activate();

    final Lock lock = new NonFairReentrantLock()
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean isOwner(Thread thread, Thread owner)
      {
        if (super.isOwner(thread, owner))
        {
          return true;
        }

        // State state = owner.getState();
        // if (state == State.WAITING)
        // {
        // StackTraceElement[] stackTrace = owner.getStackTrace();
        //
        // }

        return thread == eventQueue.getWorkerThread();
      }
    };

    /**
     * @author Eike Stepper
     */
    class Event implements Runnable
    {
      CountDownLatch latch = new CountDownLatch(1);

      @Override
      public void run()
      {
        System.out.println("event");
        lock.lock();

        try
        {
          System.out.println("forked");
        }
        finally
        {
          lock.unlock();
          latch.countDown();
        }
      }
    }

    Thread[] threads = new Thread[20];
    for (int i = 0; i < threads.length; i++)
    {
      threads[i] = new Thread()
      {
        @Override
        public void run()
        {
          lock.lock();

          try
          {
            nested();
          }
          finally
          {
            lock.unlock();
          }
        }

        private void nested()
        {
          lock.lock();
          System.out.println("nested");

          try
          {
            Event event = new Event();
            eventQueue.addWork(event);

            try
            {
              event.latch.await();
            }
            catch (Throwable ex)
            {
              //$FALL-THROUGH$
            }
          }
          finally
          {
            lock.unlock();
          }
        }
      };

      threads[i].start();
    }

    for (int i = 0; i < threads.length; i++)
    {
      threads[i].join();
    }

    eventQueue.deactivate();
  }
}
