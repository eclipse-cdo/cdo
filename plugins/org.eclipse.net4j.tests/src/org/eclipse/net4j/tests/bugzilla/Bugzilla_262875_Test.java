/*
 * Copyright (c) 2010-2012, 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    David Bonneau - initial API and implementation
 *    Andre Dietisheim - maintenance
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.IErrorHandler;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.nio.BufferUnderflowException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author David Bonneau
 */
public class Bugzilla_262875_Test extends AbstractOMTest
{
  /**
   * The length of the metadata sent in a buffer: channelID + payloadSize + correlationID + signalID
   */
  private static final short BUFFER_METADATA_LENTGH = IBuffer.HEADER_SIZE + Integer.BYTES + Short.BYTES;

  private IManagedContainer container;

  private IConnector connector;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    JVMUtil.prepareContainer(container);
    container.registerFactory(new TestProtocol.Factory());
    container.activate();

    JVMUtil.getAcceptor(container, "default");
    connector = JVMUtil.getConnector(container, "default");
  }

  @Override
  protected void doTearDown() throws Exception
  {
    connector.close();
    container.deactivate();
    super.doTearDown();
  }

  /**
   * Tests if a buffer underflow exception occurs if the data sent in a request exactly matches the capacity of a
   * buffer.
   *
   * @throws Exception
   *           the exception
   */
  public void testGivenDataMatchesBufferLengthThenBufferUnderflowException() throws Exception
  {
    final AtomicBoolean failed = new AtomicBoolean(false);
    final CountDownLatch latch = new CountDownLatch(1);
    IErrorHandler oldErrorHandler = Worker.setGlobalErrorHandler(new IErrorHandler()
    {
      @Override
      public void handleError(Throwable t)
      {
        t.printStackTrace();
        if (t instanceof BufferUnderflowException)
        {
          failed.set(true);
        }

        latch.countDown();
      }
    });

    try
    {
      TestProtocol protocol = new TestProtocol();
      protocol.open(connector);

      short bufferCapacity = protocol.getBufferProvider().getBufferCapacity();
      new TestProtocol.Request(protocol, bufferCapacity - BUFFER_METADATA_LENTGH).send();

      latch.await(DEFAULT_TIMEOUT_EXPECTED, TimeUnit.MILLISECONDS);
      assertEquals(false, failed.get());
    }
    finally
    {
      Worker.setGlobalErrorHandler(oldErrorHandler);
    }
  }

  /**
   * @author David Bonneau
   */
  private static final class TestProtocol extends SignalProtocol<Object>
  {
    private static final String NAME = "TEST_PROTOCOL";

    private static final short SIGNAL_ID = 10;

    public TestProtocol()
    {
      super(NAME);
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      switch (signalID)
      {
      case SIGNAL_ID:
        return new Indication(this);
      }

      return super.createSignalReactor(signalID);
    }

    /**
     * @author David Bonneau
     */
    private static final class Request extends RequestWithConfirmation<Boolean>
    {
      private int requestNumOfBytes;

      public Request(SignalProtocol<?> protocol, int requestNumOfBytes)
      {
        super(protocol, SIGNAL_ID);
        this.requestNumOfBytes = requestNumOfBytes;
      }

      @Override
      protected void requesting(ExtendedDataOutputStream out) throws Exception
      {
        for (int i = 0; i < requestNumOfBytes; ++i)
        {
          out.writeByte(i);
        }

        // delay completion
        Thread.sleep(100);
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in) throws Exception
      {
        return in.readBoolean();
      }
    }

    /**
     * @author David Bonneau
     */
    private static final class Indication extends IndicationWithResponse
    {
      public Indication(SignalProtocol<?> protocol)
      {
        super(protocol, SIGNAL_ID);
      }

      @Override
      protected void indicating(ExtendedDataInputStream in) throws Exception
      {
        System.out.println("indicating");
      }

      @Override
      protected void responding(ExtendedDataOutputStream out) throws Exception
      {
        out.writeBoolean(true);
      }
    }

    /**
     * @author David Bonneau
     */
    private static final class Factory extends ServerProtocolFactory
    {
      public Factory()
      {
        super(NAME);
      }

      @Override
      public Object create(String description) throws ProductCreationException
      {
        return new TestProtocol();
      }
    }
  }
}
