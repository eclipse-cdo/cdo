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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
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
    BufferedReader reader = req.getReader();
    String op = reader.readLine();
    if ("connect".equals(op))
    {
      handleConnect(reader, req, resp);
    }
  }

  protected void handleConnect(BufferedReader reader, HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
  {
    String user = reader.readLine();
    requestHandler.connectRequested(user);
  }
}
