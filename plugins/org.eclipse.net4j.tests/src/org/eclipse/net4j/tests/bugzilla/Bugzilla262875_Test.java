/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    David Bonneau - initial API and implementation
 *    Andre Dietisheim - maintenance
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.Net4jUtil;
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
public class Bugzilla262875_Test extends AbstractOMTest
{
  private static final long EXCEPTION_WAIT_TIMEOUT = 10000;

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

  public void testBufferUnderflowException() throws Exception
  {
    final AtomicBoolean failed = new AtomicBoolean(false);
    final CountDownLatch latch = new CountDownLatch(1);
    IErrorHandler oldErrorHandler = Worker.setGlobalErrorHandler(new IErrorHandler()
    {
      public void handleError(Throwable t)
      {
        t.printStackTrace();
        failed.set(t instanceof BufferUnderflowException);
        latch.countDown();
      }
    });

    try
    {
      TestProtocol protocol = new TestProtocol();
      protocol.open(connector);

      new TestProtocol.Request(protocol, Net4jUtil.DEFAULT_BUFFER_CAPACITY - 10).send();
      // TestProtocol.Request request = new TestProtocol.Request(protocol, Net4jUtil.DEFAULT_BUFFER_CAPACITY);

      latch.await(EXCEPTION_WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
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

    private static final short TEST_PROTOCOL_SIGNALID = 10;

    public TestProtocol()
    {
      super(NAME);
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      switch (signalID)
      {
      case TEST_PROTOCOL_SIGNALID:
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
        super(protocol, TEST_PROTOCOL_SIGNALID);
        this.requestNumOfBytes = requestNumOfBytes;
      }

      @Override
      protected void requesting(ExtendedDataOutputStream out) throws Exception
      {
        for (int i = 0; i < requestNumOfBytes; ++i)
        {
          out.writeByte(i);
        }
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
        super(protocol, TEST_PROTOCOL_SIGNALID);
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

      public Object create(String description) throws ProductCreationException
      {
        return new TestProtocol();
      }
    }
  }
}
