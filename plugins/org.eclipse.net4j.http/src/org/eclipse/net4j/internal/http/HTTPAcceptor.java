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

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.http.IHTTPAcceptor;
import org.eclipse.net4j.http.IHTTPConnector;
import org.eclipse.net4j.http.INet4jTransportServlet;
import org.eclipse.net4j.internal.http.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.security.IRandomizer;

import org.eclipse.internal.net4j.acceptor.Acceptor;
import org.eclipse.internal.net4j.channel.InternalChannel;
import org.eclipse.internal.net4j.connector.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptor extends Acceptor implements IHTTPAcceptor, INet4jTransportServlet.RequestHandler
{
  public static final int DEFAULT_CONNECTOR_ID_LENGTH = 32;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPAcceptor.class);

  private IRandomizer randomizer;

  private INet4jTransportServlet servlet;

  private int connectorIDLength = DEFAULT_CONNECTOR_ID_LENGTH;

  private Map<String, HTTPConnector> httpConnectors = new HashMap<String, HTTPConnector>();

  public HTTPAcceptor()
  {
  }

  public IRandomizer getRandomizer()
  {
    return randomizer;
  }

  public void setRandomizer(IRandomizer randomizer)
  {
    this.randomizer = randomizer;
  }

  public INet4jTransportServlet getServlet()
  {
    return servlet;
  }

  public void setServlet(INet4jTransportServlet servlet)
  {
    if (this.servlet != null)
    {
      this.servlet.setRequestHandler(null);
    }

    this.servlet = servlet;
    servlet.setRequestHandler(this);
  }

  public int getConnectorIDLength()
  {
    return connectorIDLength;
  }

  public void setConnectorIDLength(int connectorIDLength)
  {
    this.connectorIDLength = connectorIDLength;
  }

  public IHTTPConnector[] handleList(String connectorID)
  {
    if (StringUtil.isEmpty(connectorID))
    {
      List<IHTTPConnector> result = new ArrayList<IHTTPConnector>();
      for (IConnector acceptedConnector : getAcceptedConnectors())
      {
        IHTTPConnector connector = (IHTTPConnector)acceptedConnector;
        result.add(connector);
      }

      return result.toArray(new IHTTPConnector[result.size()]);
    }

    return new IHTTPConnector[] { httpConnectors.get(connectorID) };
  }

  public String handleConnect(String userID)
  {
    String connectorID = createConnectorID(userID);
    System.out.println("HELLO " + userID + " (" + connectorID + ")");

    HTTPServerConnector connector = new HTTPServerConnector(this);
    prepareConnector(connector);
    connector.setConnectorID(connectorID);
    connector.setUserID(userID);
    addConnector(connector);

    return connectorID;
  }

  public void handleOpenChannel(String connectorID, int channelID, short channelIndex, String protocolType)
  {
    HTTPConnector connector = httpConnectors.get(connectorID);
    if (connector == null)
    {
      throw new IllegalArgumentException("Invalid connectorID: " + connectorID);
    }

    InternalChannel channel = connector.createChannel(channelID, channelIndex, protocolType);
    if (channel != null)
    {
      channel.activate();
    }
  }

  @Override
  public String toString()
  {
    return "HTTPAcceptor";
  }

  @Override
  public void addConnector(Connector connector)
  {
    super.addConnector(connector);
    HTTPConnector httpConnector = (HTTPConnector)connector;
    httpConnectors.put(httpConnector.getConnectorID(), httpConnector);
  }

  @Override
  public void removeConnector(IConnector connector)
  {
    HTTPConnector httpConnector = (HTTPConnector)connector;
    httpConnectors.remove(httpConnector.getConnectorID());
    super.removeConnector(connector);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(randomizer, "randomizer");
    checkState(connectorIDLength > 0, "Constraint violated: connectorIDLength > 0");
  }

  protected String createConnectorID(String userID)
  {
    return randomizer.nextString(connectorIDLength, "0123456789ABCDEF");
  }
}
