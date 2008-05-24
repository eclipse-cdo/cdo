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

import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.http.IHTTPConnector;
import org.eclipse.net4j.http.INet4jTransportServlet;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

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
public class HTTPClientConnector extends HTTPConnector implements IHTTPConnector
{
  private String url;

  private HttpClient httpClient;

  private String connectorID;

  public HTTPClientConnector()
  {
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  public String getConnectorID()
  {
    return connectorID;
  }

  public String getURL()
  {
    return url;
  }

  public void setURL(String url)
  {
    this.url = url;
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
    httpClient = new HttpClient();
    connect();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    httpClient = null;
    super.doDeactivate();
  }

  private void connect() throws IOException, HttpException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ExtendedDataOutputStream out = new ExtendedDataOutputStream(baos);
    out.writeByte(INet4jTransportServlet.OPCODE_CONNECT);
    out.writeString(getUserID());
    out.flush();
    byte[] content = baos.toByteArray();

    PostMethod method = new PostMethod(url);
    method.setRequestEntity(new ByteArrayRequestEntity(content));

    httpClient.executeMethod(method);
    InputStream bodyInputStream = method.getResponseBodyAsStream();
    ExtendedDataInputStream in = new ExtendedDataInputStream(bodyInputStream);
    connectorID = in.readString();
    method.releaseConnection();
  }
}
