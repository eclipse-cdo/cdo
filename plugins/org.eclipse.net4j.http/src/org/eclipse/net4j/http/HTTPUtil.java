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

import org.eclipse.net4j.internal.http.HTTPAcceptorFactory;
import org.eclipse.net4j.internal.http.HTTPConnectorFactory;
import org.eclipse.net4j.internal.http.RandomizerInjector;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class HTTPUtil
{
  private HTTPUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new HTTPAcceptorFactory());
    container.registerFactory(new HTTPConnectorFactory());
    container.addPostProcessor(new RandomizerInjector());
  }

  public static IHTTPAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (IHTTPAcceptor)container
        .getElement(HTTPAcceptorFactory.PRODUCT_GROUP, HTTPAcceptorFactory.TYPE, description);
  }

  public static IHTTPConnector getConnector(IManagedContainer container, String description)
  {
    return (IHTTPConnector)container.getElement(HTTPConnectorFactory.PRODUCT_GROUP, HTTPConnectorFactory.TYPE,
        description);
  }
}
