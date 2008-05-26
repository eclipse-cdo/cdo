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

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.http.INet4jTransportServlet;
import org.eclipse.net4j.internal.http.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOHandler;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.internal.net4j.buffer.Buffer;
import org.eclipse.internal.net4j.channel.InternalChannel;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Queue;

/**
 * @author Eike Stepper
 */
public class HTTPClientConnector extends HTTPConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPClientConnector.class);

  private String url;

  private HttpClient httpClient;

  private int maxIdleTime = UNKNOWN_MAX_IDLE_TIME;

  public HTTPClientConnector()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  public String getURL()
  {
    return url;
  }

  public void setURL(String url)
  {
    this.url = url;
  }

  public int getMaxIdleTime()
  {
    return maxIdleTime;
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("HTTPClientConnector[{0}]", getURL()); //$NON-NLS-1$
    }

    return MessageFormat.format("HTTPClientConnector[{1}@{0}]", getURL(), getUserID()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkArg(url, "url == null");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    httpClient = createHTTPClient();
    connect();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    httpClient = null;
    super.doDeactivate();
  }

  protected HttpClient createHTTPClient()
  {
    return new HttpClient();
  }

  protected PostMethod createHTTPMethod(String url)
  {
    return new PostMethod(url);
  }

  @Override
  protected void registerChannelWithPeer(final int channelID, final short channelIndex, final IProtocol protocol)
      throws ConnectorException
  {
    try
    {
      request(new IOHandler()
      {
        public void handleOut(ExtendedDataOutputStream out) throws IOException
        {
          out.writeByte(INet4jTransportServlet.OPCODE_OPEN_CHANNEL);
          out.writeString(getConnectorID());
          out.writeInt(channelID);
          out.writeShort(channelIndex);
          out.writeString(protocol.getType());
        }

        public void handleIn(ExtendedDataInputStream in) throws IOException
        {
          boolean ok = in.readBoolean();
          if (!ok)
          {
            throw new ConnectorException("Could not open channel");
          }
        }
      });
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (IOException ex)
    {
      throw new ConnectorException(ex);
    }
  }

  private void connect() throws IOException
  {
    request(new IOHandler()
    {
      public void handleOut(ExtendedDataOutputStream out) throws IOException
      {
        out.writeByte(INet4jTransportServlet.OPCODE_CONNECT);
        out.writeString(getUserID());
      }

      public void handleIn(ExtendedDataInputStream in) throws IOException
      {
        String connectorID = in.readString();
        maxIdleTime = in.readInt();

        setConnectorID(connectorID);
        leaveConnecting();
      }
    });
  }

  private void request(IOHandler handler) throws IOException, HttpException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream out = new ExtendedDataOutputStream(baos);
    handler.handleOut(out);
    out.flush();
    byte[] content = baos.toByteArray();
    PostMethod method = createHTTPMethod(url);
    method.setRequestEntity(new ByteArrayRequestEntity(content));

    httpClient.executeMethod(method);
    InputStream bodyInputStream = method.getResponseBodyAsStream();
    ExtendedDataInputStream in = new ExtendedDataInputStream(bodyInputStream);
    handler.handleIn(in);
    method.releaseConnection();
  }
}
