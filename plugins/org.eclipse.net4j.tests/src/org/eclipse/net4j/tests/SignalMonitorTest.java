/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class SignalMonitorTest extends AbstractTransportTest
{
  public static final String PROTOCOL_TYPE = "protocol"; //$NON-NLS-1$

  public static final short SIGNAL_ID = 1;

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new ServerProtocolFactory(PROTOCOL_TYPE)
    {
      public Object create(String description) throws ProductCreationException
      {
        return new SignalProtocol<Object>(PROTOCOL_TYPE)
        {
          @Override
          protected SignalReactor createSignalReactor(short signalID)
          {
            switch (signalID)
            {
            case SIGNAL_ID:
              return new IndicationWithMonitoring(this, SIGNAL_ID)
              {
                @Override
                protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
                {
                  monitor.begin(1 + 100);

                  try
                  {
                    in.readBoolean();
                    monitor.worked();

                    for (int i = 0; i < 100; i++)
                    {
                      sleep(100);
                      monitor.worked();
                    }
                  }
                  finally
                  {
                    monitor.done();
                  }
                }

                @Override
                protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
                {
                  monitor.begin();

                  try
                  {
                    out.writeBoolean(true);
                    monitor.worked();
                  }
                  finally
                  {
                    monitor.done();
                  }
                }
              };

            default:
              return super.createSignalReactor(signalID);
            }
          }
        };
      }
    });

    return container;
  }

  public void testMonitorProgress() throws Exception
  {
    startTransport();
    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());

    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<Boolean>(protocol, SIGNAL_ID)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
      {
        monitor.begin();

        try
        {
          out.writeBoolean(true);
          monitor.worked();
        }
        finally
        {
          monitor.done();
        }
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        monitor.begin();

        try
        {
          boolean result = in.readBoolean();
          monitor.worked();
          return result;
        }
        finally
        {
          monitor.done();
        }
      }
    };

    request.send(4000L, new TestMonitor());
    sleep(200);
    protocol.close();
  }

  /**
   * @author Eike Stepper
   */
  public static final class ClientProtocol extends SignalProtocol<Object>
  {
    public ClientProtocol()
    {
      super(PROTOCOL_TYPE);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestMonitor extends Monitor
  {
    @Override
    public OMMonitor begin(double totalWork)
    {
      super.begin(totalWork);
      System.out.println("totalWork: " + getTotalWork()); //$NON-NLS-1$
      return this;
    }

    @Override
    public void worked(double work)
    {
      super.worked(work);
      System.out.println("work: " + getWork()); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends SignalMonitorTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static final class SSL extends SignalMonitorTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return true;
    }
  }
}
