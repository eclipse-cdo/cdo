/*
 * Copyright (c) 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.internal.ws.bundle.OM;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A utility class with static convenience methods.
 *
 * @author Eike Stepper
 */
public final class WSUtil
{
  public static final String FACTORY_TYPE = "ws"; //$NON-NLS-1$

  private WSUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }

  public static IWSAcceptor getAcceptor(IManagedContainer container, String acceptorName)
  {
    return (IWSAcceptor)container.getElement(WSAcceptorFactory.PRODUCT_GROUP, FACTORY_TYPE, acceptorName);
  }

  public static IWSConnector getConnector(IManagedContainer container, String description)
  {
    return (IWSConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, FACTORY_TYPE, description);
  }

  public static IWSConnector getConnector(IManagedContainer container, URI serviceURI, String acceptorName)
  {
    String description = getConnectorDescription(serviceURI, acceptorName);
    return getConnector(container, description);
  }

  public static String getConnectorDescription(String serviceURI, String acceptorName) throws URISyntaxException
  {
    return getConnectorDescription(new URI(serviceURI), acceptorName);
  }

  public static String getConnectorDescription(URI serviceURI, String acceptorName)
  {
    String string = serviceURI.toString();
    if (!string.endsWith("/")) //$NON-NLS-1$
    {
      string += "/"; //$NON-NLS-1$

      try
      {
        serviceURI = new URI(string);
      }
      catch (URISyntaxException ex)
      {
        // This can't realistically happen.
        throw new RuntimeException(ex);
      }
    }

    URI uri = serviceURI.resolve(IWSConnector.ACCEPTOR_NAME_PREFIX + acceptorName);
    return uri.getAuthority() + uri.getPath();
  }
}
