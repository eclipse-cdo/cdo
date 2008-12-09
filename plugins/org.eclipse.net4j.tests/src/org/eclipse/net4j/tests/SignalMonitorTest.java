/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
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
  public static final String PROTOCOL_TYPE = "protocol";

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
                  monitor.begin(101);

                  try
                  {
                    in.readBoolean();
                    monitor.worked(1);

                    for (int i = 0; i < 100; i++)
                    {
                      // if (i == 60)
                      // {
                      // Thread.sleep(5000);
                      // }

                      Thread.sleep(100);
                      monitor.worked(1);
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
                  monitor.begin(1);

                  try
                  {
                    out.writeBoolean(true);
                    monitor.worked(1);
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
        monitor.begin(1);

        try
        {
          out.writeBoolean(true);
          monitor.worked(1);
        }
        finally
        {
          monitor.done();
        }
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        monitor.begin(1);

        try
        {
          boolean result = in.readBoolean();
          monitor.worked(1);
          return result;
        }
        finally
        {
          monitor.done();
        }

      }
    };

    request.send(4000L, new TestMonitor());
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
    public void begin(int totalWork)
    {
      super.begin(totalWork);
      System.out.println("totalWork: " + getTotalWork());
    }

    @Override
    public void worked(double work)
    {
      super.worked(work);
      System.out.println("work: " + getWork());
    }
  }
}
