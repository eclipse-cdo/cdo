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

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author David Bonneau
 */
public class Bugzilla262875_Test extends AbstractOMTest
{
  public void testBufferUnderflowException() throws Exception
  {
    IManagedContainer container = IPluginContainer.INSTANCE;
    container.registerFactory(new TestProtocolFactory());
    JVMUtil.getAcceptor(container, "default");
    IConnector connector = JVMUtil.getConnector(container, "default");
    TestProtocol protocol = new TestProtocol("TEST_PROTOCOL");
    connector.openChannel(protocol);

    TestRequest request = new TestRequest(protocol, (short)10, 4086);
    request.send();
  }

  private class TestProtocolFactory extends ServerProtocolFactory
  {
    public TestProtocolFactory()
    {
      super("TEST_PROTOCOL");
    }

    public Object create(String description) throws ProductCreationException
    {
      return new TestProtocol("TEST_PROTOCOL");
    }

  }

  private class TestProtocol extends SignalProtocol<Object>
  {
    public TestProtocol(String type)
    {
      super(type);
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      switch (signalID)
      {
      case (short)10:
        return new TestIndication(this, signalID);
      default:
        break;
      }
      return super.createSignalReactor(signalID);
    }
  }

  private class TestRequest extends RequestWithConfirmation<Boolean>
  {
    private int value = 0;

    public TestRequest(SignalProtocol<?> protocol, short id, int value)
    {
      super(protocol, id);
      this.value = value;
    }

    @Override
    protected void requesting(ExtendedDataOutputStream out) throws Exception
    {
      for (int i = 0; i < value; ++i)
      {
        out.writeByte(0);
      }
      Thread.sleep(30);
    }

    @Override
    protected Boolean confirming(ExtendedDataInputStream in) throws Exception
    {
      return in.readBoolean();
    }
  }

  private class TestIndication extends IndicationWithResponse
  {

    public TestIndication(SignalProtocol<?> protocol, short id)
    {
      super(protocol, id);
    }

    @Override
    protected void indicating(ExtendedDataInputStream in) throws Exception
    {
      System.out.println("receive ");
    }

    @Override
    protected void responding(ExtendedDataOutputStream out) throws Exception
    {
      out.writeBoolean(true);

    }
  }
}
