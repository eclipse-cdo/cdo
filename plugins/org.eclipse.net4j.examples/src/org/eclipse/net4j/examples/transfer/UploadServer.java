/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.examples.transfer;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.internal.examples.bundle.OM;
import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class UploadServer implements UploadProtocol
{
  public static final String FOLDER_NAME = "/temp"; //$NON-NLS-1$

  public static void main(String[] args) throws Exception
  {
    // Send all traces and logs to the console
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    // Use this container to create and wire the components
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    container.registerFactory(new ProtocolFactory());
    container.activate();

    try
    {
      // Start an acceptor
      IAcceptor acceptor = (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", "tcp", "0.0.0.0:2036"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      OM.LOG.info("Accepting connections: " + acceptor); //$NON-NLS-1$

      new File(FOLDER_NAME).mkdirs();
      OM.LOG.info("Upload folder: " + FOLDER_NAME); //$NON-NLS-1$

      System.out.println("Press any key to shutdown"); //$NON-NLS-1$
      while (System.in.read() == -1)
      {
        Thread.sleep(200);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ProtocolFactory extends ServerProtocolFactory
  {
    public ProtocolFactory()
    {
      super(PROTOCOL_NAME);
    }

    @Override
    public Object create(String description) throws ProductCreationException
    {
      return new SignalProtocol<Object>(PROTOCOL_NAME)
      {
        @Override
        protected SignalReactor createSignalReactor(short signalID)
        {
          switch (signalID)
          {
          case UPLOAD_SIGNAL_ID:
            return new UploadIndication(this);

          default:
            return super.createSignalReactor(signalID);
          }
        }
      };
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class UploadIndication extends IndicationWithMonitoring
  {
    private boolean replaced;

    public UploadIndication(SignalProtocol<Object> protocol)
    {
      super(protocol, UPLOAD_SIGNAL_ID);
    }

    @Override
    protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
    {
      long size = in.readLong();
      String fileName = in.readString();
      File file = new File(FOLDER_NAME, fileName);
      replaced = file.exists();
      System.out.println((replaced ? "Replacing " : "Storing ") + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$

      monitor.begin((int)size);
      BufferedOutputStream out = null;

      try
      {
        out = new BufferedOutputStream(new FileOutputStream(file));
        while (size != 0L)
        {
          int chunk = BUFFER_SIZE;
          if (size < BUFFER_SIZE)
          {
            chunk = (int)size;
          }

          byte[] buffer = in.readByteArray();
          out.write(buffer);

          monitor.worked(chunk);
          size -= chunk;
        }
      }
      finally
      {
        monitor.done();
        if (out != null)
        {
          out.close();
        }
      }
    }

    @Override
    protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
    {
      out.writeBoolean(replaced);
    }
  }
}
