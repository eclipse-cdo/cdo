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
package org.eclipse.net4j.http.server;

import org.eclipse.net4j.acceptor.IAcceptor;

/**
 * @author Eike Stepper
 */
public interface IHTTPAcceptor extends IAcceptor
{
  public INet4jTransportServlet getServlet();

  public void setServlet(INet4jTransportServlet servlet);
}
