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
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.http.IHTTPAcceptor;
import org.eclipse.net4j.http.INet4jTransportServlet;
import org.eclipse.net4j.internal.http.bundle.OM;
import org.eclipse.net4j.internal.util.lifecycle.Worker;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.acceptor.Acceptor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptor extends Acceptor implements IHTTPAcceptor, INet4jTransportServlet.RequestHandler
{
  public static final boolean DEFAULT_START_SYNCHRONOUSLY = true;

  public static final long DEFAULT_SYNCHRONOUS_START_TIMEOUT = 2 * Worker.DEFAULT_TIMEOUT;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPAcceptor.class);

  private boolean startSynchronously = DEFAULT_START_SYNCHRONOUSLY;

  private long synchronousStartTimeout = DEFAULT_SYNCHRONOUS_START_TIMEOUT;

  private CountDownLatch startLatch;

  private int port = DEFAULT_PORT;

  public HTTPAcceptor()
  {
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public boolean isStartSynchronously()
  {
    return startSynchronously;
  }

  public void setStartSynchronously(boolean startSynchronously)
  {
    this.startSynchronously = startSynchronously;
  }

  public long getSynchronousStartTimeout()
  {
    return synchronousStartTimeout;
  }

  public void setSynchronousStartTimeout(long synchronousStartTimeout)
  {
    this.synchronousStartTimeout = synchronousStartTimeout;
  }

  public void connectRequested(String user)
  {
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("HTTPAcceptor[{0}]", port); //$NON-NLS-1$ 
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (startSynchronously)
    {
      startLatch = new CountDownLatch(1);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (startLatch != null)
    {
      if (!startLatch.await(synchronousStartTimeout, TimeUnit.MILLISECONDS))
      {
        startLatch = null;
        // IOUtil.closeSilent(serverSocketChannel);
        throw new IOException("Registration with selector timed out after " + synchronousStartTimeout + " millis");
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
  }
}
