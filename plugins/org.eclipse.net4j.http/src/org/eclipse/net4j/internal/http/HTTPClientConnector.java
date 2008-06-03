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

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.http.internal.common.HTTPConnector;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOAdapter;
import org.eclipse.net4j.util.io.IOHandler;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.Worker;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class HTTPClientConnector extends HTTPConnector
{
  private String url;

  private HttpClient httpClient;

  private int maxIdleTime = UNKNOWN_MAX_IDLE_TIME;

  private int pollInterval = DEFAULT_POLL_INTERVAL;

  private long lastRequest = System.currentTimeMillis();

  private boolean requesting;

  private Worker poller = new Worker()
  {
    @Override
    protected void work(WorkContext context) throws Exception
    {
      boolean moreBuffers = tryOperationsRequest();
      context.nextWork(moreBuffers ? 0 : 1000);
    }
  };

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

  public int getPollInterval()
  {
    return pollInterval;
  }

  public void setPollInterval(int pollInterval)
  {
    this.pollInterval = pollInterval;
  }

  @Override
  public void multiplexChannel(IChannel channel)
  {
    super.multiplexChannel(channel);
    tryOperationsRequest();
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
    poller.setDaemon(true);
    poller.activate();
    httpClient = createHTTPClient();
    doConnect();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    doDisconnect();
    poller.deactivate();
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

  private void doConnect() throws IOException
  {
    request(new IOHandler()
    {
      public void handleOut(ExtendedDataOutputStream out) throws IOException
      {
        out.writeByte(OPCODE_CONNECT);
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

  private void doDisconnect() throws IOException
  {
    request(new IOAdapter()
    {
      @Override
      public void handleOut(ExtendedDataOutputStream out) throws IOException
      {
        out.writeByte(OPCODE_DISCONNECT);
        out.writeString(getConnectorID());
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

  private boolean tryOperationsRequest()
  {
    synchronized (poller)
    {
      if (requesting)
      {
        return false;
      }

      if (getOutputQueue().isEmpty() && System.currentTimeMillis() - lastRequest < pollInterval)
      {
        return false;
      }

      requesting = true;
    }

    try
    {
      final boolean moreOperations[] = { false };
      request(new IOHandler()
      {
        public void handleOut(ExtendedDataOutputStream out) throws IOException
        {
          out.writeByte(OPCODE_OPERATIONS);
          out.writeString(getConnectorID());
          moreOperations[0] = writeOutputOperations(out);
        }

        public void handleIn(ExtendedDataInputStream in) throws IOException
        {
          readInputOperations(in);
        }
      });

      lastRequest = System.currentTimeMillis();
      return moreOperations[0];
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      synchronized (poller)
      {
        requesting = false;
      }
    }
  }
}
