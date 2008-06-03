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
package org.eclipse.net4j.http.internal.server;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.http.common.IHTTPConnector;
import org.eclipse.net4j.http.internal.common.HTTPConnector;
import org.eclipse.net4j.http.internal.server.bundle.OM;
import org.eclipse.net4j.http.server.IHTTPAcceptor;
import org.eclipse.net4j.http.server.INet4jTransportServlet;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.Worker;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IRandomizer;

import org.eclipse.internal.net4j.acceptor.Acceptor;

import org.eclipse.spi.net4j.InternalConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptor extends Acceptor implements IHTTPAcceptor, INet4jTransportServlet.RequestHandler
{
  public static final int DEFAULT_CONNECTOR_ID_LENGTH = 32;

  public static final int DEFAULT_MAX_IDLE_TIME = 30 * 60 * 1000; // 30 minutes

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPAcceptor.class);

  private IRandomizer randomizer;

  private INet4jTransportServlet servlet;

  private int connectorIDLength = DEFAULT_CONNECTOR_ID_LENGTH;

  private int maxIdleTime = DEFAULT_MAX_IDLE_TIME;

  private Map<String, HTTPServerConnector> httpConnectors = new HashMap<String, HTTPServerConnector>();

  private Worker cleaner = new Worker()
  {
    @Override
    protected void work(WorkContext context) throws Exception
    {
      int pause = cleanIdleConnectors();
      context.nextWork(pause);
    }
  };

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
    if (this.servlet != null)
    {
      this.servlet.setRequestHandler(this);
    }
  }

  public int getConnectorIDLength()
  {
    return connectorIDLength;
  }

  public void setConnectorIDLength(int connectorIDLength)
  {
    this.connectorIDLength = connectorIDLength;
  }

  public int getMaxIdleTime()
  {
    return maxIdleTime;
  }

  public void setMaxIdleTime(int maxIdleTime)
  {
    this.maxIdleTime = maxIdleTime;
  }

  public IHTTPConnector[] getHTTPConnectors()
  {
    List<IHTTPConnector> result = new ArrayList<IHTTPConnector>();
    for (IConnector acceptedConnector : getAcceptedConnectors())
    {
      IHTTPConnector connector = (IHTTPConnector)acceptedConnector;
      result.add(connector);
    }

    return result.toArray(new IHTTPConnector[result.size()]);
  }

  public IHTTPConnector[] handleList(String connectorID)
  {
    if (StringUtil.isEmpty(connectorID))
    {
      return getHTTPConnectors();
    }

    return new IHTTPConnector[] { httpConnectors.get(connectorID) };
  }

  public IHTTPConnector handleConnect(String userID)
  {
    String connectorID = createConnectorID(userID);
    HTTPServerConnector connector = createServerConnector();
    prepareConnector(connector);
    connector.setConnectorID(connectorID);
    connector.setUserID(userID);
    addConnector(connector);
    connector.activate();

    return connector;
  }

  public void handleDisonnect(String connectorID)
  {
    HTTPConnector connector = httpConnectors.get(connectorID);
    if (connector == null)
    {
      throw new IllegalArgumentException("Invalid connectorID: " + connectorID);
    }

    connector.deactivate();
  }

  public void handleOperations(String connectorID, ExtendedDataInputStream in, ExtendedDataOutputStream out)
      throws IOException
  {
    HTTPServerConnector connector = httpConnectors.get(connectorID);
    if (connector == null)
    {
      throw new IllegalArgumentException("Invalid connectorID: " + connectorID);
    }

    connector.readInputOperations(in);
    connector.writeOutputOperations(out);
  }

  @Override
  public String toString()
  {
    return "HTTPAcceptor";
  }

  @Override
  public void addConnector(InternalConnector connector)
  {
    super.addConnector(connector);
    HTTPServerConnector httpConnector = (HTTPServerConnector)connector;
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
    checkState(maxIdleTime >= 100, "Constraint violated: maxIdleTime >= 100");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    cleaner.setDaemon(true);
    cleaner.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    cleaner.deactivate();
    super.doDeactivate();
  }

  protected String createConnectorID(String userID)
  {
    return randomizer.nextString(connectorIDLength, "0123456789ABCDEF");
  }

  protected HTTPServerConnector createServerConnector()
  {
    return new HTTPServerConnector(this);
  }

  protected int cleanIdleConnectors()
  {
    long now = System.currentTimeMillis();
    IConnector[] connectors = getAcceptedConnectors();
    if (TRACER.isEnabled())
    {
      TRACER.format("Checking {0} HTTP server connectors for idle time: {1,time}", connectors.length, new Date());
    }

    for (IConnector connector : connectors)
    {
      HTTPServerConnector serverConnector = (HTTPServerConnector)connector;
      long lastTraffic = serverConnector.getLastTraffic();
      long idleTime = now - lastTraffic;
      if (idleTime > maxIdleTime)
      {
        serverConnector.deactivate();
        OM.LOG.info("Deactivated idle HTTP server connector: " + serverConnector);
      }
    }

    return maxIdleTime;
  }
}
