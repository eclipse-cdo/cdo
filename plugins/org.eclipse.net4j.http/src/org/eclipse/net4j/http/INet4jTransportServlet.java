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
package org.eclipse.net4j.http;

import javax.servlet.Servlet;

/**
 * @author Eike Stepper
 */
public interface INet4jTransportServlet extends Servlet
{
  public static final int OPCODE_CONNECT = 1;

  public RequestHandler getRequestHandler();

  public void setRequestHandler(RequestHandler handler);

  /**
   * @author Eike Stepper
   */
  public interface RequestHandler
  {
    public void connectRequested(String userID);
  }
}
