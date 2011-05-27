/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.server;

import org.eclipse.net4j.http.common.IHTTPConnector;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import javax.servlet.Servlet;

import java.io.IOException;

/**
 * A {@link Servlet servlet} with a configured {@link RequestHandler request handler}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface INet4jTransportServlet extends Servlet
{
  public RequestHandler getRequestHandler();

  public void setRequestHandler(RequestHandler handler);

  /**
   * Call-back that handles the requests that arrive at a {@link INet4jTransportServlet servlet}.
   * 
   * @author Eike Stepper
   */
  public interface RequestHandler
  {
    public IHTTPConnector[] handleList(String connectorID);

    public IHTTPConnector handleConnect(String userID);

    public void handleDisonnect(String connectorID);

    public void handleOperations(String connectorID, ExtendedDataInputStream in, ExtendedDataOutputStream out)
        throws IOException;
  }
}
