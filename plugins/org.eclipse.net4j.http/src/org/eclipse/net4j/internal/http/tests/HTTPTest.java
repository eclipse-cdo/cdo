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
package org.eclipse.net4j.internal.http.tests;

import org.eclipse.net4j.http.HTTPUtil;
import org.eclipse.net4j.http.IHTTPConnector;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class HTTPTest extends AbstractTransportTest
{
  public HTTPTest()
  {
  }

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    HTTPUtil.prepareContainer(container);
    return container;
  }

  public void test1() throws Exception
  {
    IHTTPConnector connector = getHTTPConnector();

  }

  private IHTTPConnector getHTTPConnector()
  {
    return (IHTTPConnector)container.getElement("org.eclipse.net4j.connectors", "http",
        "http://eike@localhost:8080/net4j");
  }
}
