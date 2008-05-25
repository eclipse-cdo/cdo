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

import org.eclipse.net4j.http.INet4jTransportServlet;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.internal.net4j.acceptor.AcceptorFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Net4jTransportServlet extends HttpServlet implements INet4jTransportServlet
{
  private static final long serialVersionUID = 1L;

  private RequestHandler requestHandler;

  public Net4jTransportServlet()
  {
  }

  public RequestHandler getRequestHandler()
  {
    return requestHandler;
  }

  public void setRequestHandler(RequestHandler requestHandler)
  {
    this.requestHandler = requestHandler;
  }

  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    doPost(req, resp);
  }

  @Override
  protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    handleRequest(req, resp);
  }

  protected void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    if (requestHandler == null)
    {
      throw new ServletException("No request handler installed");
    }

    ServletInputStream servletInputStream = req.getInputStream();
    ExtendedDataInputStream in = new ExtendedDataInputStream(servletInputStream);

    ServletOutputStream servletOutputStream = resp.getOutputStream();
    ExtendedDataOutputStream out = new ExtendedDataOutputStream(servletOutputStream);

    int opcode = servletInputStream.read();
    switch (opcode)
    {
    case OPCODE_CONNECT:
      handleConnect(in, out);
      break;
    }

    out.flush();
  }

  protected void handleConnect(ExtendedDataInputStream in, ExtendedDataOutputStream out) throws ServletException,
      IOException
  {
    String userID = in.readString();
    String connectorID = requestHandler.connectRequested(userID);
    out.writeString(connectorID);
  }

  /**
   * @author Eike Stepper
   */
  public static class ContainerAware extends Net4jTransportServlet
  {
    private static final String ACCEPTORS_GROUP = AcceptorFactory.PRODUCT_GROUP;

    private static final String HTTP_TYPE = HTTPAcceptorFactory.TYPE;

    private static final long serialVersionUID = 1L;

    private HTTPAcceptor acceptor;

    public ContainerAware()
    {
    }

    @Override
    public void init() throws ServletException
    {
      super.init();

      acceptor = (HTTPAcceptor)IPluginContainer.INSTANCE.getElement(ACCEPTORS_GROUP, HTTP_TYPE, null);
      if (acceptor == null)
      {
        throw new ServletException("Acceptor not found");
      }

      acceptor.setServlet(this);
      setRequestHandler(acceptor);
    }

    @Override
    public void destroy()
    {
      setRequestHandler(null);
      acceptor.setServlet(null);
      acceptor = null;
      super.destroy();
    }
  }
}
