/*
 * Copyright (c) 2008-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.http.HTTPUtil;
import org.eclipse.net4j.internal.http.HTTPClientConnector;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public class HTTPTest extends AbstractTransportTest
{
  public HTTPTest()
  {
  }

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    HTTPUtil.prepareContainer(container);
    return container;
  }

  /**
   * Result: With the current implementation (HttpClient / Jetty) it's not possible to transfer request data before
   */
  public void _testRequestFlush() throws Exception
  {
    HttpClient client = new HttpClient();
    PostMethod method = new PostMethod("http://eike@localhost:8080/net4j/echotest"); //$NON-NLS-1$
    method.setRequestEntity(new RequestEntity()
    {
      @Override
      public long getContentLength()
      {
        return -1;
      }

      @Override
      public String getContentType()
      {
        return "application/octet-stream"; //$NON-NLS-1$
      }

      @Override
      public boolean isRepeatable()
      {
        return false;
      }

      @Override
      public void writeRequest(OutputStream out) throws IOException
      {
        int count = 10;
        out.write(count);
        for (int i = 0; i < count; i++)
        {
          send(out, i);
        }
      }

      private void send(OutputStream out, int b) throws IOException
      {
        try
        {
          msg("Writing " + b); //$NON-NLS-1$
          out.write(b);
          out.flush();
          Thread.sleep(1000);
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    client.executeMethod(method);
    InputStream responseBody = method.getResponseBodyAsStream();
    ExtendedDataInputStream in = new ExtendedDataInputStream(responseBody);
    int count = in.readInt();
    for (int i = 0; i < count; i++)
    {
      int b = in.readByte();
      assertEquals(i, b);

      long gap = in.readLong();
      msg("Gap: " + gap); //$NON-NLS-1$
    }

    method.releaseConnection();
  }

  public void test1() throws Exception
  {
    HTTPClientConnector connector = getHTTPConnector();
    TestSignalProtocol protocol = new TestSignalProtocol(connector);

    IntRequest request = new IntRequest(protocol, 305419896);
    int result = request.send();
    assertEquals(305419896, result);

    sleep(500);
    protocol.close();
    sleep(500);
    connector.deactivate();
  }

  private HTTPClientConnector getHTTPConnector()
  {
    return (HTTPClientConnector)Net4jUtil.getConnector(container, "http", //$NON-NLS-1$
        "http://localhost:8080/net4j"); //$NON-NLS-1$
  }
}
