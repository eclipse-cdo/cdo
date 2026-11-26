/*
 * Copyright (c) 2006-2012, 2020, 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.EntityRequest;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocolDeactivatedException;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.AsyncRequest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.StringRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * @author Eike Stepper
 */
public class SignalTest extends AbstractConfigTest
{
  public void testSignalStartupAfterProtocolDeactivation() throws Exception
  {
    TestSignalProtocol protocol = new TestSignalProtocol();
    protocol.close();

    try
    {
      new AsyncRequest(protocol, "unused").sendAsync(); //$NON-NLS-1$
      fail("Expected a local protocol deactivation exception"); //$NON-NLS-1$
    }
    catch (SignalProtocolDeactivatedException expected)
    {
      assertTrue(protocol.waitForSignals(0));
    }
  }

  public void testSignalStartupRacingWithProtocolDeactivation() throws Exception
  {
    startTransport();
    CountDownLatch providerCaptured = new CountDownLatch(1);
    CountDownLatch continueStartup = new CountDownLatch(1);
    StartupRaceProtocol protocol = new StartupRaceProtocol(getConnector(), providerCaptured, continueStartup);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    protocol.blockNextBufferProvider();

    Thread requestThread = new Thread(() -> {
      try
      {
        new AsyncRequest(protocol, "unused").sendAsync(); //$NON-NLS-1$
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Signal startup racing with deactivation"); //$NON-NLS-1$

    try
    {
      requestThread.start();
      assertTrue("Signal startup did not capture its buffer provider", providerCaptured.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      protocol.close();
      continueStartup.countDown();
      requestThread.join(1000);

      assertFalse("Signal startup remained blocked after deactivation", requestThread.isAlive()); //$NON-NLS-1$
      assertTrue(failure.get() instanceof SignalProtocolDeactivatedException);
      assertTrue("Signal startup left a stale registration", protocol.waitForSignals(0)); //$NON-NLS-1$
    }
    finally
    {
      continueStartup.countDown();
      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testProtocolDeactivationExceptionSkipsRegularSignalExceptionHandler() throws Exception
  {
    TestSignalProtocol protocol = new TestSignalProtocol();
    AtomicReference<Throwable> handled = new AtomicReference<>();
    TestExceptionSignal signal = new TestExceptionSignal(protocol, new SignalProtocolDeactivatedException(), handled, null, null);

    signal.run();

    assertNull("Expected protocol deactivation to skip the regular exception handler", handled.get()); //$NON-NLS-1$
    protocol.close();
  }

  public void testApplicationExceptionReachesHandlerDuringProtocolDeactivation() throws Exception
  {
    TestSignalProtocol protocol = new TestSignalProtocol();
    CountDownLatch executing = new CountDownLatch(1);
    CountDownLatch deactivated = new CountDownLatch(1);
    AtomicReference<Throwable> handled = new AtomicReference<>();
    IllegalStateException applicationFailure = new IllegalStateException("application failure"); //$NON-NLS-1$
    TestExceptionSignal signal = new TestExceptionSignal(protocol, applicationFailure, handled, executing, deactivated);
    Thread signalThread = new Thread(signal, "Signal exception/deactivation race"); //$NON-NLS-1$
    signalThread.setDaemon(true);

    try
    {
      signalThread.start();
      assertTrue("Signal execution did not start", executing.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      protocol.close();
      deactivated.countDown();

      signalThread.join(5000);
      assertFalse("Signal thread did not finish", signalThread.isAlive()); //$NON-NLS-1$
      assertSame(applicationFailure, handled.get());
    }
    finally
    {
      deactivated.countDown();
      protocol.close();
    }
  }

  public void testInteger() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      int data = 0x0a;
      int result = new IntRequest(protocol, data).send();
      assertEquals(data, result);
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testArray() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      byte[] data = TinyData.getBytes();
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testAsync() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      OMPlatform.INSTANCE.setDebugging(false);
      protocol = new TestSignalProtocol(getConnector());
      String data = TinyData.getText();
      for (int i = 0; i < 1000; i++)
      {
        msg("Loop " + i); //$NON-NLS-1$
        new AsyncRequest(protocol, data).sendAsync();
        String result = new StringRequest(protocol, data).send();
        assertEquals(data, result);
      }
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testCloseUnderlyingConnection() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      final ILifecycle lifecycle = protocol;

      config.closeUnderlyingConnection(getServerConnector());
      assertNoTimeout(() -> !lifecycle.isActive());
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testEntities() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      startTransport();
      protocol = new TestSignalProtocol(getConnector());
      BiConsumer<String, Entity> entityHandler = (name, entity) -> System.out.println(name + " --> " + entity);

      int result = new EntityRequest(protocol, entityHandler, "users", "eike", "rene", "ed").send();
      assertEquals(2, result);
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  /**
   * Holds signal startup after it captures transport infrastructure so the test can deactivate the protocol at a
   * deterministic point in the startup sequence.
   *
   * @author Eike Stepper
   */
  private static final class StartupRaceProtocol extends TestSignalProtocol
  {
    private final CountDownLatch providerCaptured;

    private final CountDownLatch continueStartup;

    private volatile boolean blockNextBufferProvider;

    public StartupRaceProtocol(IConnector connector, CountDownLatch providerCaptured, CountDownLatch continueStartup)
    {
      super(connector);
      this.providerCaptured = providerCaptured;
      this.continueStartup = continueStartup;
    }

    @Override
    public IBufferProvider getBufferProvider()
    {
      IBufferProvider bufferProvider = super.getBufferProvider();
      if (blockNextBufferProvider)
      {
        blockNextBufferProvider = false;
        providerCaptured.countDown();
        try
        {
          continueStartup.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      return bufferProvider;
    }

    public void blockNextBufferProvider()
    {
      blockNextBufferProvider = true;
    }
  }

  /**
   * Runs a selected exception through the public signal lifecycle and records regular exception-handler calls.
   *
   * @author Eike Stepper
   */
  private static final class TestExceptionSignal extends Indication
  {
    private final Exception exception;

    private final AtomicReference<Throwable> handled;

    private final CountDownLatch executing;

    private final CountDownLatch continueExecution;

    public TestExceptionSignal(TestSignalProtocol protocol, Exception exception, AtomicReference<Throwable> handled, CountDownLatch executing,
        CountDownLatch continueExecution)
    {
      super(protocol, (short)0);
      this.exception = exception;
      this.handled = handled;
      this.executing = executing;
      this.continueExecution = continueExecution;
    }

    @Override
    protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
    {
      if (executing != null)
      {
        executing.countDown();
        continueExecution.await();
      }

      throw exception;
    }

    @Override
    protected void indicating(ExtendedDataInputStream in) throws Exception
    {
      // The test overrides execute to inject the selected failure.
    }

    @Override
    protected void handleRunException(Throwable ex)
    {
      handled.set(ex);
    }
  }
}
