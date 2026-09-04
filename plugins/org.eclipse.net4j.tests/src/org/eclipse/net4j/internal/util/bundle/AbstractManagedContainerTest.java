/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.util.bundle;

import org.eclipse.net4j.util.tests.AbstractOMTest;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class AbstractManagedContainerTest extends AbstractOMTest
{
  public void testPreparationFailureCanBeRetried()
  {
    ManagedContainer container = new ManagedContainer();
    AtomicInteger attempts = new AtomicInteger();

    AbstractBundle bundle = new AbstractBundle((AbstractPlatform)OMPlatform.INSTANCE, "test", getClass())
    {
      @Override
      public InputStream getInputStream(String path) throws IOException
      {
        if (attempts.getAndIncrement() == 0)
        {
          throw new IOException("first attempt"); //$NON-NLS-1$
        }

        return new ByteArrayInputStream("<plugin/>".getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
      }

      @Override
      public URL getBaseURL()
      {
        return null;
      }

      @Override
      public Iterator<Class<?>> getClasses()
      {
        return Collections.emptyIterator();
      }

      @Override
      public String getStateLocation()
      {
        return null;
      }

      @Override
      public Class<?> loadClass(String pluginID, String className) throws ClassNotFoundException
      {
        return Class.forName(className);
      }
    };

    try
    {
      try
      {
        bundle.prepareContainer(container);
        fail("The first preparation attempt must fail"); //$NON-NLS-1$
      }
      catch (RuntimeException expected)
      {
      }

      assertEquals(1, attempts.get());
      bundle.prepareContainer(container);
      assertEquals(2, attempts.get());

      bundle.prepareContainer(container);
      assertEquals(2, attempts.get());
    }
    finally
    {
      container.deactivate();
    }
  }

  public void testConcurrentSuccessfulPreparation() throws Exception
  {
    ManagedContainer container = new ManagedContainer();
    ControlledBundle bundle = new ControlledBundle(false);
    CountDownLatch secondAttemptStarted = new CountDownLatch(1);
    CountDownLatch secondAttemptReturned = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread firstThread = new Thread(() ->
    {
      try
      {
        bundle.prepareContainer(container);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    });

    Thread secondThread = new Thread(() ->
    {
      try
      {
        secondAttemptStarted.countDown();
        bundle.prepareContainer(container);
        secondAttemptReturned.countDown();
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    });

    try
    {
      firstThread.start();
      assertTrue(bundle.firstAttemptStarted.await(1, TimeUnit.SECONDS));
      secondThread.start();
      assertTrue(secondAttemptStarted.await(1, TimeUnit.SECONDS));
      assertFalse(secondAttemptReturned.await(0, TimeUnit.MILLISECONDS));
      bundle.releaseFirstAttempt.countDown();
      firstThread.join(1000);
      assertTrue(secondAttemptReturned.await(1, TimeUnit.SECONDS));
      assertEquals(1, bundle.attempts.get());
      bundle.prepareContainer(container);
      assertEquals(1, bundle.attempts.get());
      assertNull(failure.get());
    }
    finally
    {
      bundle.releaseFirstAttempt.countDown();
      firstThread.join(1000);
      secondThread.join(1000);
      container.deactivate();
    }
  }

  public void testConcurrentFailedPreparationCanBeRetried() throws Exception
  {
    ManagedContainer container = new ManagedContainer();
    ControlledBundle bundle = new ControlledBundle(true);
    CountDownLatch secondAttemptStarted = new CountDownLatch(1);
    CountDownLatch secondAttemptReturned = new CountDownLatch(1);
    AtomicBoolean firstFailureObserved = new AtomicBoolean();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread firstThread = new Thread(() ->
    {
      try
      {
        try
        {
          bundle.prepareContainer(container);
          fail("The first preparation must fail"); //$NON-NLS-1$
        }
        catch (RuntimeException expected)
        {
          firstFailureObserved.set(true);
        }
      }
      catch (AssertionError ex)
      {
        failure.set(ex);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    });

    Thread secondThread = new Thread(() ->
    {
      try
      {
        secondAttemptStarted.countDown();
        bundle.prepareContainer(container);
        secondAttemptReturned.countDown();
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    });

    try
    {
      firstThread.start();
      assertTrue(bundle.firstAttemptStarted.await(1, TimeUnit.SECONDS));
      secondThread.start();
      assertTrue(secondAttemptStarted.await(1, TimeUnit.SECONDS));
      assertFalse(secondAttemptReturned.await(0, TimeUnit.MILLISECONDS));
      assertEquals(1, bundle.attempts.get());
      bundle.releaseFirstAttempt.countDown();
      firstThread.join(1000);
      assertTrue(secondAttemptReturned.await(1, TimeUnit.SECONDS));
      assertTrue(firstFailureObserved.get());
      assertEquals(2, bundle.attempts.get());
      bundle.prepareContainer(container);
      assertEquals(2, bundle.attempts.get());
      assertNull(failure.get());
    }
    finally
    {
      bundle.releaseFirstAttempt.countDown();
      firstThread.join(1000);
      secondThread.join(1000);
      container.deactivate();
    }
  }

  private static final class ControlledBundle extends AbstractBundle
  {
    private final boolean failFirstAttempt;

    private final CountDownLatch firstAttemptStarted = new CountDownLatch(1);

    private final CountDownLatch releaseFirstAttempt = new CountDownLatch(1);

    private final AtomicInteger attempts = new AtomicInteger();

    ControlledBundle(boolean failFirstAttempt)
    {
      super((AbstractPlatform)OMPlatform.INSTANCE, "test", AbstractManagedContainerTest.class);
      this.failFirstAttempt = failFirstAttempt;
    }

    @Override
    public InputStream getInputStream(String path) throws IOException
    {
      int attempt = attempts.incrementAndGet();
      if (attempt == 1)
      {
        firstAttemptStarted.countDown();
        try
        {
          releaseFirstAttempt.await(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IOException(ex);
        }

        if (failFirstAttempt)
        {
          throw new IOException("first attempt"); //$NON-NLS-1$
        }
      }

      return new ByteArrayInputStream("<plugin/>".getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
    }

    @Override
    public URL getBaseURL()
    {
      return null;
    }

    @Override
    public Iterator<Class<?>> getClasses()
    {
      return Collections.emptyIterator();
    }

    @Override
    public String getStateLocation()
    {
      return null;
    }

    @Override
    public Class<?> loadClass(String pluginID, String className) throws ClassNotFoundException
    {
      return Class.forName(className);
    }
  }

}
