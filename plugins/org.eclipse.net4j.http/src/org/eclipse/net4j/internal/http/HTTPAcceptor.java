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
import org.eclipse.net4j.util.concurrent.NonBlockingIntCounter;

import org.eclipse.internal.net4j.acceptor.Acceptor;

/**
 * @author Eike Stepper
 */
public class HTTPAcceptor extends Acceptor implements IHTTPAcceptor, INet4jTransportServlet.RequestHandler
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPAcceptor.class);

  private INet4jTransportServlet servlet;

  private transient NonBlockingIntCounter lastConnectorID = new NonBlockingIntCounter();

  public HTTPAcceptor()
  {
  }

  public INet4jTransportServlet getServlet()
  {
    return servlet;
  }

  public void setServlet(INet4jTransportServlet servlet)
  {
    this.servlet = servlet;
  }

  public int connectRequested(String userID)
  {
    int connectorID = lastConnectorID.getValue() + 1;
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

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkArg(servlet, "servlet == null");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    servlet.setRequestHandler(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    servlet.setRequestHandler(null);
    super.doDeactivate();
  }
}
