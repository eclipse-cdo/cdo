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
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.IRandomizer;

import org.eclipse.internal.net4j.acceptor.Acceptor;

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

  public String connectRequested(String userID)
  {
    String connectorID = createConnectorID(userID);
    System.out.println("HELLO " + userID + " (" + connectorID + ")");

    HTTPServerConnector connector = new HTTPServerConnector(this, connectorID);
    prepareConnector(connector);
    connector.setUserID(userID);
    addConnector(connector);

    return connectorID;
  }

  @Override
  public String toString()
  {
    return "HTTPAcceptor";
  }

  protected String createConnectorID(String userID)
  {
    return randomizer.nextString(connectorIDLength, "0123456789ABCDEF");
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(randomizer, "randomizer");
    checkState(connectorIDLength > 0, "Constraint violated: connectorIDLength > 0");
  }
}
